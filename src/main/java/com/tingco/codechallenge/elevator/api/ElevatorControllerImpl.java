package com.tingco.codechallenge.elevator.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ElevatorControllerImpl implements ElevatorController {
    private static Logger LOGGER = LoggerFactory.getLogger(ElevatorControllerImpl.class);

    @Autowired
    private ElevatorConfiguration config;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    private List<Elevator> elevators;

    public ElevatorControllerImpl() {
    }

    public void init() {
        this.elevators = new ArrayList<>(config.numberOfElevators);
        for (int i = 0; i < config.numberOfElevators; i++) {
            /* Assumption: All elevators are having capability to reach the same floor */
            ElevatorImpl elevator = new ElevatorImpl();
            beanFactory.autowireBean(elevator);
            this.elevators.add(elevator);
        }
    }

    @Override
    public Elevator requestElevator(int toFloor) {
        LOGGER.info("Searching for elevator for serving...");

        Elevator closetElevator = findClosestElevator(toFloor);
        if (closetElevator != null) {
            closetElevator.moveElevator(toFloor);
            return closetElevator;
        }

        throw new IllegalStateException("We should get at least 1 elevator served.");
    }

    private Elevator findClosestElevator(int toFloor) {
        //Let's pick random now.
        return elevators.get(new Random().nextInt(config.numberOfElevators));
    }

    @Override
    public List<Elevator> getElevators() {
        return elevators;
    }

    @Override
    public void releaseElevator(Elevator elevator) {
        LOGGER.info("Releasing the elevator...");
        elevator.releaseElevator();
    }
}
