package test;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import arkham.racing.model.environment.*;
import arkham.racing.service.SurvivalRaceService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class SurvivalModeTest {

    @Test
    public void testMeleeWeaponTargetsOnlyNeighbors() {
        SurvivalRaceService service = new SurvivalRaceService();
        
        // создать команды
        Team attacker = new Team("Attacker", 0);
        Team team1 = new Team("Team1", 0);
        Team team2 = new Team("Team2", 0);
        Team team3 = new Team("Team3", 0);
        
        List<Team> activeTeams = Arrays.asList(team1, attacker, team2, team3); // attacker in middle
        
        List<Team> targets = service.getAvailableTargets(attacker, activeTeams, Weapon.WeaponType.MELEE);
        
        // должны быть только team1 (впереди) и team2 (сзади)
        assertEquals(2, targets.size());
        assertTrue(targets.contains(team1));
        assertTrue(targets.contains(team2));
        assertFalse(targets.contains(team3));
    }

    @Test
    public void testRangedWeaponTargetsAll() {
        SurvivalRaceService service = new SurvivalRaceService();
        
        Team attacker = new Team("Attacker", 0);
        Team team1 = new Team("Team1", 0);
        Team team2 = new Team("Team2", 0);
        Team team3 = new Team("Team3", 0);
        
        List<Team> activeTeams = Arrays.asList(attacker, team1, team2, team3);
        
        List<Team> targets = service.getAvailableTargets(attacker, activeTeams, Weapon.WeaponType.RANGED);
        
        // все кроме attacker
        assertEquals(3, targets.size());
        assertTrue(targets.contains(team1));
        assertTrue(targets.contains(team2));
        assertTrue(targets.contains(team3));
    }

    @Test
    public void testOvertakeChanceDependsOnCharacteristics() {
        SurvivalRaceService service = new SurvivalRaceService();
        
        Team strongTeam = new Team("Strong", 0);
        Car strongCar = new Car();
        strongCar.setEngine(new Engine("Strong", 0, 1000, 100, "V8"));
        strongCar.setSuspension(new Suspension("Strong", 0, 100, "Chassis"));
        strongCar.setTires(new Tires("Strong", 0, 100));
        strongCar.setAerodynamics(new Aerodynamics("Strong", 0, 100));
        strongTeam.addCar(strongCar);
        
        Team weakTeam = new Team("Weak", 0);
        Car weakCar = new Car();
        weakCar.setEngine(new Engine("Weak", 0, 300, 50, "V4"));
        weakCar.setSuspension(new Suspension("Weak", 0, 20, "Chassis"));
        weakCar.setTires(new Tires("Weak", 0, 20));
        weakCar.setAerodynamics(new Aerodynamics("Weak", 0, 10));
        weakTeam.addCar(weakCar);
        
        Track track = new Track("Test", Arrays.asList(TrackSegment.STRAIGHT), Weather.SUNNY, 1);
        
        double strongChance = service.calculateOvertakeChance(strongTeam, track);
        double weakChance = service.calculateOvertakeChance(weakTeam, track);
        
        assertTrue(strongChance > weakChance, "Strong team should have higher overtake chance");
        assertTrue(strongChance > 0 && strongChance <= 1.0);
        assertTrue(weakChance > 0 && weakChance <= 1.0);
    }
}