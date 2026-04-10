package arkham.racing.service;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import arkham.racing.service.dto.ActionResult;
import java.util.*;

public class MarketService {
    private List<Component> availableComponents;
    private List<Pilot> availablePilots;
    private List<Engineer> availableEngineers;
    private List<Nurse> availableNurses;
    private List<Chef> availableChefs;
    private List<Weapon> availableWeapons;

    public MarketService() {
        initMarket();
    }

    private void initMarket() {
        // доступные компоненты
        availableComponents = new ArrayList<>();
        availableComponents.add(new Engine("V8 Turbo", 50000, 800, 150, "V8"));
        availableComponents.add(new Engine("V6 Eco", 35000, 600, 120, "V6"));
        availableComponents.add(new Engine("Electric Power", 70000, 700, 100, "Electric"));

        availableComponents.add(new Chassis("Titanium Base", 30000, 200));
        availableComponents.add(new Chassis("Carbon Fiber", 45000, 180));
        availableComponents.add(new Chassis("Steel Frame", 20000, 250));

        availableComponents.add(new Transmission("Sport Trans", 20000, 0.95, "V8"));
        availableComponents.add(new Transmission("Eco Trans", 15000, 0.92, "V6"));
        availableComponents.add(new Transmission("Pro Trans", 30000, 0.98, "V8"));
        availableComponents.add(new Transmission("Electric Trans", 40000, 0.99, "Electric"));

        availableComponents.add(new Suspension("Sport Susp", 15000, 80, "Titanium Base"));
        availableComponents.add(new Suspension("Pro Susp", 25000, 90, "Carbon Fiber"));
        availableComponents.add(new Suspension("Standard Susp", 10000, 65, "Steel Frame"));

        availableComponents.add(new Aerodynamics("Pro Aero", 10000, 60));
        availableComponents.add(new Aerodynamics("Elite Aero", 18000, 85));
        availableComponents.add(new Aerodynamics("Basic Aero", 5000, 40));

        availableComponents.add(new Tires("Soft Tires", 5000, 95));
        availableComponents.add(new Tires("Medium Tires", 4000, 85));
        availableComponents.add(new Tires("Hard Tires", 3000, 75));

        // доступные пилоты
        availablePilots = new ArrayList<>();
        availablePilots.add(new Pilot("Михаэль Шумахер", 95.0, 92.0, 80000));
        availablePilots.add(new Pilot("Айртон Сенна", 94.0, 88.0, 75000));
        availablePilots.add(new Pilot("Льюис Хэмилтон", 92.0, 90.0, 70000));
        availablePilots.add(new Pilot("Фернандо Алонсо", 88.0, 85.0, 55000));

        // доступные инженеры
        availableEngineers = new ArrayList<>();
        availableEngineers.add(new Engineer("Главный Инженер", 90.0, 60000));
        availableEngineers.add(new Engineer("Ведущий Инженер", 85.0, 45000));
        availableEngineers.add(new Engineer("Старший Инженер", 80.0, 35000));
        availableEngineers.add(new Engineer("Младший Инженер", 70.0, 25000));

        // доступные медсестры
        availableNurses = new ArrayList<>();
        availableNurses.add(new Nurse("Главная Медсестра", 40.0, 35000));
        availableNurses.add(new Nurse("Опытная Медсестра", 35.0, 25000));
        availableNurses.add(new Nurse("Молодая Медсестра", 25.0, 15000));

        // доступные повара
        availableChefs = new ArrayList<>();
        availableChefs.add(new Chef("Шеф-повар", 90.0, 40000));
        availableChefs.add(new Chef("Опытный Повар", 75.0, 28000));
        availableChefs.add(new Chef("Помощник Повара", 60.0, 18000));

        // доступные оружия
        availableWeapons = new ArrayList<>();
        availableWeapons.add(new Weapon("Spike Ram", Weapon.WeaponType.MELEE, 50, 20000));
        availableWeapons.add(new Weapon("Oil Slick", Weapon.WeaponType.MELEE, 30, 15000));
        availableWeapons.add(new Weapon("Laser Cannon", Weapon.WeaponType.RANGED, 100, 50000));
    }

    /**
     * покупка компонента. возвращает результат операции.
     */
    public ActionResult buyComponent(Team team, Component component) {
        if (team.getBudget() >= component.getPrice()) {
            team.spendBudget(component.getPrice());
            team.addComponent(component);
            return ActionResult.success("Куплено: " + component.getName() + ". Остаток: " + team.getBudget());
        }
        return ActionResult.failure("Недостаточно средств для покупки " + component.getName());
    }

    /**
     * найм пилота. возвращает результат операции.
     */
    public ActionResult hirePilot(Team team, Pilot pilot) {
        if (team.getBudget() >= pilot.getPrice()) {
            team.spendBudget(pilot.getPrice());
            team.addPilot(pilot);
            return ActionResult.success("Нанят пилот: " + pilot.getName());
        }
        return ActionResult.failure("Недостаточно средств для найма пилота.");
    }

    /**
     * найм инженера. возвращает результат операции.
     */
    public ActionResult hireEngineer(Team team, Engineer engineer) {
        if (team.getBudget() >= engineer.getPrice()) {
            team.spendBudget(engineer.getPrice());
            team.addEngineer(engineer);
            return ActionResult.success("Нанят инженер: " + engineer.getName());
        }
        return ActionResult.failure("Недостаточно средств для найма инженера.");
    }

    /**
     * найм медсестры. возвращает результат операции.
     */
    public ActionResult hireNurse(Team team, Nurse nurse) {
        if (team.getBudget() >= nurse.getPrice()) {
            team.spendBudget(nurse.getPrice());
            team.addNurse(nurse);
            return ActionResult.success("Нанята медсестра: " + nurse.getName());
        }
        return ActionResult.failure("Недостаточно средств для найма медсестры.");
    }

    /**
     * найм повара. возвращает результат операции.
     */
    public ActionResult hireChef(Team team, Chef chef) {
        if (team.getBudget() >= chef.getPrice()) {
            team.spendBudget(chef.getPrice());
            team.addChef(chef);
            return ActionResult.success("Нанят повар: " + chef.getName());
        }
        return ActionResult.failure("Недостаточно средств для найма повара.");
    }

    public List<Component> getAvailableComponents() {
        return availableComponents;
    }

    public List<Pilot> getAvailablePilots() {
        return availablePilots;
    }

    public List<Engineer> getAvailableEngineers() {
        return availableEngineers;
    }

    public List<Nurse> getAvailableNurses() {
        return availableNurses;
    }

    public List<Chef> getAvailableChefs() {
        return availableChefs;
    }

    public List<Weapon> getAvailableWeapons() {
        return availableWeapons;
    }

    public ActionResult buyWeapon(Team team, Weapon weapon, Car car) {
        if (team.getBudget() >= weapon.getPrice()) {
            // проверка совместимости по массе
            double currentWeight = 0.0;
            if (car.getEngine() != null) {
                currentWeight += car.getEngine().getWeight();
            }
            for (Weapon w : car.getMeleeWeapons()) {
                currentWeight += w.getWeight();
            }
            for (Weapon w : car.getRangedWeapons()) {
                currentWeight += w.getWeight();
            }
            double newWeight = currentWeight + weapon.getWeight();
            if (car.getChassis() != null && newWeight > car.getChassis().getMaxEngineWeight()) {
                return ActionResult.failure("Оружие не совместимо с болидом по массе. Максимальный вес: " + car.getChassis().getMaxEngineWeight() + ", текущий: " + currentWeight + ", с оружием: " + newWeight);
            }

            team.spendBudget(weapon.getPrice());
            if (weapon.getType() == Weapon.WeaponType.MELEE) {
                car.addMeleeWeapon(weapon);
            } else {
                car.addRangedWeapon(weapon);
            }
            return ActionResult.success("Куплено оружие: " + weapon.getName());
        }
        return ActionResult.failure("Недостаточно средств для покупки оружия.");
    }
}
