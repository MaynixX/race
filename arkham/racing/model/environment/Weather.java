package arkham.racing.model.environment;

public enum Weather {
    SUNNY(1.0, "ясно, трасса сухая"),
    RAINY(1.5, "дождь, трасса мокрая"),
    SNOWY(2.0, "снег, трасса скользкая"),
    WINDY(1.2, "ветер, трасса ветреная");

    private double difficultyMultiplier;
    private String description;

    Weather(double difficultyMultiplier, String description) {
        this.difficultyMultiplier = difficultyMultiplier;
        this.description = description;
    }
    public double getDifficultyMultiplier() { return difficultyMultiplier; }
    public String getDescription() { return description; }
}