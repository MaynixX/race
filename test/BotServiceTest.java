package test;

import arkham.racing.model.*;
import arkham.racing.service.BotService;
import arkham.racing.service.GarageService;
import arkham.racing.service.MarketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * тесты для botservice
 * каждый тест проверяет одно свойство сгенерированного бота
 */
public class BotServiceTest {

    private BotService botService;

    @BeforeEach
    void setUp() {
        MarketService market = new MarketService();
        GarageService garage = new GarageService();
        botService = new BotService(market, garage);
    }

    // тест 1: бот создается с корректным именем
    @Test
    void testBotNameIsCorrect() {
        String botName = "Evil Bot";
        Team bot = botService.generateBotTeam(botName);

        assertEquals(botName, bot.getName());
    }

    // тест 2: у бота есть собранный болид
    @Test
    void testBotHasAssembledCar() {
        Team bot = botService.generateBotTeam("Test Bot");

        assertEquals(1, bot.getCars().size());
    }

    // тест 3: болид бота готов к гонке
    @Test
    void testBotCarIsReady() {
        Team bot = botService.generateBotTeam("Test Bot");

        assertTrue(bot.getCars().get(0).isReady());
    }

    // тест 4: у бота есть пилот
    @Test
    void testBotHasPilot() {
        Team bot = botService.generateBotTeam("Test Bot");

        assertEquals(1, bot.getPilots().size());
    }

    // тест 5: у бота есть инженер
    @Test
    void testBotHasEngineer() {
        Team bot = botService.generateBotTeam("Test Bot");

        assertEquals(1, bot.getEngineers().size());
    }

    // тест 6: болид бота имеет все необходимые компоненты
    @Test
    void testBotCarHasAllComponents() {
        Team bot = botService.generateBotTeam("Test Bot");
        Car car = bot.getCars().get(0);

        assertNotNull(car.getEngine());
        assertNotNull(car.getChassis());
        assertNotNull(car.getTransmission());
        assertNotNull(car.getSuspension());
        assertNotNull(car.getAerodynamics());
        assertNotNull(car.getTires());
    }

    // тест 7: несколько ботов имеют разные имена
    @Test
    void testMultipleBotsHaveDifferentNames() {
        Team bot1 = botService.generateBotTeam("Bot 1");
        Team bot2 = botService.generateBotTeam("Bot 2");

        assertNotEquals(bot1.getName(), bot2.getName());
    }

    // тест 8: каждый бот независим
    @Test
    void testBotsAreIndependent() {
        Team bot1 = botService.generateBotTeam("Bot 1");
        Team bot2 = botService.generateBotTeam("Bot 2");

        assertNotSame(bot1, bot2);
        assertNotSame(bot1.getCars().get(0), bot2.getCars().get(0));
    }
}
