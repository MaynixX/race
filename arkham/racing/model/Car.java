package arkham.racing.model;

import arkham.racing.model.components.*;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Car implements Serializable {
    private Engine engine;
    private Chassis chassis;
    private Transmission transmission;
    private Suspension suspension;
    private Aerodynamics aerodynamics;
    private Tires tires;
    private List<Weapon> meleeWeapons;
    private List<Weapon> rangedWeapons;
    private Tactics tactics;

    public Car() {
        this.meleeWeapons = new ArrayList<>();
        this.rangedWeapons = new ArrayList<>();
    }

    public Engine getEngine() { return engine; }
    public void setEngine(Engine engine) { this.engine = engine; }

    public Chassis getChassis() { return chassis; }
    public void setChassis(Chassis chassis) { this.chassis = chassis; }

    public Transmission getTransmission() { return transmission; }
    public void setTransmission(Transmission transmission) { this.transmission = transmission; }

    public Suspension getSuspension() { return suspension; }
    public void setSuspension(Suspension suspension) { this.suspension = suspension; }

    public Aerodynamics getAerodynamics() { return aerodynamics; }
    public void setAerodynamics(Aerodynamics aerodynamics) { this.aerodynamics = aerodynamics; }

    public Tires getTires() { return tires; }
    public void setTires(Tires tires) { this.tires = tires; }

    public boolean isReady() {
        return engine != null && chassis != null && transmission != null &&
               suspension != null && aerodynamics != null && tires != null;
    }

    public List<Weapon> getMeleeWeapons() { return meleeWeapons; }
    public List<Weapon> getRangedWeapons() { return rangedWeapons; }
    public void addMeleeWeapon(Weapon weapon) { if (meleeWeapons.size() < 2) meleeWeapons.add(weapon); }
    public void addRangedWeapon(Weapon weapon) { if (rangedWeapons.size() < 1) rangedWeapons.add(weapon); }

    public Tactics getTactics() { return tactics; }
    public void setTactics(Tactics tactics) { this.tactics = tactics; }

    public List<Component> getAllComponents() {
        List<Component> components = new ArrayList<>();
        if (engine != null) components.add(engine);
        if (chassis != null) components.add(chassis);
        if (transmission != null) components.add(transmission);
        if (suspension != null) components.add(suspension);
        if (aerodynamics != null) components.add(aerodynamics);
        if (tires != null) components.add(tires);
        return components;
    }

    @Override
    public String toString() {
        if (!isReady()) return "Болид (не собран / отсутствуют компоненты)";
        return "Болид [Двигатель: " + engine.getName() + ", Шасси: " + chassis.getName() + "]";
    }
}