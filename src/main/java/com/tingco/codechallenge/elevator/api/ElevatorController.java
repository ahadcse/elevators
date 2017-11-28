package com.tingco.codechallenge.elevator.api;

import java.util.List;


/**
 * Interface for the Elevator Controller.
 *
 * @author Sven Wesley
 *
 */
public interface ElevatorController {

    /**
     * Request an elevator to the specified floor.
     *
     * In real-life: we stand in front of a set of elevators at level "toFloor" => press a single common button to call for any elevator.
     * This method reflects this behavior.
     *
     * @param toFloor addressed floor as integer.
     * @return The Elevator that is going to the floor, if there is one to move.
     */
    Elevator requestElevator(int toFloor);

    /**
     * A snapshot list of all elevators in the system.
     *
     * @return A List with all {@link Elevator} objects.
     */
    List<Elevator> getElevators();

    /**
     * Telling the controller that the given elevator is free for new operations.
     *
     * In real-life: we stop at our destination floor after entering into elevator.
     * This method reflects this behavior.
     *
     * @param elevator the elevator that shall be released.
     */
    void releaseElevator(Elevator elevator);

}
