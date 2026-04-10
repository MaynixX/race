package arkham.racing.model;

import arkham.racing.model.environment.Weather;
import java.util.HashMap;
import java.util.Map;

public class Tactics {
    private Map<Weather, Double> penaltyReductions; // снижение штрафа погоды в процентах

    public Tactics() {
        penaltyReductions = new HashMap<>();
        // по умолчанию нейтральные тактики
        penaltyReductions.put(Weather.SUNNY, 0.0);
        penaltyReductions.put(Weather.RAINY, 0.0);
        penaltyReductions.put(Weather.SNOWY, 0.0);
        penaltyReductions.put(Weather.WINDY, 0.0);
    }

    public void setPenaltyReduction(Weather weather, double reduction) {
        penaltyReductions.put(weather, reduction);
    }

    public double getPenaltyReduction(Weather weather) {
        return penaltyReductions.getOrDefault(weather, 0.0);
    }

    // предустановленные тактики
    public static Tactics aggressiveRain() {
        Tactics t = new Tactics();
        t.setPenaltyReduction(Weather.RAINY, 15.0); // снижение штрафа на 15% в дождь
        return t;
    }

    public static Tactics defensiveSnow() {
        Tactics t = new Tactics();
        t.setPenaltyReduction(Weather.SNOWY, 10.0); // снижение штрафа на 10% в снег
        return t;
    }
}