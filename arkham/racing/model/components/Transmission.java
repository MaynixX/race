package arkham.racing.model.components;

import java.io.Serializable;

public class Transmission extends Component implements Serializable {
    private double efficiency;
    private String supportedEngineType;

    public Transmission(String name, double price, double efficiency, String supportedEngineType) {
        super(name, price);
        this.efficiency = efficiency;
        this.supportedEngineType = supportedEngineType;
    }

    public double getEfficiency() { return efficiency; }
    public String getSupportedEngineType() { return supportedEngineType; }
}