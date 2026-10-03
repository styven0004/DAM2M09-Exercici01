// ParkingLot.java
package com.project;

import java.util.concurrent.Semaphore;

public class ParkingLot {
    private final Semaphore spots; // permisos = places d'aparcament lliures
    private final int capacity;

    public ParkingLot(int capacity) {
        this.capacity = capacity;
        this.spots = new Semaphore(capacity, true); // fair: respecta l'ordre d'arribada
    }

    public int getCapacity() {
        return capacity;
    }

    // Un cotxe intenta entrar: adquireix un permís (espera si està ple)
    public void enter(int carId) throws InterruptedException {
        if (!spots.tryAcquire()) {
            System.out.println("Cotxe " + carId + " espera: aparcament PLE (0/" + capacity + " places lliures)");
            spots.acquire(); // bloqueja fins que algun cotxe surti
        }
        System.out.println("Cotxe " + carId + " ENTRA   -> places lliures: "
                + spots.availablePermits() + "/" + capacity);
    }

    // Un cotxe surt: allibera el permís
    public void exit(int carId) {
        spots.release();
        System.out.println("Cotxe " + carId + " SURT    -> places lliures: "
                + spots.availablePermits() + "/" + capacity);
    }
}
