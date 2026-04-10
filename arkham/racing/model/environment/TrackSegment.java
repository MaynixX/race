package arkham.racing.model.environment;

public enum TrackSegment {
    STRAIGHT, TURN, UPHILL, DOWNHILL;

    public double getLength() {
        switch (this) {
            case STRAIGHT: return 500.0;
            case TURN: return 300.0;
            case UPHILL: return 400.0;
            case DOWNHILL: return 350.0;
            default: return 400.0;
        }
    }
}