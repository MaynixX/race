package arkham.racing.service;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class PitStopManager {
    private final Semaphore pitBoxes;

    public PitStopManager(int pitBoxCount) {
        this.pitBoxes = new Semaphore(pitBoxCount);
    }

    public boolean tryEnterPit(int carId) {
        return pitBoxes.tryAcquire();
    }

    public void exitPit() {
        pitBoxes.release();
    }

    public int getWaitTime() {
        return ThreadLocalRandom.current().nextInt(10, 16);
    }

    public double getPitStopBonus() {
        // бонус после пит-стопа
        return 0.85;
    }
}