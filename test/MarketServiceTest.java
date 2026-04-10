package test;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import arkham.racing.service.MarketService;
import arkham.racing.service.dto.ActionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * тесты для marketservice
 * каждый тест проверяет одну конкретную операцию
 */
public class MarketServiceTest {

    private Team testTeam;
    private MarketService market;

    @BeforeEach
    void setUp() {
        testTeam = new Team("Test Team", 100000.0);
        market = new MarketService();
    }

    // тест 1: успешная покупка компонента
    @Test
    void testComponentPurchaseSuccess() {
        Engine engine = new Engine("Test V8", 50000, 800, 150, "V8");
        
        ActionResult result = market.buyComponent(testTeam, engine);
        
        assertTrue(result.isSuccess());
        assertEquals(50000.0, testTeam.getBudget());
        assertEquals(1, testTeam.getInventory().size());
    }

    // тест 2: невозможность покупки при недостатке средств
    @Test
    void testComponentPurchaseInsufficientFunds() {
        Engine expensive = new Engine("Expensive", 200000, 800, 150, "V8");
        
        ActionResult result = market.buyComponent(testTeam, expensive);
        
        assertFalse(result.isSuccess());
        assertEquals(100000.0, testTeam.getBudget());
        assertEquals(0, testTeam.getInventory().size());
    }

    // тест 3: успешный наём пилота
    @Test
    void testPilotHiringSuccess() {
        Pilot pilot = new Pilot("Test Pilot", 80, 80, 30000);
        
        ActionResult result = market.hirePilot(testTeam, pilot);
        
        assertTrue(result.isSuccess());
        assertEquals(70000.0, testTeam.getBudget());
        assertEquals(1, testTeam.getPilots().size());
    }

    // тест 4: невозможность найма пилота при недостатке средств
    @Test
    void testPilotHiringInsufficientFunds() {
        Pilot expensive = new Pilot("Expensive", 95, 95, 200000);
        
        ActionResult result = market.hirePilot(testTeam, expensive);
        
        assertFalse(result.isSuccess());
        assertEquals(100000.0, testTeam.getBudget());
        assertEquals(0, testTeam.getPilots().size());
    }

    // тест 5: успешный наём инженера
    @Test
    void testEngineerHiringSuccess() {
        Engineer engineer = new Engineer("Test Engineer", 75, 25000);
        
        ActionResult result = market.hireEngineer(testTeam, engineer);
        
        assertTrue(result.isSuccess());
        assertEquals(75000.0, testTeam.getBudget());
        assertEquals(1, testTeam.getEngineers().size());
    }

    // тест 6: невозможность найма инженера при недостатке средств
    @Test
    void testEngineerHiringInsufficientFunds() {
        Engineer expensive = new Engineer("Expensive", 90, 200000);
        
        ActionResult result = market.hireEngineer(testTeam, expensive);
        
        assertFalse(result.isSuccess());
        assertEquals(100000.0, testTeam.getBudget());
        assertEquals(0, testTeam.getEngineers().size());
    }

    // тест 7: несколько последовательных покупок
    @Test
    void testMultiplePurchases() {
        Engine engine = new Engine("V8", 50000, 800, 150, "V8");
        Chassis chassis = new Chassis("Base", 30000, 200);
        
        market.buyComponent(testTeam, engine);
        market.buyComponent(testTeam, chassis);
        
        assertEquals(20000.0, testTeam.getBudget());
        assertEquals(2, testTeam.getInventory().size());
    }

    // тест 8: получение списка доступных компонентов
    @Test
    void testGetAvailableComponents() {
        assertNotNull(market.getAvailableComponents());
        assertTrue(market.getAvailableComponents().size() > 0);
    }

    // тест 9: получение списка доступных пилотов
    @Test
    void testGetAvailablePilots() {
        assertNotNull(market.getAvailablePilots());
        assertTrue(market.getAvailablePilots().size() > 0);
    }

    // тест 10: получение списка доступных инженеров
    @Test
    void testGetAvailableEngineers() {
        assertNotNull(market.getAvailableEngineers());
        assertTrue(market.getAvailableEngineers().size() > 0);
    }
}
