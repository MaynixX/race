package test;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import arkham.racing.model.environment.*;
import arkham.racing.service.PitStopManager;
import arkham.racing.service.RaceSimulation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ConcurrencyTest {

    @Test
    void testPitStopConcurrency() throws InterruptedException {
        PitStopManager manager = new PitStopManager(2);
        AtomicInteger activeInPit = new AtomicInteger(0);
        AtomicInteger maxActive = new AtomicInteger(0);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(5);
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    while (true) {
                        if (manager.tryEnterPit(0)) {
                            int current = activeInPit.incrementAndGet();
                            maxActive.updateAndGet(prev -> Math.max(prev, current));
                            Thread.sleep(200);
                            activeInPit.decrementAndGet();
                            manager.exitPit();
                            break;
                        }
                        Thread.sleep(20);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(5, TimeUnit.SECONDS), "All pit stop threads should complete");
        executor.shutdownNow();
        assertTrue(maxActive.get() <= 2, "No more than 2 cars should be in pit stop simultaneously");
    }

    @Test
    void testRaceCompletionLatch() throws Exception {
        Team team1 = setupReadyTeam("Team A", 90.0, 90.0, 80.0);
        Team team2 = setupReadyTeam("Team B", 85.0, 85.0, 75.0);
        Track track = new Track("Test Track", Arrays.asList(TrackSegment.STRAIGHT), Weather.SUNNY, 2);
        RaceSimulation sim = new RaceSimulation(Arrays.asList(team1, team2), track, 2);

        ExecutorService executor = Executors.newSingleThreadExecutor(Thread.ofVirtual().factory());
        Future<?> future = executor.submit(sim::startSimulation);

        future.get(20, TimeUnit.SECONDS);
        executor.shutdownNow();

        Field latchField = RaceSimulation.class.getDeclaredField("raceLatch");
        latchField.setAccessible(true);
        CountDownLatch latch = (CountDownLatch) latchField.get(sim);

        assertEquals(0, latch.getCount(), "RaceSimulation should complete only after all car threads have finished");
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
}
