package com.tingco.codechallenge.elevator.config;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.tingco.codechallenge.elevator.api.ElevatorController;
import com.tingco.codechallenge.elevator.api.ElevatorMovementEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@Component
public class ElevatorCliRunner implements CommandLineRunner {
    @Autowired
    private Executor taskExecutor;

    @Autowired
    private ElevatorController elevatorController;

    @Autowired
    private EventBus eventBus;

    private Map<Integer, Boolean> peopleMovedInElevator = new HashMap<>();

    @Subscribe
    public void elevatorReachedAt(ElevatorMovementEvent event) {
        if (peopleMovedInElevator.get(event.getReachedFloor()) != null) {
            return;
        }

        // This is the list of floor from where people will be inserted in the elevator and will press the destination.
        switch (event.getReachedFloor()) {
            case 1:
                event.getElevator().moveElevator(9);
                break;
            case 8:
                event.getElevator().moveElevator(3);
                break;
            case 2:
                event.getElevator().moveElevator(3);
                break;
            case 5:
                event.getElevator().moveElevator(10);
                break;
        }

        peopleMovedInElevator.put(event.getReachedFloor(), Boolean.TRUE);
    }

    @Override
    public void run(String... args) {
        eventBus.register(this);

        runCase1();
        //runCase2();
        //runCase3();
    }

    /**
     * - Total number of elevators = 2
     * - Total number of floors = 10
     * - All elevator is stopped.
     * - 4 SERIAL requests came from 4 floors. Each floor will move to different floor.
     */
    private void runCase1() {
        requestElevator(8);
        requestElevator(1);
        requestElevator(2);
        requestElevator(5);
    }

    /**
     * - Total number of elevators = 2
     * - Total number of floors = 10
     * - All elevator is stopped.
     * - 4 PARALLEL requests came from 4 floors. Each floor will move to different floor.
     */
    private void runCase2() {
        new Thread(() -> requestElevator(8)).start();
        new Thread(() -> requestElevator(1)).start();
        new Thread(() -> requestElevator(2)).start();
        new Thread(() -> requestElevator(5)).start();
    }

    /**
     * - Total number of elevators = 2
     * - Total number of floors = 10
     * - All elevator is stopped.
     * - Both elevator is busy.
     * - 4 requests came from 4 floors. Each floor will move to different floor.
     */
    private void runCase3() {
        new Thread(() -> move1stElevatorTo(5)).start();
        new Thread(() -> move2ndElevatorTo(7)).start();

        requestElevator(8);
        requestElevator(1);
        requestElevator(2);
        requestElevator(5);
    }

    private void requestElevator(int requestFromFloor) {
        taskExecutor.execute(() -> {
            try {
                elevatorController.requestElevator(requestFromFloor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void move1stElevatorTo(int destinationFloor) {
        elevatorController.getElevators().get(0).moveElevator(destinationFloor);
    }

    private void move2ndElevatorTo(int destinationFloor) {
        elevatorController.getElevators().get(1).moveElevator(destinationFloor);
    }
}
