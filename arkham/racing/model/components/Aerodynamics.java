package arkham.racing.model.components;

import java.io.Serializable;

public class Aerodynamics extends Component implements Serializable {
    private double downforce;

    public Aerodynamics(String name, double price, double downforce) {
        super(name, price);
        this.downforce = downforce;
    }

    public double getDownforce() { return downforce; }
}