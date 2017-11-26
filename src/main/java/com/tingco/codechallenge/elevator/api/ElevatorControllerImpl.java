package com.tingco.codechallenge.elevator.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class ElevatorControllerImpl implements ElevatorController {
    private static Logger LOGGER = LoggerFactory.getLogger(ElevatorControllerImpl.class);
    private int numberOfElevators;
    private List<Elevator> elevators;

    public ElevatorControllerImpl(int numberOfElevators) {
        this.numberOfElevators = numberOfElevators;

        this.elevators = new ArrayList<>(numberOfElevators);
        for (int i = 0; i < numberOfElevators; i++) {
            this.elevators.add(new ElevatorImpl());
        }
    }

    @Override
    public Elevator requestElevator(int toFloor) {
        LOGGER.info("Searching for elevator for serving...");
        for (int i = 0; i < numberOfElevators; i++) {
            Elevator elevator = elevators.get(i);
            if (!elevator.isBusy()) {
                elevator.moveElevator(toFloor);
                return elevator;
            }
        }
        return null;
    }

    @Override
    public List<Elevator> getElevators() {
        return elevators;
    }

    @Override
    public void releaseElevator(Elevator elevator) {
    }
}
