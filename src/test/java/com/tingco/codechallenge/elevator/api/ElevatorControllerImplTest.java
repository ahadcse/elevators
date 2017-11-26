package com.tingco.codechallenge.elevator.api;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ElevatorControllerImplTest {

    @Autowired
    private ElevatorControllerImpl controller;

    @Before
    public void setup() {
        controller = new ElevatorControllerImpl(6);
    }

    @Test
    public void requestElevator() throws Exception {
        Elevator elevator = controller.requestElevator(3);
        Assert.assertNotNull(elevator);
        Assert.assertNotNull(elevator.getId());
        Assert.assertEquals(3, elevator.getAddressedFloor());
        Assert.assertEquals(false, elevator.isBusy());
    }

    @Test
    public void getElevators() throws Exception {
        List<Elevator> elevators = controller.getElevators();
        Assert.assertNotNull(elevators);
        Assert.assertEquals(false, elevators.isEmpty());
        Assert.assertEquals(6, elevators.size());
    }

    @Test
    public void releaseElevator() throws Exception {
    }

    @After
    public void destroy() {
        controller = null;
    }

}