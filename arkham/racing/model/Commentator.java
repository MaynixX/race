package arkham.racing.model;

import arkham.racing.model.environment.Weather;
import java.util.Random;

public class Commentator {
    private static volatile int currentTime = 0; // в секундах
    private Random random = new Random();

    public static void setCurrentTime(int time) {
        currentTime = time;
    }

    private String getCurrentTime() {
        int min = currentTime / 60;
        int sec = currentTime % 60;
        return String.format("%02d:%02d", min, sec);
    }

    public void announceStart(int carCount) {
        System.out.println("[00:00] Сигнал старта! " + carCount + " болидов устремились в первый поворот.");
    }

    public void announceWeather(Weather weather) {
        System.out.println("[" + getCurrentTime() + "] Погода: " + weather.getDescription() + ".");
    }

    public void announceIncident(String incident) {
        System.out.println("[" + getCurrentTime() + "] Инцидент: " + incident);
    }

    public void announcePitStop(int carId, int waitTime) {
        System.out.println("[" + getCurrentTime() + "] Болид #" + carId + " заезжает в боксы (ожидание: " + waitTime + " сек).");
    }

    public void announceComment(String comment) {
        System.out.println("[" + getCurrentTime() + "] Комментарий: " + comment);
    }

    public void announceFinish(int carId, String driverName) {
        System.out.println("[" + getCurrentTime() + "] ФИНИШ! Болид #" + carId + " (" + driverName + ") побеждает.");
    }

    public void announceHeader(String trackName) {
        System.out.println("GLOBAL CIRCUIT SERIES – " + trackName.toUpperCase() + " (LIVE SIMULATION V1.0)");
    }

    public void announceResults(String results) {
        System.out.println(results);
    }

    public void announceStats(String stats) {
        System.out.println(stats);
    }

}