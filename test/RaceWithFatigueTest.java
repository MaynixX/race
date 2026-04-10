package test;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import arkham.racing.model.environment.*;
import arkham.racing.service.RaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * тесты для интеграции усталости с гонкой и персоналом поддержки
 */
public class RaceWithFatigueTest {

    private RaceService race;

    @BeforeEach
    void setUp() {
        race = new RaceService();
    }

    private Team setupTeam(String name) {
        Team team = new Team(name, 100000);
        team.addPilot(new Pilot("Test Pilot", 80, 80, 0));
        team.addEngineer(new Engineer("Test Eng", 50, 0));

        Car car = new Car();
        car.setEngine(new Engine("V8", 0, 800, 150, "V8"));
        car.setChassis(new Chassis("Base", 0, 200));
        car.setTransmission(new Transmission("Trans", 0, 0.9, "V8"));
        car.setSuspension(new Suspension("Susp", 0, 80, "Base"));
        car.setAerodynamics(new Aerodynamics("Aero", 0, 60));
        car.setTires(new Tires("Tires", 0, 95));
        team.addCar(car);

        return team;
    }

    // тест 1: медсестра реально снижает накопление усталости
    @Test
    void testNurseReducesFatigue() {
        Team teamNoNurse = setupTeam("No Nurse");
        Team teamWithNurse = setupTeam("With Nurse");
        teamWithNurse.addNurse(new Nurse("Test Nurse", 40.0, 0));

        Track track = new Track("Test", Arrays.asList(TrackSegment.STRAIGHT, TrackSegment.TURN, TrackSegment.UPHILL), Weather.SUNNY, 5);

        race.runChampionship(Arrays.asList(teamNoNurse), track, new Scanner(""), false);
        race.runChampionship(Arrays.asList(teamWithNurse), track, new Scanner(""), false);

        double fatigueNoNurse = teamNoNurse.getPilots().get(0).getFatigue();
        double fatigueWithNurse = teamWithNurse.getPilots().get(0).getFatigue();

        assertTrue(fatigueWithNurse < fatigueNoNurse, "с медсестрой пилот должен устать меньше");
    }

    // тест 2: повар реально кормит и снижает усталость
    @Test
    void testChefCanReduceFatigue() {
        Team teamNoChef = setupTeam("No Chef");
        Team teamWithChef = setupTeam("With Chef");
        teamWithChef.addChef(new Chef("Test Chef", 80.0, 0));

        // делаем длинную трассу, чтобы пилот успел сильно устать (усталость > 25) и повар начал работать
        Track track = new Track("Test", Arrays.asList(TrackSegment.STRAIGHT, TrackSegment.TURN, TrackSegment.UPHILL, TrackSegment.DOWNHILL), Weather.SUNNY, 15);

        race.runChampionship(Arrays.asList(teamNoChef), track, new Scanner(""), false);
        race.runChampionship(Arrays.asList(teamWithChef), track, new Scanner(""), false);

        double fatigueNoChef = teamNoChef.getPilots().get(0).getFatigue();
        double fatigueWithChef = teamWithChef.getPilots().get(0).getFatigue();

        assertTrue(fatigueWithChef < fatigueNoChef, "повар должен снизить усталость на длинной дистанции");
    }

    // тест 3: усталость увеличивает время гонки
    @Test
    void testFatigueIncreasesRaceTime() {
        Team freshTeam = setupTeam("Fresh");
        Team tiredTeam = setupTeam("Tired");

        Pilot tiredPilot = tiredTeam.getPilots().get(0);
        tiredPilot.addFatigue(80.0); // загоняем пилота в усталость до гонки

        Track track = new Track("Test", Arrays.asList(TrackSegment.STRAIGHT), Weather.SUNNY, 1);

        double freshTime = race.calculateRaceTime(freshTeam.getCars().get(0), freshTeam.getPilots().get(0), null, track, null, null);

        // сбрасываем усталость не будем, передаем напрямую в calculateRaceTime чтобы проверить множитель
        double tiredTime = race.calculateRaceTime(tiredTeam.getCars().get(0), tiredPilot, null, track, null, null);

        assertTrue(tiredTime > freshTime, "уставший пилот должен ехать медленнее");
    }
}