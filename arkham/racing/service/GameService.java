package arkham.racing.service;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import arkham.racing.model.environment.*;
import arkham.racing.service.dto.ActionResult;
import arkham.racing.service.dto.RaceRoundResult;
import arkham.racing.service.dto.SurvivalRaceResult;
import arkham.racing.ui.ConsoleUI;
import java.util.*;

public class GameService {
    private Team playerTeam;
    private Scanner scanner;
    private RaceService raceService;
    private MarketService marketService;
    private GarageService garageService;
    private BotService botService;
    private ConsoleUI ui;

    private SaveLoadService saveLoadService;
    private List<Team> otherTeams;
    private List<Track> availableTracks;
    private Track selectedTrack;
    private SurvivalRaceService survivalRaceService;

    public GameService(Team playerTeam, ConsoleUI ui) {
        this.playerTeam = playerTeam;
        this.ui = ui;
        this.scanner = new Scanner(System.in);
        this.raceService = new RaceService();
        this.marketService = new MarketService();
        this.garageService = new GarageService();
        this.botService = new BotService(this.marketService, this.garageService);
        this.saveLoadService = new SaveLoadService();
        this.otherTeams = new ArrayList<>();
        this.otherTeams.add(botService.generateBotTeam("Мискатоник Racing"));
        this.otherTeams.add(botService.generateBotTeam("Культисты Speed"));
        this.availableTracks = new ArrayList<>();
        this.selectedTrack = null;
        this.survivalRaceService = new SurvivalRaceService();
        initTracks();
    }

    public GameService(Team playerTeam, List<Team> otherTeams, List<Track> availableTracks, ConsoleUI ui) {
        this.playerTeam = playerTeam;
        this.otherTeams = otherTeams;
        this.availableTracks = availableTracks;
        this.ui = ui;
        this.scanner = new Scanner(System.in);
        this.raceService = new RaceService();
        this.marketService = new MarketService();
        this.garageService = new GarageService();
        this.saveLoadService = new SaveLoadService();
        this.selectedTrack = null;
        this.survivalRaceService = new SurvivalRaceService();
    }

    private void initTracks() {
        availableTracks = new ArrayList<>();
        availableTracks.add(new Track("Аркхемское кольцо", Arrays.asList(TrackSegment.STRAIGHT, TrackSegment.TURN, TrackSegment.UPHILL, TrackSegment.TURN), Weather.SUNNY, 5));
        availableTracks.add(new Track("Уэст-ривер-стрит", Arrays.asList(TrackSegment.STRAIGHT, TrackSegment.STRAIGHT, TrackSegment.TURN, TrackSegment.DOWNHILL), Weather.RAINY, 3));
    }

    public void start() {
        runTrackEditor();
        selectTrack();
        boolean running = true;
        while (running) {
            ui.printMainMenu(playerTeam.getBudget());
            if(!scanner.hasNextInt()) { scanner.next(); continue; }
            int choice = scanner.nextInt();
            running = processMenuChoice(choice);
        }
    }

    private boolean processMenuChoice(int choice) {
        switch (choice) {
            case 0: handleDebugInit(); break;
            case 1: handleRaceMenu(); break;
            case 2: handleMarketMenu(); break;
            case 3: handleGarageMenu(); break;
            case 4: handleViewGarage(); break;
            case 5: handleViewStaff(); break;
            case 6: handleViewPlayerStats(); break;
            case 7: handleViewOtherTeams(); break;
            case 8: handleSaveGame(); break;
            case 9:
                ui.printExitingGame();
                return false;
            default: ui.printErrorInvalidMenuChoice();
        }
        return true;
    }

    private void handleDebugInit() {
        ui.printDebugInitStarted();
        Engine v8 = new Engine("V8 Turbo", 50000, 800, 150, "V8");
        Chassis heavyChassis = new Chassis("Titanium Base", 30000, 200);
        Transmission trans = new Transmission("Sport Trans", 20000, 0.95, "V8");
        Suspension susp = new Suspension("Sport Susp", 15000, 80, "Titanium Base");
        Aerodynamics aero = new Aerodynamics("Pro Aero", 10000, 60);
        Tires tires = new Tires("Soft Tires", 5000, 95);

        playerTeam.addComponent(v8);
        playerTeam.addComponent(heavyChassis);
        playerTeam.addComponent(trans);
        playerTeam.addComponent(susp);
        playerTeam.addComponent(aero);
        playerTeam.addComponent(tires);

        playerTeam.addPilot(new Pilot("Шумахер", 95.0, 90.0, 100000));
        playerTeam.addEngineer(new Engineer("Главный Механик", 80.0, 50000));

        ActionResult result = garageService.assembleCar(playerTeam, v8, heavyChassis, trans, susp, aero, tires);
        ui.printDebugInitCompleted();
    }

    private void handleRaceMenu() {
        System.out.println("Выберите режим гонки:");
        System.out.println("1) Обычная гонка");
        System.out.println("2) Игра на выживание");
        System.out.println("3) Симуляция гонки");
        System.out.print("Выберите: ");
        int mode = scanner.nextInt();

        List<Team> participants = new ArrayList<>();
        participants.add(playerTeam);
        participants.addAll(otherTeams);
        
        if (mode == 2) {
            // режим выживания
            SurvivalRaceResult survivalResult = runSurvivalMode(participants, selectedTrack);
            displaySurvivalResult(survivalResult);
            // без автосохранения
        } else if (mode == 3) {
            // режим симуляции
            System.out.print("Введите количество пит-стоп боксов: ");
            int pitBoxes = scanner.nextInt();
            raceService.runSimulation(participants, selectedTrack, pitBoxes);
        } else {
            RaceRoundResult result = raceService.runChampionship(participants, selectedTrack, scanner, true);
            displayRaceResult(result);
            // автосохранение после гонки
            try {
                saveLoadService.saveGame(playerTeam.getName().replace(" Racing", ""), playerTeam, otherTeams, availableTracks, "auto");
                System.out.println("Автосохранение выполнено.");
            } catch (Exception e) {
                System.out.println("Ошибка автосохранения: " + e.getMessage());
            }
        }
    }

    private void displayRaceResult(RaceRoundResult result) {
        ui.printRaceStart(result.getTrackName(), result.getWeatherDescription(), result.getLaps());

        for (String incident : result.getIncidents()) {
            System.out.println(incident);
        }

        List<Map.Entry<Team, Double>> sortedResults = new ArrayList<>(result.getResults().entrySet());
        sortedResults.sort(Map.Entry.comparingByValue());

        ui.printRaceResults(sortedResults);
    }

    private void handleMarketMenu() {
        boolean inMarket = true;
        while (inMarket) {
            System.out.println("\n========== РЫНОК ==========");
            System.out.println("Ваш бюджет: " + playerTeam.getBudget());
            System.out.println("1) Купить компоненты");
            System.out.println("2) Нанять пилота");
            System.out.println("3) Нанять инженера");
            System.out.println("4) Нанять медсестру");
            System.out.println("5) Нанять повара");
            System.out.println("6) Купить оружие");
            System.out.println("7) Вернуться в главное меню");
            System.out.print("Выберите действие: ");

            if(!scanner.hasNextInt()) { scanner.next(); continue; }
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    showComponentsMenu();
                    break;
                case 2:
                    showPilotsMenu();
                    break;
                case 3:
                    showEngineersMenu();
                    break;
                case 4:
                    showNursesMenu();
                    break;
                case 5:
                    showChefsMenu();
                    break;
                case 6:
                    showWeaponsMenu();
                    break;
                case 7:
                    inMarket = false;
                    break;
                default:
                    ui.printMarketInvalidChoice();
            }
        }
    }

    private void showNursesMenu() {
        ui.printNursesMenuHeader();

        Map<Integer, Nurse> nurseMap = new HashMap<>();
        int index = 1;

        for (Nurse nurse : marketService.getAvailableNurses()) {
            System.out.println(index + ") " + nurse.toString());
            nurseMap.put(index, nurse);
            index++;
        }

        ui.printNurseChoice();
        if(!scanner.hasNextInt()) { scanner.next(); return; }
        int choice = scanner.nextInt();

        if (choice == 0) return;

        Nurse selected = nurseMap.get(choice);
        if (selected != null) {
            ActionResult result = marketService.hireNurse(playerTeam, selected);
            System.out.println(result.getMessage());
        } else {
            ui.printInvalidChoiceError();
        }
    }

    private void showChefsMenu() {
        ui.printChefsMenuHeader();

        Map<Integer, Chef> chefMap = new HashMap<>();
        int index = 1;

        for (Chef chef : marketService.getAvailableChefs()) {
            System.out.println(index + ") " + chef.toString());
            chefMap.put(index, chef);
            index++;
        }

        ui.printChefChoice();
        if(!scanner.hasNextInt()) { scanner.next(); return; }
        int choice = scanner.nextInt();

        if (choice == 0) return;

        Chef selected = chefMap.get(choice);
        if (selected != null) {
            ActionResult result = marketService.hireChef(playerTeam, selected);
            System.out.println(result.getMessage());
        } else {
            ui.printInvalidChoiceError();
        }
    }

    private void showWeaponsMenu() {
        if (playerTeam.getCars().isEmpty()) {
            System.out.println("У вас нет болидов для установки оружия!");
            return;
        }

        System.out.println("Выберите болид для установки оружия:");
        Map<Integer, Car> carMap = new HashMap<>();
        int index = 1;
        for (Car car : playerTeam.getCars()) {
            System.out.println(index + ") Болид с двигателем " + (car.getEngine() != null ? car.getEngine().getName() : "нет"));
            carMap.put(index, car);
            index++;
        }
        System.out.print("Выберите болид: ");
        int carChoice = scanner.nextInt();
        Car selectedCar = carMap.get(carChoice);
        if (selectedCar == null) return;

        System.out.println("Доступное оружие:");
        Map<Integer, Weapon> weaponMap = new HashMap<>();
        index = 1;
        for (Weapon weapon : marketService.getAvailableWeapons()) {
            System.out.println(index + ") " + weapon.getName() + " (" + weapon.getType() + ", вес: " + weapon.getWeight() + ", цена: " + weapon.getPrice() + ")");
            weaponMap.put(index, weapon);
            index++;
        }
        System.out.print("Выберите оружие: ");
        int weaponChoice = scanner.nextInt();
        Weapon selectedWeapon = weaponMap.get(weaponChoice);
        if (selectedWeapon != null) {
            ActionResult result = marketService.buyWeapon(playerTeam, selectedWeapon, selectedCar);
            System.out.println(result.getMessage());
        } else {
            ui.printInvalidChoiceError();
        }
    }

    private void showComponentsMenu() {
        ui.printComponentsMenuHeader();

        Map<Integer, Component> componentMap = new HashMap<>();
        int index = 1;

        ui.printComponentsCategory("Двигатели");
        for (Component comp : marketService.getAvailableComponents()) {
            if (comp instanceof Engine) {
                System.out.println(index + ") " + comp.toString());
                componentMap.put(index, comp);
                index++;
            }
        }

        ui.printComponentsCategory("Шасси");
        for (Component comp : marketService.getAvailableComponents()) {
            if (comp instanceof Chassis) {
                System.out.println(index + ") " + comp.toString());
                componentMap.put(index, comp);
                index++;
            }
        }

        ui.printComponentsCategory("Трансмиссии");
        for (Component comp : marketService.getAvailableComponents()) {
            if (comp instanceof Transmission) {
                System.out.println(index + ") " + comp.toString());
                componentMap.put(index, comp);
                index++;
            }
        }

        ui.printComponentsCategory("Подвески");
        for (Component comp : marketService.getAvailableComponents()) {
            if (comp instanceof Suspension) {
                System.out.println(index + ") " + comp.toString());
                componentMap.put(index, comp);
                index++;
            }
        }

        ui.printComponentsCategory("Аэродинамика");
        for (Component comp : marketService.getAvailableComponents()) {
            if (comp instanceof Aerodynamics) {
                System.out.println(index + ") " + comp.toString());
                componentMap.put(index, comp);
                index++;
            }
        }

        ui.printComponentsCategory("Шины");
        for (Component comp : marketService.getAvailableComponents()) {
            if (comp instanceof Tires) {
                System.out.println(index + ") " + comp.toString());
                componentMap.put(index, comp);
                index++;
            }
        }

        ui.printComponentChoice();
        if(!scanner.hasNextInt()) { scanner.next(); return; }
        int choice = scanner.nextInt();

        if (choice == 0) return;

        Component selected = componentMap.get(choice);
        if (selected != null) {
            ActionResult result = marketService.buyComponent(playerTeam, selected);
            System.out.println(result.getMessage());
        } else {
            ui.printInvalidChoiceError();
        }
    }

    private void showPilotsMenu() {
        ui.printPilotsMenuHeader();

        Map<Integer, Pilot> pilotMap = new HashMap<>();
        int index = 1;

        for (Pilot pilot : marketService.getAvailablePilots()) {
            System.out.println(index + ") " + pilot.toString());
            pilotMap.put(index, pilot);
            index++;
        }

        ui.printPilotChoice();
        if(!scanner.hasNextInt()) { scanner.next(); return; }
        int choice = scanner.nextInt();

        if (choice == 0) return;

        Pilot selected = pilotMap.get(choice);
        if (selected != null) {
            ActionResult result = marketService.hirePilot(playerTeam, selected);
            System.out.println(result.getMessage());
        } else {
            ui.printInvalidChoiceError();
        }
    }

    private void showEngineersMenu() {
        ui.printEngineersMenuHeader();

        Map<Integer, Engineer> engineerMap = new HashMap<>();
        int index = 1;

        for (Engineer eng : marketService.getAvailableEngineers()) {
            System.out.println(index + ") " + eng.toString());
            engineerMap.put(index, eng);
            index++;
        }

        ui.printEngineerChoice();
        if(!scanner.hasNextInt()) { scanner.next(); return; }
        int choice = scanner.nextInt();

        if (choice == 0) return;

        Engineer selected = engineerMap.get(choice);
        if (selected != null) {
            ActionResult result = marketService.hireEngineer(playerTeam, selected);
            System.out.println(result.getMessage());
        } else {
            ui.printInvalidChoiceError();
        }
    }

    private void handleGarageMenu() {
        List<Component> inventory = playerTeam.getInventory();

        if (inventory.isEmpty()) {
            ui.printNoComponentsInGarage();
            return;
        }

        if (playerTeam.getPilots().isEmpty()) {
            ui.printNoPilotError();
            return;
        }

        if (playerTeam.getEngineers().isEmpty()) {
            ui.printNoEngineerError();
            return;
        }

        ui.printGarageMenuHeader();

        Engine selectedEngine = (Engine) selectComponent(inventory, "двигатель", Engine.class);
        if (selectedEngine == null) return;

        Chassis selectedChassis = (Chassis) selectComponent(inventory, "шасси", Chassis.class);
        if (selectedChassis == null) return;

        Transmission selectedTrans = (Transmission) selectComponent(inventory, "трансмиссию", Transmission.class);
        if (selectedTrans == null) return;

        Suspension selectedSusp = (Suspension) selectComponent(inventory, "подвеску", Suspension.class);
        if (selectedSusp == null) return;

        Aerodynamics selectedAero = (Aerodynamics) selectComponent(inventory, "аэродинамику", Aerodynamics.class);
        if (selectedAero == null) return;

        Tires selectedTires = (Tires) selectComponent(inventory, "шины", Tires.class);
        if (selectedTires == null) return;

        ActionResult result = garageService.assembleCar(playerTeam, selectedEngine, selectedChassis, selectedTrans,
                selectedSusp, selectedAero, selectedTires);
        System.out.println(result.getMessage());
    }

    private Component selectComponent(List<Component> inventory, String componentType, Class<?> type) {
        List<Component> available = new ArrayList<>();
        int index = 1;

        ui.printSelectComponentPrompt(componentType);

        for (Component comp : inventory) {
            if (type.isInstance(comp)) {
                System.out.println(index + ") " + comp.toString());
                available.add(comp);
                index++;
            }
        }

        if (available.isEmpty()) {
            ui.printNoComponentsOfType(componentType);
            return null;
        }

        ui.printChooseComponentPrompt(componentType);
        if(!scanner.hasNextInt()) { scanner.next(); return null; }
        int choice = scanner.nextInt();

        if (choice == 0) return null;
        if (choice >= 1 && choice <= available.size()) {
            return available.get(choice - 1);
        }

        return null;
    }

    private void handleViewGarage() {
        System.out.println("\n========== ВАШ ГАРАЖ ==========");
        List<Car> cars = playerTeam.getCars();
        if (cars.isEmpty()) {
            System.out.println("Гараж пуст");
            return;
        }
        for (int i = 0; i < cars.size(); i++) {
            System.out.println((i + 1) + ") " + cars.get(i).toString());
        }
    }

    private void handleViewStaff() {
        System.out.println("\n========== ВАША КОМАНДА ==========");
        System.out.println("Пилоты:");
        if (playerTeam.getPilots().isEmpty()) {
            System.out.println("  Нет");
        } else {
            for (Pilot p : playerTeam.getPilots()) {
                System.out.println("  " + p.toString());
            }
        }
        System.out.println("Инженеры:");
        if (playerTeam.getEngineers().isEmpty()) {
            System.out.println("  Нет");
        } else {
            for (Engineer e : playerTeam.getEngineers()) {
                System.out.println("  " + e.toString());
            }
        }
        System.out.println("Медсестры:");
        if (playerTeam.getNurses().isEmpty()) {
            System.out.println("  Нет");
        } else {
            for (Nurse n : playerTeam.getNurses()) {
                System.out.println("  " + n.toString());
            }
        }
        System.out.println("Повара:");
        if (playerTeam.getChefs().isEmpty()) {
            System.out.println("  Нет");
        } else {
            for (Chef c : playerTeam.getChefs()) {
                System.out.println("  " + c.toString());
            }
        }
    }

    private void handleViewPlayerStats() {
        System.out.println("\n========== СТАТИСТИКА ==========");
        List<RaceResult> results = raceService.getGlobalResults();
        if (results.isEmpty()) {
            System.out.println("Нет результатов гонок");
            return;
        }
        for (RaceResult r : results) {
            System.out.println(r.toString());
        }
    }

    private void handleViewOtherTeams() {
        System.out.println("\n========== ДРУГИЕ КОМАНДЫ ==========");
        for (Team t : otherTeams) {
            System.out.println("Команда: " + t.getName() + " | Бюджет: " + t.getBudget());
        }
    }

    private void runTrackEditor() {
        System.out.println("Запустить Редактор Треков? (1 - да, 0 - нет)");
        int choice = scanner.nextInt();
        if (choice == 1) {
            handleTrackEditor();
        }
    }

    private void handleTrackEditor() {
        System.out.println("\n========== РЕДАКТОР ТРЕКОВ ==========");
        System.out.println("1) Создать новый трек");
        System.out.println("2) Редактировать существующий трек");
        System.out.println("3) Удалить трек");
        System.out.println("4) Вернуться");
        System.out.print("Выберите действие: ");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1: createNewTrack(); break;
            case 2: editTrack(); break;
            case 3: deleteTrack(); break;
            case 4: break;
            default: System.out.println("Неверный выбор.");
        }
    }

    private void selectTrack() {
        ui.printAvailableTracks(availableTracks);
        int trackIdx = scanner.nextInt() - 1;
        if (trackIdx >= 0 && trackIdx < availableTracks.size()) {
            selectedTrack = availableTracks.get(trackIdx);
            System.out.println("Выбрана трасса: " + selectedTrack.getName());
        } else {
            System.out.println("Неверный выбор, выбрана первая трасса.");
            selectedTrack = availableTracks.get(0);
        }
    }

    private void createNewTrack() {
        System.out.print("Введите название трека: ");
        String name = scanner.next();
        System.out.print("Введите количество кругов: ");
        int laps = scanner.nextInt();
        System.out.println("Выберите погоду: 1) SUNNY, 2) RAINY");
        int weatherChoice = scanner.nextInt();
        Weather weather = weatherChoice == 1 ? Weather.SUNNY : Weather.RAINY;
        System.out.print("Введите количество сегментов: ");
        int numSegments = scanner.nextInt();
        List<TrackSegment> segments = new ArrayList<>();
        for (int i = 0; i < numSegments; i++) {
            System.out.println("Сегмент " + (i+1) + ": 1) STRAIGHT, 2) TURN, 3) UPHILL, 4) DOWNHILL");
            int segChoice = scanner.nextInt();
            TrackSegment seg = switch (segChoice) {
                case 1 -> TrackSegment.STRAIGHT;
                case 2 -> TrackSegment.TURN;
                case 3 -> TrackSegment.UPHILL;
                case 4 -> TrackSegment.DOWNHILL;
                default -> TrackSegment.STRAIGHT;
            };
            segments.add(seg);
        }
        availableTracks.add(new Track(name, segments, weather, laps));
        System.out.println("Трек создан.");
    }

    private void editTrack() {
        System.out.println("Выберите трек для редактирования:");
        for (int i = 0; i < availableTracks.size(); i++) {
            System.out.println((i+1) + ") " + availableTracks.get(i).getName());
        }
        int idx = scanner.nextInt() - 1;
        if (idx >= 0 && idx < availableTracks.size()) {
            // редактируем название
            System.out.print("Введите новое название: ");
            String newName = scanner.next();
            Track track = availableTracks.get(idx);
            // создаем новый трек
            availableTracks.set(idx, new Track(newName, track.getSegments(), track.getWeather(), track.getLaps()));
            System.out.println("Трек обновлен.");
        } else {
            System.out.println("Неверный выбор.");
        }
    }

    private void deleteTrack() {
        System.out.println("Выберите трек для удаления:");
        for (int i = 0; i < availableTracks.size(); i++) {
            System.out.println((i+1) + ") " + availableTracks.get(i).getName());
        }
        int idx = scanner.nextInt() - 1;
        if (idx >= 0 && idx < availableTracks.size()) {
            availableTracks.remove(idx);
            System.out.println("Трек удален.");
        } else {
            System.out.println("Неверный выбор.");
        }
    }

    private void handleSaveGame() {
        System.out.print("Введите имя сохранения: ");
        String saveName = scanner.next();
        try {
            saveLoadService.saveGame(playerTeam.getName().replace(" Racing", ""), playerTeam, otherTeams, availableTracks, saveName);
            System.out.println("Игра сохранена как '" + saveName + "'");
        } catch (Exception e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    private SurvivalRaceResult runSurvivalMode(List<Team> participants, Track track) {
        System.out.println("Начинается режим выживания на трассе " + track.getName());
        List<Team> activeTeams = new ArrayList<>(participants);
        Random random = new Random();
        double trackLength = track.getLaps() * track.getSegments().stream().mapToDouble(TrackSegment::getLength).sum();
        Map<Team, Double> positions = new HashMap<>();
        for (Team t : activeTeams) {
            positions.put(t, 0.0);
        }

        while (activeTeams.size() > 1) {
            // показываем позиции
            System.out.println("\nТекущие позиции:");
            activeTeams.sort((a, b) -> Double.compare(positions.get(b), positions.get(a))); // сортируем по позиции
            for (int i = 0; i < activeTeams.size(); i++) {
                Team t = activeTeams.get(i);
                System.out.println((i + 1) + ") " + t.getName() + " - " + String.format("%.1f", positions.get(t)) + "m");
            }

            // ход игрока
            if (activeTeams.contains(playerTeam)) {
                System.out.println("\nВаш ход:");
                System.out.println("1) Попытаться обогнать");
                System.out.println("2) Применить атаку");
                System.out.print("Выберите: ");
                int action = scanner.nextInt();
                if (action == 1) {
                    // обгон
                    double overtakeChance = survivalRaceService.calculateOvertakeChance(playerTeam, track);
                    if (random.nextDouble() < overtakeChance) {
                        positions.put(playerTeam, positions.get(playerTeam) + 50);
                        System.out.println("Обгон удался!");
                    } else {
                        System.out.println("Обгон не удался.");
                    }
                } else if (action == 2) {
                    // атака
                    performPlayerAttack(activeTeams, positions, random);
                } else {
                    System.out.println("Неверный выбор, пропуск хода.");
                }
            }

            // ходы ботов
            for (Team bot : new ArrayList<>(activeTeams)) {
                if (bot == playerTeam) continue;

                Car botCar = bot.getCars().isEmpty() ? null : bot.getCars().get(0);
                boolean hasWeapon = false;
                if (botCar != null) {
                    hasWeapon = !botCar.getMeleeWeapons().isEmpty() || !botCar.getRangedWeapons().isEmpty();
                }

                if (!hasWeapon) {
                    // без оружия бот только обгоняет
                    double overtakeChance = survivalRaceService.calculateOvertakeChance(bot, track);
                    if (random.nextDouble() < overtakeChance) {
                        positions.put(bot, positions.get(bot) + 50);
                        System.out.println(bot.getName() + " обогнал!");
                    }
                } else {
                    // боты с оружием решают случайно
                    if (random.nextBoolean()) {
                        double overtakeChance = survivalRaceService.calculateOvertakeChance(bot, track);
                        if (random.nextDouble() < overtakeChance) {
                            positions.put(bot, positions.get(bot) + 50);
                            System.out.println(bot.getName() + " обогнал!");
                        }
                    } else {
                        performBotAttack(bot, activeTeams, positions, random);
                    }
                }
            }

            // все идут вперед
            for (Team t : new ArrayList<>(activeTeams)) {
                double baseSpeed = 100.0;
                if (!t.getCars().isEmpty() && t.getCars().get(0).getEngine() != null) {
                    baseSpeed += t.getCars().get(0).getEngine().getPower() / 10.0;
                }
                positions.put(t, positions.get(t) + baseSpeed);
            }

            // проверяем финиш
            for (Team t : new ArrayList<>(activeTeams)) {
                if (positions.get(t) >= trackLength) {
                    return new SurvivalRaceResult(track.getName(), t.getName(), new ArrayList<>(), positions);
                }
            }
        }

        // остался один
        return new SurvivalRaceResult(track.getName(), activeTeams.get(0).getName(), new ArrayList<>(), positions);
    }

    private void performPlayerAttack(List<Team> activeTeams, Map<Team, Double> positions, Random random) {
        Car playerCar = playerTeam.getCars().isEmpty() ? null : playerTeam.getCars().get(0);
        if (playerCar == null) {
            System.out.println("У вас нет болида для атаки!");
            return;
        }
        List<Weapon> allWeapons = new ArrayList<>();
        allWeapons.addAll(playerCar.getMeleeWeapons());
        allWeapons.addAll(playerCar.getRangedWeapons());
        if (allWeapons.isEmpty()) {
            System.out.println("У вас нет оружия!");
            return;
        }

        for (Weapon weapon : allWeapons) {
            System.out.println("Атака оружием: " + weapon.getName() + " (" + weapon.getType() + ")");
            List<Team> targets = survivalRaceService.getAvailableTargets(playerTeam, activeTeams, weapon.getType());
            if (targets.isEmpty()) {
                System.out.println("Нет доступных целей для этого оружия.");
                continue;
            }
            System.out.println("Выберите цель:");
            for (int i = 0; i < targets.size(); i++) {
                System.out.println((i + 1) + ") " + targets.get(i).getName());
            }
            System.out.print("Цель: ");
            int targetIdx = scanner.nextInt() - 1;
            if (targetIdx >= 0 && targetIdx < targets.size()) {
                Team target = targets.get(targetIdx);
                if (survivalRaceService.performAttack(playerTeam, target, weapon, random)) {
                    activeTeams.remove(target);
                    positions.remove(target);
                    System.out.println(target.getName() + " уничтожен!");
                } else {
                    System.out.println("Атака промахнулась.");
                }
            } else {
                System.out.println("Неверный выбор цели.");
            }
        }
    }

    private void performBotAttack(Team bot, List<Team> activeTeams, Map<Team, Double> positions, Random random) {
        Car botCar = bot.getCars().isEmpty() ? null : bot.getCars().get(0);
        if (botCar == null) return;
        List<Weapon> allWeapons = new ArrayList<>();
        allWeapons.addAll(botCar.getMeleeWeapons());
        allWeapons.addAll(botCar.getRangedWeapons());
        for (Weapon weapon : allWeapons) {
            List<Team> targets = survivalRaceService.getAvailableTargets(bot, activeTeams, weapon.getType());
            if (!targets.isEmpty()) {
                Team target = targets.get(random.nextInt(targets.size()));
                if (survivalRaceService.performAttack(bot, target, weapon, random)) {
                    activeTeams.remove(target);
                    positions.remove(target);
                    System.out.println(bot.getName() + " уничтожил " + target.getName() + "!");
                }
            }
        }
    }

    private void displaySurvivalResult(SurvivalRaceResult result) {
        System.out.println("Результат режима выживания:");
        System.out.println("Победитель: " + result.getWinner());
        System.out.println("Финальные позиции:");
        for (Map.Entry<Team, Double> entry : result.getFinalPositions().entrySet()) {
            System.out.println(entry.getKey().getName() + " - " + String.format("%.1f", entry.getValue()) + "m");
        }
    }
}