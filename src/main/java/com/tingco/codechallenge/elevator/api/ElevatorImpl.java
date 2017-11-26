package com.tingco.codechallenge.elevator.api;

import com.tingco.codechallenge.elevator.exception.ElevatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElevatorImpl implements Elevator {

    private static Logger LOGGER = LoggerFactory.getLogger(ElevatorImpl.class);
    private static final int MIN_FLOOR = 0;
    private static final int MAX_FLOOR = 30;
    private static Integer AUTO_INCREMENT_ID = 1;

    private int id;
    private Direction direction = Direction.NONE;

    private boolean isBusy = false;
    private int addressedFloor = 0;
    private int currentFloor = 0;

    public ElevatorImpl() {
        synchronized (AUTO_INCREMENT_ID) {
            id = AUTO_INCREMENT_ID++;
        }
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public int getAddressedFloor() {
        return addressedFloor;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void moveElevator(int toFloor) {
        if (toFloor < MIN_FLOOR || toFloor >= MAX_FLOOR) {
            LOGGER.error("Illegal floor number provided...");
            throw new ElevatorException("Cannot move to illegal floor: " + toFloor);
        }

        LOGGER.info("======> Elevator #" + getId() + " is busy now to move to: " + toFloor);


        addressedFloor = toFloor;
        isBusy = true;

        if (addressedFloor > currentFloor) {
            direction = Direction.UP;
            moveElevatorUp();

        } else if (addressedFloor < currentFloor) {
            direction = Direction.DOWN;
            moveElevatorDown();
        }

        isBusy = false;

        LOGGER.info("======> Elevator #" + getId() + " is idle now at: " + currentFloor());
    }

    private void moveElevatorUp() {
        for (int i = currentFloor; i < addressedFloor; i++) {
            LOGGER.info("Elevator #" + getId() + " is moving from " + i + " => " + (i + 1) + " Direction: " + getDirection());
            simulateMovementTime();
            currentFloor++;
        }
        direction = Direction.NONE;
    }

    private void moveElevatorDown() {
        for (int i = currentFloor; i > addressedFloor; i--) {
            LOGGER.info("Elevator #" + getId() + " is moving from " + i + " => " + (i - 1) + " Direction: " + getDirection());
            simulateMovementTime();
            currentFloor--;
        }
        direction = Direction.NONE;
    }

    @Override
    public boolean isBusy() {
        return isBusy;
    }

    @Override
    public int currentFloor() {
        return currentFloor;
    }

    @Override
    public String toString() {
        return "(id=" + getId() + ", currentFloor=" + currentFloor() + ", addressedFloor=" + getAddressedFloor() + ", isBusy=" + isBusy() + ")";
    }

    private void simulateMovementTime() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LOGGER.error("Error in movement time simulator: ");
            e.printStackTrace();
        }
    }
}
