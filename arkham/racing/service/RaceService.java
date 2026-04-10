package arkham.racing.service;

import arkham.racing.model.*;
import arkham.racing.model.components.*;
import arkham.racing.model.environment.*;
import arkham.racing.service.dto.RaceRoundResult;
import java.util.*;

public class RaceService {
    private List<RaceResult> globalResults = new ArrayList<>();
    private Random random = new Random();

    // запускает чемпионат и собирает результаты
    public RaceRoundResult runChampionship(List<Team> teams, Track track, Scanner scanner, boolean isPlayerRace) {
        RaceRoundResult roundResult = new RaceRoundResult(track.getName(), track.getWeather().getDescription(), track.getLaps());
        Map<Team, Double> results = new HashMap<>();

        for (Team team : teams) {
            if (team.getCars().isEmpty() || team.getPilots().isEmpty()) continue;

            Car car = team.getCars().get(0);
            Pilot pilot = team.getPilots().get(0);
            Engineer engineer = team.getEngineers().isEmpty() ? null : team.getEngineers().get(0);
            Nurse nurse = team.getNurses().isEmpty() ? null : team.getNurses().get(0);
            Chef chef = team.getChefs().isEmpty() ? null : team.getChefs().get(0);

            if (!car.isReady()) {
                if (isPlayerRace && team == teams.get(0)) {
                    roundResult.addIncident("Ваш болид не готов к гонке!");
                }
                continue;
            }

            // для игрока ремонт через сканер
            if (isPlayerRace && team == teams.get(0)) {
                handlePreRaceRepairs(car, engineer, scanner, roundResult);
            }

            List<String> wearIncidents = checkWearAndTear(car, team.getName());
            for (String incident : wearIncidents) {
                roundResult.addIncident(incident);
            }

            if (!car.isReady()) {
                roundResult.addIncident("Болид команды " + team.getName() + " не может продолжить из-за критической поломки!");
                results.put(team, 9999.0);
                continue;
            }

            // сбрасываем усталость пилота
            pilot.resetFatigue();

            double time = calculateRaceTime(car, pilot, engineer, track, nurse, chef);
            results.put(team, time);
            applyWear(car);
        }

        publishResults(results, track, roundResult);
        return roundResult;
    }

    private void handlePreRaceRepairs(Car car, Engineer engineer, Scanner scanner, RaceRoundResult roundResult) {
        for (Component c : car.getAllComponents()) {
            if (c.getWear() >= 50.0) {
                if (engineer != null) {
                    System.out.println("Компонент " + c.getName() + " изношен на " + String.format("%.1f", c.getWear()) + "%. ");
                    System.out.print("Инженер " + engineer.getName() + " готов починить деталь " + c.getName() + ". Починить? (1 - Да, 0 - Нет): ");

                    if (scanner.hasNextInt()) {
                        int choice = scanner.nextInt();
                        if (choice == 1) {
                            c.setWear(0.0);
                            roundResult.addIncident("Компонент " + c.getName() + " был успешно отремонтирован инженером " + engineer.getName() + ".");
                        }
                    } else {
                        scanner.next(); // сбросить неверный ввод
                    }
                } else {
                    roundResult.addIncident("Компонент " + c.getName() + " изношен на " + String.format("%.1f", c.getWear()) + "%. У вас нет инженеров для ремонта.");
                }
            }
        }
    }

    public double calculateRaceTime(Car car, Pilot pilot, Engineer engineer, Track track, Nurse nurse, Chef chef) {
        double totalTime = 0.0;
        double weatherMult = track.getWeather().getDifficultyMultiplier();
        double engineerBonus = (engineer != null) ? engineer.getQualification() * 0.05 : 0.0; // бонус инженера

        for (int lap = 0; lap < track.getLaps(); lap++) {
            for (TrackSegment segment : track.getSegments()) {
                double segmentTime = 120.0; // базовое время

                switch (segment) {
                    case STRAIGHT:
                        segmentTime -= (car.getEngine().getPower() * car.getTransmission().getEfficiency()) / 250; // мощность двигателя снижает время
                        break;
                    case TURN:
                        segmentTime -= (pilot.getAccuracy() * 0.08) + (car.getSuspension().getHandling() * 0.03);
                        break;
                    case UPHILL:
                        segmentTime -= (car.getEngine().getPower() / car.getEngine().getWeight()) * 1.0;
                        break;
                    case DOWNHILL:
                        segmentTime -= car.getAerodynamics().getDownforce() * 0.12;
                        break;
                }

                segmentTime -= pilot.getSkill() * 0.02; // навык пилота снижает время
                segmentTime -= car.getTires().getGrip() * 0.02;
                segmentTime -= engineerBonus;

                if (segmentTime < 10.0) segmentTime = 10.0;

                segmentTime *= pilot.getFatigueTimeMultiplier();
                totalTime += segmentTime * weatherMult;

                // медсестра снижает усталость
                double fatigueGain = (nurse != null) ? 1.0 : 2.0;
                pilot.addFatigue(fatigueGain);

                // повар снижает усталость
                if (chef != null && pilot.getFatigue() > 25.0) {
                    double fatigueReduction = chef.getCookingQuality() * 0.2; // уменьшает усталость
                    double timeToEat = 5.0;
                    pilot.reduceFatigue(fatigueReduction);
                    totalTime += timeToEat;
                }
            }
        }

        return totalTime;
    }

    // проверка износа деталей
    public List<String> checkWearAndTear(Car car, String teamName) {
        List<String> incidents = new ArrayList<>();
        List<Component> toRemove = new ArrayList<>();

        for (Component c : car.getAllComponents()) {
            if (c.getWear() > 50.0) {
                if (random.nextDouble() > 0.6) {
                    incidents.add("На болиде " + teamName + " сломалась деталь: " + c.getName());
                    c.setDestroyed(true);
                    toRemove.add(c);
                }
            }
        }

        for (Component c : toRemove) {
            if (c instanceof Engine) car.setEngine(null);
            else if (c instanceof Chassis) car.setChassis(null);
            else if (c instanceof Transmission) car.setTransmission(null);
            else if (c instanceof Suspension) car.setSuspension(null);
            else if (c instanceof Aerodynamics) car.setAerodynamics(null);
            else if (c instanceof Tires) car.setTires(null);
        }

        return incidents;
    }

    private void applyWear(Car car) {
        if(car.getEngine() != null) car.getEngine().addWear(15.0);
        if(car.getChassis() != null) car.getChassis().addWear(5.0);
        if(car.getTransmission() != null) car.getTransmission().addWear(10.0);
        if(car.getSuspension() != null) car.getSuspension().addWear(8.0);
        if(car.getAerodynamics() != null) car.getAerodynamics().addWear(4.0);
        if(car.getTires() != null) car.getTires().addWear(25.0);
    }

    private void publishResults(Map<Team, Double> results, Track track, RaceRoundResult roundResult) {
        List<Map.Entry<Team, Double>> sortedResults = new ArrayList<>(results.entrySet());
        sortedResults.sort(Map.Entry.comparingByValue());

        double prizePool = 1000000;

        for (int i = 0; i < sortedResults.size(); i++) {
            Team t = sortedResults.get(i).getKey();
            Double time = sortedResults.get(i).getValue();

            // добавляем результат
            roundResult.addResult(t, time);

            if (time != 9999.0) {
                globalResults.add(new RaceResult(track.getName(), t.getName(), i + 1, time));

                if (i == 0) t.addBudget(prizePool * 0.5);
                else if (i == 1) t.addBudget(prizePool * 0.3);
                else if (i == 2) t.addBudget(prizePool * 0.2);
            }
        }
    }

    public List<RaceResult> getGlobalResults() { return globalResults; }

    // запускает симуляцию гонки
    public void runSimulation(List<Team> teams, Track track, int pitBoxCount) {
        RaceSimulation simulation = new RaceSimulation(teams, track, pitBoxCount);
        simulation.startSimulation();
    }
}
