package test;

import arkham.racing.model.*;
import arkham.racing.service.MarketService;
import arkham.racing.service.dto.ActionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * тесты для найма медсестер и поваров
 */
public class SupportStaffTest {

    private Team testTeam;
    private MarketService market;

    @BeforeEach
    void setUp() {
        testTeam = new Team("Test Team", 100000.0);
        market = new MarketService();
    }

    // тест 1: успешный наём медсестры
    @Test
    void testNurseHiringSuccess() {
        Nurse nurse = new Nurse("Test Nurse", 40.0, 30000);

        ActionResult result = market.hireNurse(testTeam, nurse);

        assertTrue(result.isSuccess());
        assertEquals(70000.0, testTeam.getBudget());
        assertEquals(1, testTeam.getNurses().size());
    }

    // тест 2: невозможность найма медсестры при недостатке средств
    @Test
    void testNurseHiringInsufficientFunds() {
        Nurse expensive = new Nurse("Expensive", 50.0, 150000);

        ActionResult result = market.hireNurse(testTeam, expensive);

        assertFalse(result.isSuccess());
        assertEquals(100000.0, testTeam.getBudget());
        assertEquals(0, testTeam.getNurses().size());
    }

    // тест 3: успешный наём повара
    @Test
    void testChefHiringSuccess() {
        Chef chef = new Chef("Test Chef", 75.0, 35000);

        ActionResult result = market.hireChef(testTeam, chef);

        assertTrue(result.isSuccess());
        assertEquals(65000.0, testTeam.getBudget());
        assertEquals(1, testTeam.getChefs().size());
    }

    // тест 4: невозможность найма повара при недостатке средств
    @Test
    void testChefHiringInsufficientFunds() {
        Chef expensive = new Chef("Expensive", 90.0, 150000);

        ActionResult result = market.hireChef(testTeam, expensive);

        assertFalse(result.isSuccess());
        assertEquals(100000.0, testTeam.getBudget());
        assertEquals(0, testTeam.getChefs().size());
    }

    // тест 5: получение списка доступных медсестер
    @Test
    void testGetAvailableNurses() {
        assertNotNull(market.getAvailableNurses());
        assertTrue(market.getAvailableNurses().size() > 0);
    }

    // тест 6: получение списка доступных поваров
    @Test
    void testGetAvailableChefs() {
        assertNotNull(market.getAvailableChefs());
        assertTrue(market.getAvailableChefs().size() > 0);
    }

    // тест 7: команда может иметь несколько медсестер и поваров
    @Test
    void testMultipleSupportStaff() {
        Nurse nurse1 = new Nurse("Nurse 1", 40.0, 20000);
        Nurse nurse2 = new Nurse("Nurse 2", 35.0, 15000);
        Chef chef1 = new Chef("Chef 1", 75.0, 25000);

        market.hireNurse(testTeam, nurse1);
        market.hireNurse(testTeam, nurse2);
        market.hireChef(testTeam, chef1);

        assertEquals(2, testTeam.getNurses().size());
        assertEquals(1, testTeam.getChefs().size());
    }
}
