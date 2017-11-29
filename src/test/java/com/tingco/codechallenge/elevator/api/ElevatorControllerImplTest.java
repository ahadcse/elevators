package com.tingco.codechallenge.elevator.api;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.tingco.codechallenge.elevator.config.ElevatorApplication;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ElevatorApplication.class)
public class ElevatorControllerImplTest {

    @Autowired
    private ElevatorController controller;

    @Autowired
    private EventBus eventBus;

    private static ElevatorReachListener elevatorReachListener = null;

    @Before
    public void setup() {
    }

    @After
    public void destroy() {
    }

    @Test
    public void requestElevator() throws Exception {
        initElevatorReachListener();

        Elevator elevator = controller.requestElevator(7);
        Assert.assertNotNull(elevator);
        Assert.assertNotNull(elevator.getId());
        Assert.assertEquals(true, elevator.isBusy());
        Assert.assertEquals(7, elevator.getAddressedFloor()); /* This is where the elevator is destined. */

        Assert.assertTrue(elevator.currentFloor() < 1); /* Elevator should not reach 1st floor yet */
        Assert.assertEquals("NONE", elevator.getDirection().name()); /* Elevator will move around after 100ms */

        sleep(1800); /* To go up, it takes 500 ms. So, with 1800ms, elevator should be in between 3-5th floor. */
        Assert.assertTrue(elevator.currentFloor() >= 3 && elevator.currentFloor() <= 5); /* Elevator is still moving */
        Assert.assertEquals("UP", elevator.getDirection().name());
        Assert.assertEquals(0, elevatorReachListener.reachedFloors.size()); /* Still no reach */

        elevator.moveElevator(0); /* Mean while, someone requested this very elevator from ground floor */

        sleep(3000); /* Withing 3000ms, elevator will reach 7th floor and then start coming down to 0-th floor. */
        Assert.assertTrue(elevator.currentFloor() < 6); /* Elevator is still moving */
        Assert.assertEquals("DOWN", elevator.getDirection().name());
        Assert.assertEquals(1, elevatorReachListener.reachedFloors.size());
        Assert.assertTrue(elevatorReachListener.reachedFloors.contains(7)); /* Elevator reached the 7th floor already */

        sleep(4000); /* Withing 3000ms, elevator will reach ground floor and then stop. */
        Assert.assertTrue(elevator.currentFloor() == 0);
        Assert.assertEquals("NONE", elevator.getDirection().name());
        Assert.assertEquals(2, elevatorReachListener.reachedFloors.size());
        Assert.assertTrue(elevatorReachListener.reachedFloors.contains(7)); /* Elevator reached the 7th floor already */
        Assert.assertTrue(elevatorReachListener.reachedFloors.contains(0)); /* Elevator reached the 0th floor also */
    }

    @Test
    public void getElevators() throws Exception {
        List<Elevator> elevators = controller.getElevators();
        Assert.assertNotNull(elevators);
        Assert.assertEquals(false, elevators.isEmpty());
        Assert.assertEquals(6, elevators.size());
    }

    @Component
    public static class ElevatorReachListener {
        private List<Integer> reachedFloors = new ArrayList<>();

        /**
         * This method is to simulate people entering elevator and pressing the destination floor.
         *
         * @param event occurred event.
         */
        @Subscribe
        public void elevatorReachedAt(ElevatorMovementEvent event) {
            reachedFloors.add(event.getReachedFloor());
        }
    }

    private void initElevatorReachListener() {
        if (elevatorReachListener == null) {
            elevatorReachListener = new ElevatorReachListener();
            eventBus.register(elevatorReachListener);
        }
        elevatorReachListener.reachedFloors.clear();
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}