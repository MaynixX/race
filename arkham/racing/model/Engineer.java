package arkham.racing.model;

import java.io.Serializable;

public class Engineer implements Serializable {
    private String name;
    private double qualification;
    private double price;

    public Engineer(String name, double qualification, double price) {
        this.name = name;
        this.qualification = qualification;
        this.price = price;
    }

    public String getName() { return name; }
    public double getQualification() { return qualification; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return name + " (Квалификация: " + qualification + ")";
    }
}