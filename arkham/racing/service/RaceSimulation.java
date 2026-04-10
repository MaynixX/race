package arkham.racing.service;

import arkham.racing.model.*;
import arkham.racing.model.environment.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RaceSimulation {
    private final Track track;
    private final Commentator commentator;
    private final PitStopManager pitStopManager; // ограничивает количество машин в боксе
    private volatile Weather currentWeather; // погода видна всем потокам
    private final Map<Integer, CarState> carStates = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3, Thread.ofVirtual().factory());
    private final AtomicInteger simulationTime = new AtomicInteger(0); // время гонки
    private final CountDownLatch raceLatch; // ждем, пока все машины закончат
    private final AtomicInteger totalWeatherChanges = new AtomicInteger(0);

    public RaceSimulation(List<Team> teams, Track track, int pitBoxCount) {
        this.track = track;
        this.currentWeather = track.getWeather();
        this.commentator = new Commentator();
        this.pitStopManager = new PitStopManager(pitBoxCount);

        // собираем состояния болидов
        int carId = 1;
        for (Team team : teams) {
            if (!team.getCars().isEmpty() && !team.getPilots().isEmpty()) {
                Car car = team.getCars().get(0);
                Pilot pilot = team.getPilots().get(0);
                carStates.put(carId, new CarState(carId, car, pilot, team.getName()));
                carId++;
            }
        }
        this.raceLatch = new CountDownLatch(carStates.size());
    }

    public void startSimulation() {
        commentator.announceHeader(track.getName());
        commentator.announceStart(carStates.size());

        // запускаем таймер
        scheduler.scheduleAtFixedRate(() -> {
            int time = simulationTime.incrementAndGet();
            Commentator.setCurrentTime(time);
        }, 1, 1, TimeUnit.SECONDS);

        // стартуем смену погоды
        scheduleWeatherChange(0);

        // стартуем проверку инцидентов
        scheduleIncidentCheck(0);

        // стартуем потоки машин
        for (CarState state : carStates.values()) {
            executor.submit(() -> carThread(state));
        }

        // ждем завершения гонки
        try {
            raceLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        stopSimulation();
    }

    private void scheduleWeatherChange(int delaySeconds) {
        scheduler.schedule(() -> {
            weatherThread();
            scheduleWeatherChange(ThreadLocalRandom.current().nextInt(10, 16));
        }, delaySeconds, TimeUnit.SECONDS);
    }

    private void scheduleIncidentCheck(int delaySeconds) {
        scheduler.schedule(() -> {
            incidentThread();
            scheduleIncidentCheck(ThreadLocalRandom.current().nextInt(7, 11));
        }, delaySeconds, TimeUnit.SECONDS);
    }

    private void carThread(CarState state) {
        while (!state.isFinished && !state.isDnf && !Thread.currentThread().isInterrupted()) {
            double lapTime = calculateLapTime(state);
            state.totalTime += lapTime;
            state.lapsCompleted++;
            state.tireWear += ThreadLocalRandom.current().nextDouble(15.0, 30.0);


            state.pilot.addFatigue(1.0);

            if (state.lapsCompleted >= track.getLaps()) {
                state.isFinished = true;
                commentator.announceFinish(state.carId, state.teamName);
                raceLatch.countDown();
                break;
            }

                if (state.tireWear > 75.0) {
                if (pitStopManager.tryEnterPit(state.carId)) {
                    int waitTime = pitStopManager.getWaitTime();
                    commentator.announcePitStop(state.carId, waitTime);
                    try {
                        Thread.sleep(waitTime * 100L);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    state.tireWear = 0.0;
                    state.pitStopBonusMultiplier = pitStopManager.getPitStopBonus();
                    state.pitStopCount++;
                    pitStopManager.exitPit();
                } else {
                    commentator.announceComment("Болид #" + state.carId + " ожидает свободный бокс.");
                }
            } else {
                state.pitStopBonusMultiplier = 1.0;
            }


            try {
                Thread.sleep(ThreadLocalRandom.current().nextLong(2000, 3001));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (state.isDnf) {
            raceLatch.countDown();
        }
    }

    private double calculateLapTime(CarState state) {
        double baseTime = ThreadLocalRandom.current().nextDouble(100.0, 121.0);
        double weatherMult;
        synchronized (this) {
            weatherMult = currentWeather.getDifficultyMultiplier();
        }
        double tacticsReduction = state.car.getTactics() != null ? state.car.getTactics().getPenaltyReduction(currentWeather) : 0.0;
        double fatigueMult = state.pilot.getFatigueTimeMultiplier();
        double tirePenalty = 1.0 + (state.tireWear / 100.0) * 0.35;
        double result = baseTime * weatherMult * (1.0 - tacticsReduction / 100.0) * fatigueMult * tirePenalty * state.pitStopBonusMultiplier;
        state.pitStopBonusMultiplier = 1.0;
        return result + state.damagePenalty;
    }

    private void weatherThread() {
        Weather oldWeather = currentWeather;
        Weather[] weathers = Weather.values();
        Weather newWeather = weathers[ThreadLocalRandom.current().nextInt(weathers.length)];

        synchronized (this) {
            currentWeather = newWeather;
        }

        if (newWeather != oldWeather) {
            totalWeatherChanges.incrementAndGet();
            commentator.announceWeather(newWeather);
            commentator.announceComment("Внимание: начинается " + newWeather.getDescription() + "!");
        }
    }

    private void incidentThread() {
        List<CarState> activeCars = carStates.values().stream().filter(s -> !s.isFinished && !s.isDnf).toList();
        if (activeCars.isEmpty()) {
            return;
        }
        CarState victim = activeCars.get(ThreadLocalRandom.current().nextInt(activeCars.size()));
        if (ThreadLocalRandom.current().nextDouble() < 0.25) {
            victim.isDnf = true;
            commentator.announceIncident("Инцидент на трассе! Болид #" + victim.carId + " выбывает из-за повреждений.");
        } else {
            victim.damagePenalty += 6.8;
            commentator.announceIncident("Инцидент на трассе! Болид #" + victim.carId + " получил повреждение, +6.8 с/круг.");
        }
    }

    public void stopSimulation() {
        executor.shutdownNow();
        scheduler.shutdownNow();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
            scheduler.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<CarState> results = new ArrayList<>(carStates.values());
        results.sort(Comparator.comparingDouble(s -> s.isDnf ? Double.MAX_VALUE : s.totalTime));

        StringBuilder resultsStr = new StringBuilder("\nИтоговые результаты:");
        for (int i = 0; i < results.size(); i++) {
            CarState s = results.get(i);
            if (s.isDnf) {
                resultsStr.append("\n(").append(i + 1).append(") ").append(s.teamName).append(": сход (авария, круг ").append(s.lapsCompleted).append(").");
            } else {
                resultsStr.append("\n(").append(i + 1).append(") ").append(s.teamName).append(": ").append(s.lapsCompleted).append(" кругов, ").append(formatTime(s.totalTime)).append(".");
            }
            resultsStr.append(" Пит-стопов: ").append(s.pitStopCount).append(".");
        }
        commentator.announceResults(resultsStr.toString());

        int pitStops = results.stream().mapToInt(s -> s.pitStopCount).sum();
        int dnfCount = (int) results.stream().filter(s -> s.isDnf).count();
        int weatherChanges = totalWeatherChanges.get();
        commentator.announceStats("Статистика: " + pitStops + " пит-стопа, " + dnfCount + " сходов, " + weatherChanges + " смена погоды.");
    }

    private String formatTime(double seconds) {
        int min = (int) (seconds / 60);
        int sec = (int) (seconds % 60);
        int ms = (int) ((seconds % 1) * 1000);
        return String.format("%d:%02d.%03d", min, sec, ms);
    }

    private static class CarState {
        int carId;
        Car car;
        Pilot pilot;
        String teamName;
        double totalTime = 0.0;
        int lapsCompleted = 0;
        volatile boolean isFinished = false;
        volatile boolean isDnf = false;
        double tireWear = 0.0;
        double pitStopBonusMultiplier = 1.0;
        int pitStopCount = 0;
        volatile double damagePenalty = 0.0; // штраф за повреждения

        CarState(int carId, Car car, Pilot pilot, String teamName) {
            this.carId = carId;
            this.car = car;
            this.pilot = pilot;
            this.teamName = teamName;
        }
    }
}