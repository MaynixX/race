package arkham.racing.model;

import java.io.Serializable;

public class RaceResult implements Serializable {
    private String trackName;
    private String teamName;
    private int position;
    private double time;

    public RaceResult(String trackName, String teamName, int position, double time) {
        this.trackName = trackName;
        this.teamName = teamName;
        this.position = position;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Трасса: " + trackName + " | Команда: " + teamName + " | Место: " + position + " | Время: " + String.format("%.2f", time) + " сек";
    }
}