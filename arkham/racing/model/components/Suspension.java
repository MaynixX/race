package arkham.racing.model.components;

import java.io.Serializable;

public class Suspension extends Component implements Serializable {
    private double handling;
    private String supportedChassisName;

    public Suspension(String name, double price, double handling, String supportedChassisName) {
        super(name, price);
        this.handling = handling;
        this.supportedChassisName = supportedChassisName;
    }

    public double getHandling() { return handling; }
    public String getSupportedChassisName() { return supportedChassisName; }
}