package arkham.racing.service;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import java.util.*;

public class BotService {
    private MarketService marketService;
    private GarageService garageService;
    private Random random;

    public BotService(MarketService marketService, GarageService garageService) {
        this.marketService = marketService;
        this.garageService = garageService;
        this.random = new Random();
    }

    public Team generateBotTeam(String name) {
        Team bot = new Team(name, 500000); // достаточный бюджет для найма персонала и покупки компонентов

        // получаем список доступных компонентов
        List<Component> allComponents = marketService.getAvailableComponents();

        // фильруем компоненты по типам
        List<Engine> engines = filterEngines(allComponents);
        List<Chassis> chassis = filterChassis(allComponents);
        List<Transmission> transmissions = filterTransmissions(allComponents);
        List<Suspension> suspensions = filterSuspensions(allComponents);
        List<Aerodynamics> aerodynamics = filterAerodynamics(allComponents);
        List<Tires> tires = filterTires(allComponents);

        // выбираем случайные компоненты
        Engine selectedEngine = engines.get(random.nextInt(engines.size()));
        Chassis selectedChassis = chassis.get(random.nextInt(chassis.size()));
        
        // выбираем совместимую трансмиссию (на основе типа двигателя)
        Transmission selectedTransmission = selectCompatibleTransmission(transmissions, selectedEngine);
        
        // выбираем совместимую подвеску (на основе шасси)
        Suspension selectedSuspension = selectCompatibleSuspension(suspensions, selectedChassis);
        
        Aerodynamics selectedAero = aerodynamics.get(random.nextInt(aerodynamics.size()));
        Tires selectedTires = tires.get(random.nextInt(tires.size()));

        // покупаем компоненты
        marketService.buyComponent(bot, selectedEngine);
        marketService.buyComponent(bot, selectedChassis);
        marketService.buyComponent(bot, selectedTransmission);
        marketService.buyComponent(bot, selectedSuspension);
        marketService.buyComponent(bot, selectedAero);
        marketService.buyComponent(bot, selectedTires);

        // нанимаем случайного пилота и инженера
        List<Pilot> availablePilots = marketService.getAvailablePilots();
        Pilot selectedPilot = availablePilots.get(random.nextInt(availablePilots.size()));
        marketService.hirePilot(bot, selectedPilot);

        List<Engineer> availableEngineers = marketService.getAvailableEngineers();
        Engineer selectedEngineer = availableEngineers.get(random.nextInt(availableEngineers.size()));
        marketService.hireEngineer(bot, selectedEngineer);

        // боты нанимают случайную медсестру и повара для улучшения производительности
        List<Nurse> availableNurses = marketService.getAvailableNurses();
        Nurse selectedNurse = availableNurses.get(random.nextInt(availableNurses.size()));
        marketService.hireNurse(bot, selectedNurse);

        List<Chef> availableChefs = marketService.getAvailableChefs();
        Chef selectedChef = availableChefs.get(random.nextInt(availableChefs.size()));
        marketService.hireChef(bot, selectedChef);

        // собираем болид
        garageService.assembleCarSilent(bot, selectedEngine, selectedChassis, selectedTransmission, 
                                       selectedSuspension, selectedAero, selectedTires);

        // назначаем случайную тактику
        Car botCar = bot.getCars().get(0);
        if (botCar != null) {
            Tactics[] tactics = {Tactics.aggressiveRain(), Tactics.defensiveSnow()};
            botCar.setTactics(tactics[random.nextInt(tactics.length)]);
        }

        // боты покупают 1-2 оружия с учетом совместимости по весу шасси
        int weaponsToBuy = 1 + random.nextInt(2); // 1 или 2
        List<Weapon> availableWeapons = new ArrayList<>(marketService.getAvailableWeapons());
        Collections.shuffle(availableWeapons, random);

        for (Weapon weapon : availableWeapons) {
            if (bot.getCars().isEmpty()) break;
            Car car = bot.getCars().get(0);
            if (car == null) break;

            if (marketService.buyWeapon(bot, weapon, car).isSuccess()) {
                weaponsToBuy--;
            }
            if (weaponsToBuy <= 0) break;
        }

        return bot;
    }

        // выбираем совместимую трансмиссию
    private Transmission selectCompatibleTransmission(List<Transmission> transmissions, Engine engine) {
        String engineType = engine.getEngineType();
        
        // фильтруем трансмиссии, совместимые с этим типом двигателя
        List<Transmission> compatible = new ArrayList<>();
        for (Transmission t : transmissions) {
            if (t.getSupportedEngineType().equals(engineType)) {
                compatible.add(t);
            }
        }
        
        // если совместимые трансмиссии есть, выбираем случайно
        if (!compatible.isEmpty()) {
            return compatible.get(random.nextInt(compatible.size()));
        }
        
        // на случай, если нет совместимых, возвращаем первую доступную
        return transmissions.get(0);
    }

        // выбираем совместимую подвеску
    private Suspension selectCompatibleSuspension(List<Suspension> suspensions, Chassis chassis) {
        String chassisName = chassis.getName();
        
        // фильтруем подвески, совместимые с этим шасси
        List<Suspension> compatible = new ArrayList<>();
        for (Suspension s : suspensions) {
            if (s.getSupportedChassisName().equals(chassisName)) {
                compatible.add(s);
            }
        }
        
        // если совместимые подвески есть, выбираем случайно
        if (!compatible.isEmpty()) {
            return compatible.get(random.nextInt(compatible.size()));
        }
        
        // на случай, если нет совместимых, возвращаем первую доступную
        return suspensions.get(0);
    }

    // методы фильтрации компонентов
    private List<Engine> filterEngines(List<Component> components) {
        List<Engine> result = new ArrayList<>();
        for (Component c : components) {
            if (c instanceof Engine) result.add((Engine) c);
        }
        return result;
    }

    private List<Chassis> filterChassis(List<Component> components) {
        List<Chassis> result = new ArrayList<>();
        for (Component c : components) {
            if (c instanceof Chassis) result.add((Chassis) c);
        }
        return result;
    }

    private List<Transmission> filterTransmissions(List<Component> components) {
        List<Transmission> result = new ArrayList<>();
        for (Component c : components) {
            if (c instanceof Transmission) result.add((Transmission) c);
        }
        return result;
    }

    private List<Suspension> filterSuspensions(List<Component> components) {
        List<Suspension> result = new ArrayList<>();
        for (Component c : components) {
            if (c instanceof Suspension) result.add((Suspension) c);
        }
        return result;
    }

    private List<Aerodynamics> filterAerodynamics(List<Component> components) {
        List<Aerodynamics> result = new ArrayList<>();
        for (Component c : components) {
            if (c instanceof Aerodynamics) result.add((Aerodynamics) c);
        }
        return result;
    }

    private List<Tires> filterTires(List<Component> components) {
        List<Tires> result = new ArrayList<>();
        for (Component c : components) {
            if (c instanceof Tires) result.add((Tires) c);
        }
        return result;
    }
}
