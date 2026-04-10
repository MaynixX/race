package arkham.racing.model;

import java.io.Serializable;

/**
 * медсестра - медицинский персонал команды.
 * дает единый бафф на усталость в начале гонки.
 */
public class Nurse implements Serializable {
    private String name;
    private double healingPower; // 0-100, сколько усталости она может убрать в начале гонки
    private double price;

    public Nurse(String name, double healingPower, double price) {
        this.name = name;
        this.healingPower = healingPower;
        this.price = price;
    }

    public String getName() { return name; }

    public double getPrice() { return price; }

    @Override
    public String toString() {
        return name + " (Лечебная сила: " + healingPower + ")";
    }
}
