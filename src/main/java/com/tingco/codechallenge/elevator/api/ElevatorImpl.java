package com.tingco.codechallenge.elevator.api;

import com.google.common.eventbus.EventBus;
import com.tingco.codechallenge.elevator.exception.ElevatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

class ElevatorImpl implements Elevator {

    private static Logger LOGGER = LoggerFactory.getLogger(ElevatorImpl.class);
    private static Integer AUTO_INCREMENT_ID = 1;

    /**
     * An unique id of this elevator.
     */
    private int id;

    /**
     * Keeps track of the direction to which it is moving.
     */
    private Direction direction = Direction.NONE;

    /**
     * Keeps track of in which floor the elevator is moving.
     */
    private int addressedFloor = 0;

    /**
     * Keeps track of all floors where the elevator will stop. Thread-safe list.
     */
    private Vector<Integer> stoppageFloors = new Vector<>();

    /**
     * Keeps track of in which floor the elevator is currently lying on now.
     */
    private int currentFloor = 0;

    private Thread movementThread;
    private Lock movementLock = new ReentrantLock();

    @Autowired
    private ElevatorConfiguration config;

    @Autowired
    private EventBus eventBus;

    /**
     * Thread safe constructor.
     */
    public ElevatorImpl() {
        synchronized (AUTO_INCREMENT_ID) {
            id = AUTO_INCREMENT_ID++;
        }

        movementThread = new Thread(() -> {
            doMovement();
        });
        movementThread.start();
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
        validateFloor(toFloor);
        enqueueStoppageFloor(toFloor);
        findAndSetNextAddressedFloor();
    }

    @Override
    public boolean isBusy() {
        return stoppageFloors.size() > 0;
    }


    @Override
    public int currentFloor() {
        return currentFloor;
    }

    @Override
    public String toString() {
        return "(id=" + getId() + ", currentFloor=" + currentFloor() + ", stoppageFloors=" + stoppageFloors + ", addressedFloor=" + getAddressedFloor() + ", isBusy=" + isBusy() + ")";
    }

    private void validateFloor(int floor) {
        if (floor < 0 || floor > config.numberOfFloors) {
            LOGGER.error("Cannot move to illegal floor: " + floor + ". Possible floor range: [0, " + config.numberOfFloors + "]");
            throw new ElevatorException("Cannot move to illegal floor: " + floor + ". Possible floor range: [0, " + config.numberOfFloors + "]");
        }
    }

    private void doMovement() {
        LOGGER.info("Movement controller unit of elevator #" + getId() + " has started");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                boolean isMoved = false;

                while (checkAddressedFloorToGoUp()) {
                    isMoved = true;
                    direction = Direction.UP;
                    simulateMovementUp();
                    moveUpDone();
                }

                while (checkAddressedFloorToGoDown()) {
                    isMoved = true;
                    direction = Direction.DOWN;
                    simulateMovementDown();
                    moveDownDone();
                }

                if (isMoved) {
                    dequeueStoppageFloor(addressedFloor);
                    simulatePassengerEnterOrExit();
                    eventBus.post(new ElevatorMovementEvent(this, addressedFloor));

                } else if (direction != Direction.NONE) {
                    releaseElevator();
                }

                findAndSetNextAddressedFloor();

                Thread.sleep(100);
            } catch (InterruptedException ie) {
                break;
            }
        }

        LOGGER.info("Movement controller unit of elevator #" + getId() + " has stopped");
    }

    @Override
    public void releaseElevator() {
        movementLock.lock();
        try {
            stoppageFloors.clear();
            direction = Direction.NONE;
        } finally {
            movementLock.unlock();
        }
    }

    private void enqueueStoppageFloor(int floor) {
        movementLock.lock();
        try {
            stoppageFloors.add(floor);
            stoppageFloors = new Vector<>(stoppageFloors.stream().distinct().sorted().collect(Collectors.toList()));

            LOGGER.info("Elevator #" + getId() + ") enqueued floor: " + floor + ". Next stoppages: " + stoppageFloors);
        } finally {
            movementLock.unlock();
        }
    }

    private void dequeueStoppageFloor(int floor) {
        movementLock.lock();
        try {
            stoppageFloors.removeElement(floor);
        } finally {
            movementLock.unlock();
        }
    }

    private void findAndSetNextAddressedFloor() {
        movementLock.lock();
        try {
            if (direction == Direction.NONE || direction == Direction.UP) {
                Integer nextFloor = findNextAddressFloorUp();
                if (nextFloor == null) {
                    nextFloor = findNextAddressFloorDown();
                }
                if (nextFloor != null) {
                    addressedFloor = nextFloor.intValue();
                }

            } else if (direction == Direction.DOWN) {
                Integer nextFloor = findNextAddressFloorDown();
                if (nextFloor == null) {
                    nextFloor = findNextAddressFloorUp();
                }
                if (nextFloor != null) {
                    addressedFloor = nextFloor.intValue();
                }
            }

        } finally {
            movementLock.unlock();
        }
    }

    private Integer findNextAddressFloorUp() {
        return stoppageFloors.stream().filter((item) -> (item > currentFloor)).findFirst().orElse(null);
    }

    private Integer findNextAddressFloorDown() {
        return stoppageFloors.stream().filter((item) -> (item < currentFloor)).sorted((o1, o2) -> o2 - o1).findFirst().orElse(null);
    }

    private void moveUpDone() {
        movementLock.lock();
        try {
            currentFloor++;
        } finally {
            movementLock.unlock();
        }
    }

    private void moveDownDone() {
        movementLock.lock();
        try {
            currentFloor--;
        } finally {
            movementLock.unlock();
        }
    }

    private boolean checkAddressedFloorToGoUp() {
        movementLock.lock();
        try {
            return currentFloor < addressedFloor;
        } finally {
            movementLock.unlock();
        }
    }

    private boolean checkAddressedFloorToGoDown() {
        movementLock.lock();
        try {
            return currentFloor > addressedFloor;
        } finally {
            movementLock.unlock();
        }
    }

    private void simulateMovementUp() {
        try {
            LOGGER.info("Elevator #" + getId() + ") moving " + getDirection() + " from " + currentFloor + " => " + (currentFloor + 1));
            Thread.sleep(500);

        } catch (InterruptedException e) {
            LOGGER.error("Error in movement time simulator: ", e);
            e.printStackTrace();
        }
    }

    private void simulateMovementDown() {
        try {
            LOGGER.info("Elevator #" + getId() + ") moving " + getDirection() + " from " + currentFloor + " => " + (currentFloor - 1));
            Thread.sleep(300);

        } catch (InterruptedException e) {
            LOGGER.error("Error in movement time simulator: ", e);
            e.printStackTrace();
        }
    }

    private void simulatePassengerEnterOrExit() {
        try {
            LOGGER.info("Elevator #" + getId() + ") floor reached: " + currentFloor + ". People are moving in/out. Next stoppages: " + stoppageFloors);
            Thread.sleep(400);
        } catch (InterruptedException e) {
            LOGGER.error("Error in movement time simulator: ");
            e.printStackTrace();
        }
    }
}
