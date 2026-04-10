package arkham.racing.model;

import arkham.racing.model.components.Component;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Team implements Serializable {
    private String name;
    private double budget;
    private List<Component> inventory;
    private List<Car> cars;
    private List<Pilot> pilots;
    private List<Engineer> engineers;
    private List<Nurse> nurses;
    private List<Chef> chefs;

    public Team(String name, double initialBudget) {
        this.name = name;
        this.budget = initialBudget;
        this.inventory = new ArrayList<>();
        this.cars = new ArrayList<>();
        this.pilots = new ArrayList<>();
        this.engineers = new ArrayList<>();
        this.nurses = new ArrayList<>();
        this.chefs = new ArrayList<>();
    }

    public String getName() { return name; }
    public double getBudget() { return budget; }
    public void spendBudget(double amount) { this.budget -= amount; }
    public void addBudget(double amount) { this.budget += amount; }

    public List<Component> getInventory() { return inventory; }
    public void addComponent(Component c) { this.inventory.add(c); }
    public void removeComponent(Component c) { this.inventory.remove(c); }

    public List<Car> getCars() { return cars; }
    public void addCar(Car car) { this.cars.add(car); }

    public List<Pilot> getPilots() { return pilots; }
    public void addPilot(Pilot p) { this.pilots.add(p); }

    public List<Engineer> getEngineers() { return engineers; }
    public void addEngineer(Engineer e) { this.engineers.add(e); }

    public List<Nurse> getNurses() { return nurses; }
    public void addNurse(Nurse n) { this.nurses.add(n); }

    public List<Chef> getChefs() { return chefs; }
    public void addChef(Chef c) { this.chefs.add(c); }
}