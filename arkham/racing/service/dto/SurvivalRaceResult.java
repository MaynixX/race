package arkham.racing.service.dto;

import arkham.racing.model.Team;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SurvivalRaceResult implements Serializable {
    private String trackName;
    private String winner;
    private List<String> eliminated;
    private Map<Team, Double> finalPositions;

    public SurvivalRaceResult(String trackName, String winner, List<String> eliminated, Map<Team, Double> finalPositions) {
        this.trackName = trackName;
        this.winner = winner;
        this.eliminated = eliminated;
        this.finalPositions = finalPositions;
    }

    public String getWinner() { return winner; }

    public Map<Team, Double> getFinalPositions() { return finalPositions; }
}