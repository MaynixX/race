package test;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import arkham.racing.service.GarageService;
import arkham.racing.service.dto.ActionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * тесты для garageservice
 * каждый тест проверяет одно условие сборки болида
 */
public class GarageServiceTest {

    private Team testTeam;
    private GarageService garage;

    @BeforeEach
    void setUp() {
        testTeam = new Team("Test Team", 100000.0);
        garage = new GarageService();
    }

    // тест 1: попытка сборки без инженераs
    @Test
    void testAssemblyWithoutEngineer() {
        Engine e = new Engine("V8", 1000, 800, 150, "V8");
        Chassis c = new Chassis("Base", 1000, 200);
        Transmission t = new Transmission("Trans", 1000, 0.9, "V8");
        Suspension s = new Suspension("Susp", 1000, 80, "Base");
        Aerodynamics a = new Aerodynamics("Aero", 1000, 60);
        Tires tires = new Tires("Tires", 1000, 95);

        ActionResult result = garage.assembleCar(testTeam, e, c, t, s, a, tires);

        assertFalse(result.isSuccess());
        assertEquals(0, testTeam.getCars().size());
    }

    // тест 2: попытка сборки с двигателем тяжелее чем может выдержать шасси
    @Test
    void testAssemblyEngineToHeavyForChassis() {
        testTeam.addEngineer(new Engineer("Eng", 50, 0));
        
        Engine heavyEngine = new Engine("V8", 1000, 800, 150, "V8"); // вес 150
        Chassis lightChassis = new Chassis("Base", 1000, 100); // макс вес 100
        Transmission t = new Transmission("Trans", 1000, 0.9, "V8");
        Suspension s = new Suspension("Susp", 1000, 80, "Base");
        Aerodynamics a = new Aerodynamics("Aero", 1000, 60);
        Tires tires = new Tires("Tires", 1000, 95);

        ActionResult result = garage.assembleCar(testTeam, heavyEngine, lightChassis, t, s, a, tires);

        assertFalse(result.isSuccess());
        assertEquals(0, testTeam.getCars().size());
    }

    // тест 3: несовместимость трансмиссии с двигателем
    @Test
    void testAssemblyIncompatibleTransmission() {
        testTeam.addEngineer(new Engineer("Eng", 50, 0));
        
        Engine e = new Engine("V8", 1000, 800, 150, "V8");
        Chassis c = new Chassis("Base", 1000, 200);
        Transmission wrongTrans = new Transmission("Trans", 1000, 0.9, "V6"); // не подходит для v8
        Suspension s = new Suspension("Susp", 1000, 80, "Base");
        Aerodynamics a = new Aerodynamics("Aero", 1000, 60);
        Tires tires = new Tires("Tires", 1000, 95);

        ActionResult result = garage.assembleCar(testTeam, e, c, wrongTrans, s, a, tires);

        assertFalse(result.isSuccess());
        assertEquals(0, testTeam.getCars().size());
    }

    // тест 4: несовместимость подвески с шасси
    @Test
    void testAssemblyIncompatibleSuspension() {
        testTeam.addEngineer(new Engineer("Eng", 50, 0));
        
        Engine e = new Engine("V8", 1000, 800, 150, "V8");
        Chassis c = new Chassis("Base", 1000, 200);
        Transmission t = new Transmission("Trans", 1000, 0.9, "V8");
        Suspension wrongSusp = new Suspension("Susp", 1000, 80, "Other Base"); // не подходит
        Aerodynamics a = new Aerodynamics("Aero", 1000, 60);
        Tires tires = new Tires("Tires", 1000, 95);

        ActionResult result = garage.assembleCar(testTeam, e, c, t, wrongSusp, a, tires);

        assertFalse(result.isSuccess());
        assertEquals(0, testTeam.getCars().size());
    }

    // тест 5: успешная сборка с совместимыми компонентами
    @Test
    void testSuccessfulAssembly() {
        testTeam.addEngineer(new Engineer("Eng", 50, 0));
        
        Engine e = new Engine("V8", 1000, 800, 150, "V8");
        Chassis c = new Chassis("Base", 1000, 200);
        Transmission t = new Transmission("Trans", 1000, 0.9, "V8");
        Suspension s = new Suspension("Susp", 1000, 80, "Base");
        Aerodynamics a = new Aerodynamics("Aero", 1000, 60);
        Tires tires = new Tires("Tires", 1000, 95);

        ActionResult result = garage.assembleCar(testTeam, e, c, t, s, a, tires);

        assertTrue(result.isSuccess());
        assertEquals(1, testTeam.getCars().size());
        assertTrue(testTeam.getCars().get(0).isReady());
    }

    // тест 6: проверка что компоненты удалены из инвентаря после сборки
    @Test
    void testComponentsRemovedFromInventory() {
        testTeam.addEngineer(new Engineer("Eng", 50, 0));
        
        Engine e = new Engine("V8", 1000, 800, 150, "V8");
        Chassis c = new Chassis("Base", 1000, 200);
        Transmission t = new Transmission("Trans", 1000, 0.9, "V8");
        Suspension s = new Suspension("Susp", 1000, 80, "Base");
        Aerodynamics a = new Aerodynamics("Aero", 1000, 60);
        Tires tires = new Tires("Tires", 1000, 95);

        testTeam.addComponent(e);
        testTeam.addComponent(c);
        testTeam.addComponent(t);
        testTeam.addComponent(s);
        testTeam.addComponent(a);
        testTeam.addComponent(tires);

        assertEquals(6, testTeam.getInventory().size());

        garage.assembleCar(testTeam, e, c, t, s, a, tires);

        assertEquals(0, testTeam.getInventory().size());
    }

    // тест 7: проверка что болид готов к гонке после сборки
    @Test
    void testCarReadyAfterAssembly() {
        testTeam.addEngineer(new Engineer("Eng", 50, 0));
        
        Engine e = new Engine("V8", 1000, 800, 150, "V8");
        Chassis c = new Chassis("Base", 1000, 200);
        Transmission t = new Transmission("Trans", 1000, 0.9, "V8");
        Suspension s = new Suspension("Susp", 1000, 80, "Base");
        Aerodynamics a = new Aerodynamics("Aero", 1000, 60);
        Tires tires = new Tires("Tires", 1000, 95);

        garage.assembleCar(testTeam, e, c, t, s, a, tires);

        Car car = testTeam.getCars().get(0);
        assertTrue(car.isReady());
        assertTrue(car.getEngine() != null);
        assertTrue(car.getChassis() != null);
        assertTrue(car.getTransmission() != null);
        assertTrue(car.getSuspension() != null);
        assertTrue(car.getAerodynamics() != null);
        assertTrue(car.getTires() != null);
    }
}
