package com.tingco.codechallenge.elevator.api;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ElevatorImplTest {

    private Elevator elevator;

    @Before
    public void setup() {
        elevator = new ElevatorImpl();
    }

    @Test
    public void getDirection() throws Exception {
        elevator.moveElevator(3);
        Assert.assertEquals(Elevator.Direction.NONE, elevator.getDirection());

    }

    @Test
    public void getAddressedFloor() throws Exception {
        elevator.moveElevator(4);
        Assert.assertEquals(4, elevator.getAddressedFloor());
    }

    @Test
    public void getId() throws Exception {
    }

    @Test
    public void moveElevator() throws Exception {
        elevator.moveElevator(3);
        Assert.assertEquals(3, elevator.getAddressedFloor());
        Assert.assertEquals(3, elevator.currentFloor());
        Assert.assertEquals(false, elevator.isBusy());

    }

    @Test
    public void isBusy() throws Exception {
    }

    @Test
    public void currentFloor() throws Exception {
        elevator.moveElevator(3);
        Assert.assertEquals(3, elevator.currentFloor());
    }

    @After
    public void destroy() {
        elevator = null;
    }

}