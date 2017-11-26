package com.tingco.codechallenge.elevator.config;

import com.tingco.codechallenge.elevator.api.ElevatorControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
public class ElevatorCliRunner implements CommandLineRunner {
    @Autowired
    private Executor taskExecutor;

    @Autowired
    private ElevatorControllerImpl elevatorController;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Elevators: " + elevatorController.getElevators());

        taskExecutor.execute(() -> elevatorController.requestElevator(5));
        taskExecutor.execute(() -> elevatorController.requestElevator(10));
        taskExecutor.execute(() -> elevatorController.requestElevator(3));
        taskExecutor.execute(() -> elevatorController.requestElevator(6));
        taskExecutor.execute(() -> elevatorController.requestElevator(7));
        taskExecutor.execute(() -> elevatorController.requestElevator(2));
        taskExecutor.execute(() -> elevatorController.requestElevator(2));
    }
}
