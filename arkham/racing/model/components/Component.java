package arkham.racing.model.components;

import java.io.Serializable;

public abstract class Component implements Serializable {
    private String name;
    private double price;
    private double wear;

    public Component(String name, double price) {
        this.name = name;
        this.price = price;
        this.wear = 0.0;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getWear() { return wear; }
    public void setWear(double wear) { this.wear = wear; }
    public void setDestroyed(boolean destroyed) {
    }

    public void addWear(double amount) {
        this.wear += amount;
        if (this.wear >= 100.0) {
            this.wear = 100.0;
        }
    }

    @Override
    public String toString() {
        return name + " (Износ: " + String.format("%.1f", wear) + "%)";
    }
}
