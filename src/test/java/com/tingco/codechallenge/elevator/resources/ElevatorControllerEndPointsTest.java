package com.tingco.codechallenge.elevator.resources;

import com.tingco.codechallenge.elevator.api.Elevator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tingco.codechallenge.elevator.config.ElevatorApplication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Boiler plate test class to get up and running with a test faster.
 *
 * @author Sven Wesley
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ElevatorApplication.class)
public class ElevatorControllerEndPointsTest {

    private MockMvc mockMvc;

    @Autowired
    private ElevatorControllerEndPoints endPoints;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(endPoints).build();
    }

    @Test
    public void ping() {
        Assert.assertEquals("pong", endPoints.ping());
    }

    @Test
    public void request() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
        .get("/rest/v1/request?toFloor=3")
        .accept(MediaType.ALL))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(jsonPath("$.responseCode", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.responseMessage", is("Elevator found for serving")))
                .andExpect(jsonPath("$.responseBody", is(notNullValue())))
                .andExpect(jsonPath("$.responseBody.id", is(notNullValue())))
                .andExpect(jsonPath("$.responseBody.direction", is("NONE")))
                .andExpect(jsonPath("$.responseBody.addressedFloor", is(notNullValue())))
                .andExpect(jsonPath("$.responseBody.addressedFloor", is(3)))
                .andExpect(jsonPath("$.responseBody.busy", is(false)))
        ;
    }

    @Test
    public void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/rest/v1/get")
                .accept(MediaType.ALL))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(jsonPath("$.responseCode", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.responseMessage", is("Elevator list found")))
                .andExpect(jsonPath("$.responseBody", is(notNullValue())))
        ;

    }

    @Test
    public void release() throws Exception {
    }

    @After
    public void destroy() {
        mockMvc = null;
    }

}
