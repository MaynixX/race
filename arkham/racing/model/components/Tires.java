package arkham.racing.model.components;

import java.io.Serializable;

public class Tires extends Component implements Serializable {
    private double grip;

    public Tires(String name, double price, double grip) {
        super(name, price);
        this.grip = grip;
    }

    public double getGrip() { return grip; }
}