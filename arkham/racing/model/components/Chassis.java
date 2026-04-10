package arkham.racing.model.components;

import java.io.Serializable;

public class Chassis extends Component implements Serializable {
    private double maxEngineWeight;

    public Chassis(String name, double price, double maxEngineWeight) {
        super(name, price);
        this.maxEngineWeight = maxEngineWeight;
    }

    public double getMaxEngineWeight() { return maxEngineWeight; }
}