package arkham.racing.model;

import java.io.Serializable;

public class Pilot implements Serializable {
    private String name;
    private double skill;
    private double accuracy;
    private double price;
    private double fatigue; // 0-100, где 0 = не устал, 100 = полная усталость

    public Pilot(String name, double skill, double accuracy, double price) {
        this.name = name;
        this.skill = skill;
        this.accuracy = accuracy;
        this.price = price;
        this.fatigue = 0.0;
    }

    public String getName() { return name; }
    public double getSkill() { return skill; }
    public double getAccuracy() { return accuracy; }
    public double getPrice() { return price; }
    public double getFatigue() { return fatigue; }

    // добавляет усталость пилоту
    public void addFatigue(double amount) {
        this.fatigue = Math.min(100.0, fatigue + amount);
    }

    // уменьшает усталость пилота
    public void reduceFatigue(double amount) {
        this.fatigue = Math.max(0.0, fatigue - amount);
    }

    // множитель времени от усталости
    public double getFatigueTimeMultiplier() {
        return 1.0 + (fatigue / 100.0) * 0.5;
    }

    // сбрасывает усталость
    public void resetFatigue() {
        this.fatigue = 0.0;
    }

    @Override
    public String toString() {
        return name + " (Мастерство: " + skill + ", Аккуратность: " + accuracy + ", Усталость: " + String.format("%.1f", fatigue) + "%)";
    }
}