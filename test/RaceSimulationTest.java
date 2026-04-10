package test;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import arkham.racing.model.environment.*;
import arkham.racing.service.PitStopManager;
import arkham.racing.service.RaceSimulation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class RaceSimulationTest {

    private Team team1;
    private Team team2;

    @BeforeEach
    void setUp() {
        // собираем тестовые команды
        team1 = setupReadyTeam("Team 1", 90.0, 90.0, 80.0);
        team2 = setupReadyTeam("Team 2", 85.0, 85.0, 75.0);
    }

    private Team setupReadyTeam(String name, double pilotSkill, double accuracy, double qualification) {
        Team team = new Team(name, 1000000.0);

        Engine engine = new Engine("V8", 0, 800, 150, "V8");
        Chassis chassis = new Chassis("Base", 0, 200);
        Transmission transmission = new Transmission("Manual", 0, 0.9, "Manual");
        Suspension suspension = new Suspension("Sport", 0, 80, "Sport");
        Aerodynamics aerodynamics = new Aerodynamics("Wing", 0, 60);
        Tires tires = new Tires("Soft", 0, 95);

        Car car = new Car();
        car.setEngine(engine);
        car.setChassis(chassis);
        car.setTransmission(transmission);
        car.setSuspension(suspension);
        car.setAerodynamics(aerodynamics);
        car.setTires(tires);
        car.setTactics(Tactics.aggressiveRain());

        Pilot pilot = new Pilot("Pilot", pilotSkill, accuracy, 100000.0);
        Engineer engineer = new Engineer("Engineer", qualification, 50000.0);

        team.addCar(car);
        team.addPilot(pilot);
        team.addEngineer(engineer);

        return team;
    }

    @Test
    @Timeout(value = 25, unit = TimeUnit.SECONDS)
    void testRaceCompletesAndLatchReleases() throws Exception {
        // проверяем что гонка доходит до конца и защелка открывается
        Track track = new Track("Test Track", Arrays.asList(TrackSegment.STRAIGHT), Weather.SUNNY, 1);
        RaceSimulation sim = new RaceSimulation(Arrays.asList(team1, team2), track, 2);

        sim.startSimulation();

        Field latchField = RaceSimulation.class.getDeclaredField("raceLatch");
        latchField.setAccessible(true);
        CountDownLatch latch = (CountDownLatch) latchField.get(sim);

        assertEquals(0, latch.getCount());
    }

    @Test
    void testPitStopSemaphoreLimitsCars() {
        // проверяем работу семафора в боксах напрямую
        PitStopManager pitManager = new PitStopManager(2);

        assertTrue(pitManager.tryEnterPit(1));
        assertTrue(pitManager.tryEnterPit(2));
        assertFalse(pitManager.tryEnterPit(3)); // мест нет

        pitManager.exitPit();
        assertTrue(pitManager.tryEnterPit(3)); // место появилось
    }

    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void testPilotFatigueAccumulatesInSimulation() {
        Track track = new Track("Test Track", Arrays.asList(TrackSegment.STRAIGHT), Weather.SUNNY, 3);
        RaceSimulation sim = new RaceSimulation(Arrays.asList(team1), track, 2);

        Pilot pilot = team1.getPilots().get(0);

        assertEquals(0.0, pilot.getFatigue());
        sim.startSimulation();
        assertTrue(pilot.getFatigue() > 0.0);
    }

    @Test
    void testTacticsPenaltyReduction() {
        // проверяем работу бонусов тактики
        Tactics aggressiveRain = Tactics.aggressiveRain();
        Tactics defensiveSnow = Tactics.defensiveSnow();

        assertEquals(15.0, aggressiveRain.getPenaltyReduction(Weather.RAINY));
        assertEquals(0.0, aggressiveRain.getPenaltyReduction(Weather.SNOWY));
        assertEquals(10.0, defensiveSnow.getPenaltyReduction(Weather.SNOWY));
    }

    @Test
    @Timeout(value = 25, unit = TimeUnit.SECONDS)
    void testCarStatesUpdatedCorrectly() throws Exception {
        // лезем через рефлексию проверять реальные статусы после гонки
        int targetLaps = 2;
        Track track = new Track("Test Track", Arrays.asList(TrackSegment.STRAIGHT), Weather.SUNNY, targetLaps);
        RaceSimulation sim = new RaceSimulation(Arrays.asList(team1), track, 2);

        sim.startSimulation();

        Field statesField = RaceSimulation.class.getDeclaredField("carStates");
        statesField.setAccessible(true);
        Map<?, ?> carStates = (Map<?, ?>) statesField.get(sim);

        assertEquals(1, carStates.size());

        Object carState = carStates.values().iterator().next();

        Field lapsField = carState.getClass().getDeclaredField("lapsCompleted");
        lapsField.setAccessible(true);
        int lapsCompleted = (int) lapsField.get(carState);

        Field finishedField = carState.getClass().getDeclaredField("isFinished");
        finishedField.setAccessible(true);
        boolean isFinished = (boolean) finishedField.get(carState);

        Field dnfField = carState.getClass().getDeclaredField("isDnf");
        dnfField.setAccessible(true);
        boolean isDnf = (boolean) dnfField.get(carState);

        assertTrue((lapsCompleted >= targetLaps && isFinished) || isDnf);
    }

    @Test
    @Timeout(value = 25, unit = TimeUnit.SECONDS)
    void testWeatherThreadUpdatesWeather() throws Exception {
        // проверяем что поток погоды реально меняет volatile переменную
        Track track = new Track("Test Track", Arrays.asList(TrackSegment.STRAIGHT), Weather.SUNNY, 5);
        RaceSimulation sim = new RaceSimulation(Arrays.asList(team1), track, 2);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(sim::startSimulation);

        Field weatherField = RaceSimulation.class.getDeclaredField("currentWeather");
        weatherField.setAccessible(true);

        boolean weatherChanged = false;
        for(int i = 0; i < 15; i++) {
            Thread.sleep(1000);
            Weather w = (Weather) weatherField.get(sim);
            if (w != Weather.SUNNY) {
                weatherChanged = true;
                break;
            }
        }

        executor.shutdownNow();
        assertTrue(weatherChanged);
    }
}