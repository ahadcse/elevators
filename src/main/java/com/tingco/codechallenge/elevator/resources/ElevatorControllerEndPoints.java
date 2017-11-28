package com.tingco.codechallenge.elevator.resources;

import com.tingco.codechallenge.elevator.api.*;
import com.tingco.codechallenge.elevator.exception.ElevatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;

/**
 * Rest Resource.
 *
 * @author Sven Wesley
 */
@RestController
@RequestMapping("/rest/v1")
public final class ElevatorControllerEndPoints {

    private static Logger LOGGER = LoggerFactory.getLogger(ElevatorControllerEndPoints.class);

    @Autowired
    private ElevatorController elevatorController;

    @Autowired
    private ElevatorConfiguration elevatorConfiguration;

    /**
     * Ping service to test if we are alive.
     *
     * @return String pong
     */
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping() {
        return "pong";
    }

    @RequestMapping(value = "/request", method = RequestMethod.GET)
    public ApiResponse request(@RequestParam(value = "toFloor", required = true) int toFloor) {
        ApiResponse<Elevator> apiResponse = null;
        LOGGER.info("request controller started...");
        Elevator elevator = elevatorController.requestElevator(toFloor);
        if (elevator == null) {
            LOGGER.info("All elevators are busy.");
            throw new ElevatorException("All elevators are busy");
        }
        apiResponse = new ApiResponse<>(200, "Elevator found for serving");
        apiResponse.setResponseBody(elevator);
        return apiResponse;
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ApiResponse get() {
        ApiResponse<List<Elevator>> apiResponse;
        LOGGER.info("get controller started...");
        List<Elevator> elevators = elevatorController.getElevators();
        if (elevators.isEmpty()) {
            LOGGER.error("No elevator found");
            throw new ElevatorException("No elevator list found");
        }
        apiResponse = new ApiResponse<>(200, "Elevator list found");
        apiResponse.setResponseBody(elevators);
        return apiResponse;
    }

    @RequestMapping(value = "/release", method = RequestMethod.GET)
    public void release(@RequestParam(value = "id", required = true) int id) {
        LOGGER.info("release controller started...");
        if (id < 0 || id > elevatorConfiguration.numberOfFloors) {
            LOGGER.error("Cannot release invaid elevator");
            throw new ElevatorException("Cannot release invaid elevator");
        }
        try {
            Elevator elevator = elevatorController.getElevators().get(id);
            elevatorController.releaseElevator(elevator);
        } catch (ElevatorException e) {
            throw new ElevatorException("No elevator exists with Id: " + id);
        }
    }
}
