package test;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import arkham.racing.service.MarketService;
import arkham.racing.service.dto.ActionResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WeaponWeightTest {
    @Test
    public void testWeaponWeightCompatibility() {
        MarketService market = new MarketService();
        Team team = new Team("Test", 100000);
        Car car = new Car();
        Engine engine = new Engine("Test Engine", 0, 0, 100, "V8"); // weight 100
        Chassis chassis = new Chassis("Test Chassis", 0, 200); // max 200
        car.setEngine(engine);
        car.setChassis(chassis);

        // добавить легкое оружие, должно пройти
        Weapon lightWeapon = new Weapon("Light", Weapon.WeaponType.MELEE, 50, 0);
        ActionResult result = market.buyWeapon(team, lightWeapon, car);
        assertTrue(result.isSuccess());
        assertEquals(1, car.getMeleeWeapons().size());

        // добавить тяжелое оружие, должно fail
        Weapon heavyWeapon = new Weapon("Heavy", Weapon.WeaponType.MELEE, 60, 0); // 100 + 50 + 60 = 210 > 200
        result = market.buyWeapon(team, heavyWeapon, car);
        assertFalse(result.isSuccess());
        assertEquals(1, car.getMeleeWeapons().size()); // не добавлено
    }
}