// CarTask.java
package com.project;

import java.util.concurrent.ThreadLocalRandom;

public class CarTask implements Runnable {
    private final ParkingLot parking;
    private final int id;

    public CarTask(ParkingLot parking, int id) {
        this.parking = parking;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            // Temps d'arribada aleatori (100-800 ms)
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 801));

            parking.enter(id); // adquireix permís (pot esperar)
            try {
                // Temps aparcat aleatori (1000-2500 ms)
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 2501));
            } finally {
                parking.exit(id); // allibera permís sempre
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
