package test;

import arkham.racing.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * тесты для механики усталости пилота
 */
public class FatigueTest {

    private Pilot pilot;

    @BeforeEach
    void setUp() {
        pilot = new Pilot("Test Pilot", 80.0, 80.0, 50000);
    }

    // тест 1: пилот начинает без усталости
    @Test
    void testPilotStartsWithoutFatigue() {
        assertEquals(0.0, pilot.getFatigue());
    }

    // тест 2: усталость добавляется корректно
    @Test
    void testFatigueIncrease() {
        pilot.addFatigue(20.0);

        assertEquals(20.0, pilot.getFatigue());
    }

    // тест 3: усталость не может превышать 100
    @Test
    void testFatigueMaximum() {
        pilot.addFatigue(150.0);

        assertEquals(100.0, pilot.getFatigue());
    }

    // тест 4: усталость может быть снижена
    @Test
    void testFatigueReduction() {
        pilot.addFatigue(50.0);
        pilot.reduceFatigue(20.0);

        assertEquals(30.0, pilot.getFatigue());
    }

    // тест 5: усталость не может быть ниже 0
    @Test
    void testFatigueMinimum() {
        pilot.addFatigue(30.0);
        pilot.reduceFatigue(50.0);

        assertEquals(0.0, pilot.getFatigue());
    }

    // тест 6: множитель времени без усталости = 1.0
    @Test
    void testTimeMultiplierWithoutFatigue() {
        assertEquals(1.0, pilot.getFatigueTimeMultiplier());
    }

    // тест 7: множитель времени при 50% усталости ~1.25
    @Test
    void testTimeMultiplierWithHalfFatigue() {
        pilot.addFatigue(50.0);

        double multiplier = pilot.getFatigueTimeMultiplier();
        assertEquals(1.25, multiplier, 0.01);
    }

    // тест 8: множитель времени при 100% усталости = 1.5
    @Test
    void testTimeMultiplierWithFullFatigue() {
        pilot.addFatigue(100.0);

        double multiplier = pilot.getFatigueTimeMultiplier();
        assertEquals(1.5, multiplier, 0.01);
    }

    // тест 9: усталость увеличивает время гонки
    @Test
    void testFatigueIncreasesRaceTime() {
        double timeWithoutFatigue = 100.0 * pilot.getFatigueTimeMultiplier();

        pilot.addFatigue(50.0);
        double timeWithFatigue = 100.0 * pilot.getFatigueTimeMultiplier();

        assertTrue(timeWithFatigue > timeWithoutFatigue);
    }

    // тест 10: усталость может быть сброшена
    @Test
    void testFatigueReset() {
        pilot.addFatigue(75.0);
        pilot.resetFatigue();

        assertEquals(0.0, pilot.getFatigue());
    }

    // тест 11: несколько добавлений усталости
    @Test
    void testMultipleFatigueAdditions() {
        pilot.addFatigue(10.0);
        pilot.addFatigue(15.0);
        pilot.addFatigue(25.0);

        assertEquals(50.0, pilot.getFatigue());
    }

    // тест 12: чередующиеся добавления и снижения усталости
    @Test
    void testInterchangeFatigueOperations() {
        pilot.addFatigue(40.0);
        pilot.reduceFatigue(10.0);
        pilot.addFatigue(20.0);
        pilot.reduceFatigue(15.0);

        assertEquals(35.0, pilot.getFatigue());
    }

    // тест 13: tostring содержит информацию об усталости
    @Test
    void testToStringIncludesFatigue() {
        pilot.addFatigue(45.5);
        String str = pilot.toString();

        assertTrue(str.contains("Усталость"), "String should contain 'Усталость': " + str);
        // проверяем что строка содержит число усталости (может быть как 45.5, так и 45,5 в зависимости от локали)
        assertTrue(str.contains("45.5") || str.contains("45,5"), "String should contain fatigue value: " + str);
    }

    // тест 14: проверка линейности множителя усталости
    @Test
    void testFatigueMultiplierLinearity() {
        pilot.resetFatigue();
        double mult0 = pilot.getFatigueTimeMultiplier();

        pilot.addFatigue(25.0);
        double mult25 = pilot.getFatigueTimeMultiplier();

        pilot.resetFatigue();
        pilot.addFatigue(50.0);
        double mult50 = pilot.getFatigueTimeMultiplier();

        // разница между 0% и 25% должна быть точно половиной разницы между 0% и 50%
        assertEquals((mult50 - mult0) / 2.0, mult25 - mult0, 0.001);
    }
}
