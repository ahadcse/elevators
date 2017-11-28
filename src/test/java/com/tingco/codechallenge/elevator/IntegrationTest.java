package com.tingco.codechallenge.elevator;

import com.tingco.codechallenge.elevator.api.ElevatorControllerImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tingco.codechallenge.elevator.config.ElevatorApplication;

import java.util.concurrent.Executor;

/**
 * Boiler plate test class to get up and running with a test faster.
 *
 * @author Sven Wesley
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ElevatorApplication.class)
public class IntegrationTest {

    @Autowired
    private Executor taskExecutor;

    @Autowired
    private ElevatorControllerImpl elevatorController;


    @Test
    public void simulateAnElevatorShaft() {
    }

}
