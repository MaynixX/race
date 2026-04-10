package arkham.racing.model.environment;

import java.util.List;
import java.io.Serializable;

public class Track implements Serializable {
    private String name;
    private List<TrackSegment> segments;
    private Weather weather;
    private int laps;

    public Track(String name, List<TrackSegment> segments, Weather weather, int laps) {
        this.name = name;
        this.segments = segments;
        this.weather = weather;
        this.laps = laps;
    }

    public String getName() { return name; }
    public List<TrackSegment> getSegments() { return segments; }
    public Weather getWeather() { return weather; }
    public int getLaps() { return laps; }

    @Override
    public String toString() {
        return name + " (" + laps + " кругов, Погода: " + weather.getDescription() + ")";
    }
}