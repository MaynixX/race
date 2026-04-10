package arkham.racing.service;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import arkham.racing.model.environment.*;
import java.util.*;

public class SurvivalRaceService {

    public double calculateOvertakeChance(Team team, Track track) {
        if (team.getCars().isEmpty()) return 0.1;
        Car car = team.getCars().get(0);
        
        // углубленный расчет: учитывать двигатель, подвеску, шины, аэродинамику
        double power = car.getEngine() != null ? car.getEngine().getPower() : 500;
        double handling = car.getSuspension() != null ? car.getSuspension().getHandling() : 50;
        double grip = car.getTires() != null ? car.getTires().getGrip() : 50;
        double downforce = car.getAerodynamics() != null ? car.getAerodynamics().getDownforce() : 30;
        
        // шанс зависит от комбинации характеристик
        double totalScore = power * 0.4 + handling * 0.3 + grip * 0.2 + downforce * 0.1;
        
        // нормализовать к 0-1, с учетом погоды
        double weatherModifier = track.getWeather() == Weather.RAINY ? 0.8 : 1.0;
        return Math.min(1.0, (totalScore / 2000.0) * weatherModifier);
    }

    public List<Team> getAvailableTargets(Team attacker, List<Team> activeTeams, Weapon.WeaponType weaponType) {
        List<Team> targets = new ArrayList<>(activeTeams);
        targets.remove(attacker);
        
        if (weaponType == Weapon.WeaponType.MELEE) {
            // только соседи: впереди и сзади по текущему порядку позиций
            int attackerIndex = activeTeams.indexOf(attacker);
            List<Team> meleeTargets = new ArrayList<>();
            if (attackerIndex > 0) meleeTargets.add(activeTeams.get(attackerIndex - 1)); // впереди (лучшая позиция)
            if (attackerIndex < activeTeams.size() - 1) meleeTargets.add(activeTeams.get(attackerIndex + 1)); // сзади
            return meleeTargets;
        } else {
            // ranged: все
            return targets;
        }
    }

    public boolean performAttack(Team attacker, Team target, Weapon weapon, Random random) {
        // простая логика попадания: 70% шанс
        return random.nextDouble() < 0.7;
    }
}