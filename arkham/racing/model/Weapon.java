package arkham.racing.model;

import java.io.Serializable;

public class Weapon implements Serializable {
    public enum WeaponType {
        MELEE, RANGED
    }

    private String name;
    private WeaponType type;
    private double weight;
    private double price;

    public Weapon(String name, WeaponType type, double weight, double price) {
        this.name = name;
        this.type = type;
        this.weight = weight;
        this.price = price;
    }

    public String getName() { return name; }
    public WeaponType getType() { return type; }
    public double getWeight() { return weight; }
    public double getPrice() { return price; }
}