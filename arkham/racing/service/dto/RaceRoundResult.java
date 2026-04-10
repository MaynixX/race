package arkham.racing.service.dto;

import arkham.racing.model.Team;
import java.util.*;
import java.io.Serializable;

/**
 * результат раунда гонки
 */
public class RaceRoundResult implements Serializable {
    private String trackName;
    private String weatherDescription;
    private int laps;
    private Map<Team, Double> results; // team -> time
    private List<String> incidents; // сообщения об инцидентах во время гонки

    public RaceRoundResult(String trackName, String weatherDescription, int laps) {
        this.trackName = trackName;
        this.weatherDescription = weatherDescription;
        this.laps = laps;
        this.results = new LinkedHashMap<>();
        this.incidents = new ArrayList<>();
    }

    public void addResult(Team team, Double time) {
        this.results.put(team, time);
    }

    public void addIncident(String incident) {
        this.incidents.add(incident);
    }

    public String getTrackName() {
        return trackName;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public int getLaps() {
        return laps;
    }

    public Map<Team, Double> getResults() {
        return results;
    }

    public List<String> getIncidents() {
        return incidents;
    }
}
