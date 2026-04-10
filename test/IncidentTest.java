package test;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import arkham.racing.service.RaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * тесты для инцидентов во время гонки
 */
public class IncidentTest {

    private RaceService race;

    @BeforeEach
    void setUp() {
        race = new RaceService();
    }

    // тест 1: проверка на поломку детали с высоким износом
    @Test
    void testComponentBreaksWithHighWear() {
        Engine engine = new Engine("V8", 0, 800, 150, "V8");
        engine.setWear(99.0); // почти убитый двигатель

        Car car = new Car();
        car.setEngine(engine);
        car.setChassis(new Chassis("Base", 0, 200));
        car.setTransmission(new Transmission("Trans", 0, 0.9, "V8"));
        car.setSuspension(new Suspension("Susp", 0, 80, "Base"));
        car.setAerodynamics(new Aerodynamics("Aero", 0, 60));
        car.setTires(new Tires("Tires", 0, 95));

        boolean broke = false;

        // так как поломка зависит от рандома, крутим цикл 50 раз.
        // убитый двигатель обязан сломаться хотя бы один раз.
        for (int i = 0; i < 50; i++) {
            List<String> incidents = race.checkWearAndTear(car, "Test");
            if (!incidents.isEmpty()) {
                broke = true;
                break;
            }
        }

        assertTrue(broke, "деталь с износом 99% должна сломаться хотя бы 1 раз");
    }

    // тест 2: компонент с низким износом не должен ломаться
    @Test
    void testComponentDoesNotBreakWithLowWear() {
        Engine engine = new Engine("V8", 0, 800, 150, "V8");
        engine.setWear(10.0); // новая деталь

        Car car = new Car();
        car.setEngine(engine);
        car.setChassis(new Chassis("Base", 0, 200));
        car.setTransmission(new Transmission("Trans", 0, 0.9, "V8"));
        car.setSuspension(new Suspension("Susp", 0, 80, "Base"));
        car.setAerodynamics(new Aerodynamics("Aero", 0, 60));
        car.setTires(new Tires("Tires", 0, 95));

        boolean broke = false;

        for (int i = 0; i < 50; i++) {
            List<String> incidents = race.checkWearAndTear(car, "Test");
            if (!incidents.isEmpty()) {
                broke = true;
                break;
            }
        }

        assertFalse(broke, "новая деталь вообще не должна ломаться");
    }

    // тест 3: проверка что износ реально влияет на вероятность поломки
    @Test
    void testWearInfluencesBreakage() {
        Engine newEngine = new Engine("V8", 0, 800, 150, "V8");
        newEngine.setWear(5.0);
        Car goodCar = new Car();
        goodCar.setEngine(newEngine);
        goodCar.setChassis(new Chassis("Base", 0, 200));

        Engine badEngine = new Engine("V8", 0, 800, 150, "V8");
        badEngine.setWear(95.0);
        Car badCar = new Car();
        badCar.setEngine(badEngine);
        badCar.setChassis(new Chassis("Base", 0, 200));

        int goodBreaks = 0;
        int badBreaks = 0;

        for (int i = 0; i < 100; i++) {
            if (!race.checkWearAndTear(goodCar, "T1").isEmpty()) goodBreaks++;
            if (!race.checkWearAndTear(badCar, "T2").isEmpty()) badBreaks++;
        }

        assertTrue(badBreaks > goodBreaks, "старая деталь должна ломаться чаще новой");
    }
}