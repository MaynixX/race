package arkham.racing.model.components;

import java.io.Serializable;

public class Engine extends Component implements Serializable {
    private double power;
    private double weight;
    private String engineType;

    public Engine(String name, double price, double power, double weight, String engineType) {
        super(name, price);
        this.power = power;
        this.weight = weight;
        this.engineType = engineType;
    }

    public double getPower() { return power; }
    public double getWeight() { return weight; }
    public String getEngineType() { return engineType; }
}