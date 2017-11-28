package com.tingco.codechallenge.elevator.api;

public class ElevatorConfiguration {
    public int numberOfElevators;

    public int numberOfFloors;

    public ElevatorConfiguration(int numberOfElevators, int numberOfFloors) {
        if (numberOfElevators < 1 || numberOfFloors < 1) {
            throw new IllegalArgumentException("Illegal number of elevators / floors");
        }

        this.numberOfElevators = numberOfElevators;
        this.numberOfFloors = numberOfFloors;
    }
}
