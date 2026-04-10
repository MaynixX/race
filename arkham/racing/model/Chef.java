package arkham.racing.model;

import java.io.Serializable;

/**
 * повар - персонал группы поддержки на трассе.
 * на каждом сегменте пилот может покушать, потратив время, чтобы снизить усталость.
 */
public class Chef implements Serializable {
    private String name;
    private double cookingQuality; // 0-100, эффективность снижения усталости за счет еды
    private double price;

    public Chef(String name, double cookingQuality, double price) {
        this.name = name;
        this.cookingQuality = cookingQuality;
        this.price = price;
    }

    public String getName() { return name; }
    public double getCookingQuality() { return cookingQuality; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return name + " (Качество готовки: " + cookingQuality + ")";
    }
}
