package test;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import arkham.racing.model.environment.*;
import arkham.racing.service.RaceService;
import arkham.racing.service.dto.RaceRoundResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * тесты для raceservice
 * каждый тест проверяет одно условие гонки
 */
public class RaceServiceTest {

    private RaceService race;
    private Team goodTeam;
    private Team badTeam;

    @BeforeEach
    void setUp() {
        race = new RaceService();
        goodTeam = setupReadyTeam("Good Team", 95.0, 95.0, 90.0);
        badTeam = setupReadyTeam("Bad Team", 50.0, 50.0, 10.0);
    }

    // тест 1: гонка должна вернуть результаты для обеих команд
    @Test
    void testRaceReturnsResults() {
        Track track = new Track("Test Track", Arrays.asList(TrackSegment.STRAIGHT, TrackSegment.TURN), Weather.SUNNY, 1);
        
        RaceRoundResult result = race.runChampionship(Arrays.asList(goodTeam, badTeam), track, new Scanner(""), false);
        
        assertNotNull(result, "Race result should not be null");
        assertNotNull(result.getResults(), "Results map should not be null");
        assertTrue(result.getResults().size() > 0, "Should have at least one result");
    }

    // тест 2: хороший пилот быстрее плохого
    @Test
    void testGoodPilotFasterThanBad() {
        Track track = new Track("Test Track", Arrays.asList(TrackSegment.STRAIGHT), Weather.SUNNY, 1);
        
        RaceRoundResult result = race.runChampionship(Arrays.asList(goodTeam, badTeam), track, new Scanner(""), false);
        
        assertNotNull(result.getResults(), "Results should not be null");
        assertTrue(result.getResults().containsKey(goodTeam), "Good team should have result");
        assertTrue(result.getResults().containsKey(badTeam), "Bad team should have result");
        
        Double goodTime = result.getResults().get(goodTeam);
        Double badTime = result.getResults().get(badTeam);
        
        assertNotNull(goodTime, "Good team time should not be null");
        assertNotNull(badTime, "Bad team time should not be null");
        assertTrue(goodTime < badTime, "Good pilot should be faster (" + goodTime + " < " + badTime + ")");
    }

    // тест 3: погода влияет на время гонки
    @Test
    void testWeatherInfluence() {
        // создаем две отдельные команды для тестирования разной погоды
        Team sunnyTeam = setupReadyTeam("Sunny Team", 80, 80, 50);
        Team rainyTeam = setupReadyTeam("Rainy Team", 80, 80, 50);
        
        Track sunnyTrack = new Track("Sunny", Arrays.asList(TrackSegment.STRAIGHT), Weather.SUNNY, 1);
        Track rainyTrack = new Track("Rainy", Arrays.asList(TrackSegment.STRAIGHT), Weather.RAINY, 1);

        RaceRoundResult sunnyResult = race.runChampionship(Arrays.asList(sunnyTeam), sunnyTrack, new Scanner(""), false);
        RaceRoundResult rainyResult = race.runChampionship(Arrays.asList(rainyTeam), rainyTrack, new Scanner(""), false);

        assertNotNull(sunnyResult.getResults().values(), "Sunny result should have values");
        assertTrue(sunnyResult.getResults().size() > 0, "Sunny result should have at least one entry");
        assertNotNull(rainyResult.getResults().values(), "Rainy result should have values");
        assertTrue(rainyResult.getResults().size() > 0, "Rainy result should have at least one entry");
        
        Double sunnyTime = (Double) sunnyResult.getResults().values().toArray()[0];
        Double rainyTime = (Double) rainyResult.getResults().values().toArray()[0];
        
        assertTrue(sunnyTime < rainyTime, "Sunny weather should be faster (" + sunnyTime + " < " + rainyTime + ")");
    }

    // тест 4: время расчета больше нуля
    @Test
    void testRaceTimePositive() {
        Engine e = new Engine("V8", 0, 800, 150, "V8");
        Chassis c = new Chassis("Base", 0, 200);
        Transmission t = new Transmission("Trans", 0, 0.9, "V8");
        Suspension s = new Suspension("Susp", 0, 80, "Base");
        Aerodynamics a = new Aerodynamics("Aero", 0, 60);
        Tires tires = new Tires("Tires", 0, 95);

        Car car = new Car();
        car.setEngine(e);
        car.setChassis(c);
        car.setTransmission(t);
        car.setSuspension(s);
        car.setAerodynamics(a);
        car.setTires(tires);

        Pilot pilot = new Pilot("Pilot", 80, 80, 0);
        Engineer engineer = new Engineer("Eng", 50, 0);
        Track track = new Track("Test", Arrays.asList(TrackSegment.STRAIGHT), Weather.SUNNY, 1);

        double time = race.calculateRaceTime(car, pilot, engineer, track, null, null);
        
        assertTrue(time > 0, "Race time should be positive");
    }

    // тест 5: инженер влияет на время гонки
    @Test
    void testEngineerInfluence() {
        Engine e = new Engine("V8", 0, 800, 150, "V8");
        Chassis c = new Chassis("Base", 0, 200);
        Transmission t = new Transmission("Trans", 0, 0.9, "V8");
        Suspension s = new Suspension("Susp", 0, 80, "Base");
        Aerodynamics a = new Aerodynamics("Aero", 0, 60);
        Tires tires = new Tires("Tires", 0, 95);

        Car car = new Car();
        car.setEngine(e);
        car.setChassis(c);
        car.setTransmission(t);
        car.setSuspension(s);
        car.setAerodynamics(a);
        car.setTires(tires);

        Pilot pilot = new Pilot("Pilot", 80, 80, 0);
        Engineer goodEngineer = new Engineer("Good", 90, 0);
        Engineer badEngineer = new Engineer("Bad", 30, 0);
        Track track = new Track("Test", Arrays.asList(TrackSegment.STRAIGHT), Weather.SUNNY, 1);

        double timeWithGoodEng = race.calculateRaceTime(car, pilot, goodEngineer, track, null, null);
        double timeWithBadEng = race.calculateRaceTime(car, pilot, badEngineer, track, null, null);
        
        assertTrue(timeWithGoodEng < timeWithBadEng);
    }

    // тест 6: восстановленные результаты добавляются в глобальную историю
    @Test
    void testResultsAddedToGlobalHistory() {
        Track track = new Track("Test Track", Arrays.asList(TrackSegment.STRAIGHT), Weather.SUNNY, 1);
        int historySizeBefore = race.getGlobalResults().size();
        
        race.runChampionship(Arrays.asList(goodTeam, badTeam), track, new Scanner(""), false);
        
        int historySizeAfter = race.getGlobalResults().size();
        assertTrue(historySizeAfter > historySizeBefore);
    }

    // тест 7: износ применяется к компонентам после гонки
    @Test
    void testWearAppliedAfterRace() {
        Engine e = new Engine("V8", 0, 800, 150, "V8");
        Chassis c = new Chassis("Base", 0, 200);
        Transmission t = new Transmission("Trans", 0, 0.9, "V8");
        Suspension s = new Suspension("Susp", 0, 80, "Base");
        Aerodynamics a = new Aerodynamics("Aero", 0, 60);
        Tires tires = new Tires("Tires", 0, 95);

        Team team = new Team("Test", 100000);
        team.addPilot(new Pilot("P", 80, 80, 0));
        team.addEngineer(new Engineer("E", 50, 0));
        
        Car car = new Car();
        car.setEngine(e);
        car.setChassis(c);
        car.setTransmission(t);
        car.setSuspension(s);
        car.setAerodynamics(a);
        car.setTires(tires);
        team.addCar(car);

        double engineWearBefore = car.getEngine().getWear();
        
        Track track = new Track("Test", Arrays.asList(TrackSegment.STRAIGHT), Weather.SUNNY, 1);
        race.runChampionship(Arrays.asList(team), track, new Scanner(""), false);
        
        double engineWearAfter = car.getEngine().getWear();
        assertTrue(engineWearAfter > engineWearBefore);
    }

    // вспомогательный метод для быстрой подготовки команды
    private Team setupReadyTeam(String name, double pilotSkill, double pilotAcc, double engQual) {
        Team t = new Team(name, 100000);
        Engine e = new Engine("V8", 0, 800, 150, "V8");
        Chassis c = new Chassis("Base", 0, 200);
        Transmission tr = new Transmission("Trans", 0, 0.9, "V8");
        Suspension s = new Suspension("Susp", 0, 80, "Base");
        Aerodynamics a = new Aerodynamics("Aero", 0, 60);
        Tires tires = new Tires("Tires", 0, 95);

        t.addPilot(new Pilot("Pilot", pilotSkill, pilotAcc, 0));
        t.addEngineer(new Engineer("Eng", engQual, 0));
        
        Car car = new Car();
        car.setEngine(e);
        car.setChassis(c);
        car.setTransmission(tr);
        car.setSuspension(s);
        car.setAerodynamics(a);
        car.setTires(tires);
        t.addCar(car);
        
        return t;
    }
}
