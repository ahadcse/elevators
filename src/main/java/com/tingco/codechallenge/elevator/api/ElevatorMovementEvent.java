package com.tingco.codechallenge.elevator.api;

public class ElevatorMovementEvent {
    private Elevator elevator;
    private int reachedFloor;

    public ElevatorMovementEvent(Elevator elevator, int reachedFloor) {
        this.elevator = elevator;
        this.reachedFloor = reachedFloor;
    }

    public Elevator getElevator() {
        return elevator;
    }

    public int getReachedFloor() {
        return reachedFloor;
    }
}
