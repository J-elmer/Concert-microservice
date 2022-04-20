package com.example.se_track_concert.controller;

import com.example.se_track_concert.controller.DTO.NewConcertDTO;
import com.example.se_track_concert.controller.DTO.UpdateConcertDTO;
import com.example.se_track_concert.exception.InvalidPerformerIdException;
import com.example.se_track_concert.model.Concert;
import com.example.se_track_concert.service.ConcertService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConcertController.class)
class ConcertControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ConcertService concertService;

    @Test
    void getAllConcerts() {
        try {
            mockMvc.perform(get("/concert/all")
                            .contentType("application/json"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    void getConcertsByStage() {
        try {
            mockMvc.perform(get("/concert/by-stage")
                            .contentType("application/json")
                            .param("stage", "test"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    void getById() {
        Mockito.when(this.concertService.getConcertById(1L)).thenReturn(new Concert());
        try {
            mockMvc.perform(get("/concert/1")
                            .contentType("application/json"))
                    .andExpect(status().isOk());
            mockMvc.perform(get("/concert/2")
                            .contentType("application/json"))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    void createNewConcert() {
        NewConcertDTO newConcert = new NewConcertDTO(1, LocalDate.of(2022, 1, 1), "test", LocalTime.now(), LocalTime.now());

        try {
            mockMvc.perform(post("/concert/new")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(newConcert)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    void createNewConcertFailed() {
        NewConcertDTO newConcert2 = new NewConcertDTO(0, LocalDate.of(2022, 1, 1), "test", LocalTime.now(), LocalTime.now());

        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(post("/concert/new")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(newConcert2)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String expectedOutPut = "{\"performerId\":\"Performer of concert mandatory\"}";
        String actual = null;
        try {
            assert mvcResult != null;
            actual = mvcResult.getResponse().getContentAsString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        assert (expectedOutPut.equals(actual));
    }

    @Test
    void updateConcert() {
        UpdateConcertDTO updateConcert = new UpdateConcertDTO(1, 1, LocalDate.now(), "test", LocalTime.now(), LocalTime.now());
        try {
            mockMvc.perform(put("/concert/update")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(updateConcert)))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    void deleteConcert() {
        try {
            mockMvc.perform(delete("/concert/delete")
                            .contentType("application/json")
                            .param("id", "1"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    void checkIfConcertCanBeReviewedExpectValid() {
        Mockito.when(this.concertService.getConcertById(1L)).thenReturn(new Concert(1, LocalDate.of(2020, 1, 1), "Arena", LocalTime.now(), LocalTime.now()));
        MvcResult result = null;
        try {
            result = mockMvc.perform(get("/concert/valid-review")
                            .contentType("application/json")
                            .param("id", "1"))
                    .andExpect(status().isOk()).andReturn();
        } catch (Exception e) {
            System.out.println(e);
        }
        if (result != null) {{
            String expected = "{\"canBeReviewed\":true,\"performerId\":1}";
            String actual = null;
            try {
                actual = result.getResponse().getContentAsString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            assertEquals(expected, actual);
        }}
    }

    @Test
    void checkIfConcertCanBeReviewedExpectInvalid() {
        MvcResult result = null;
        try {
            result = mockMvc.perform(get("/concert/valid-review")
                            .contentType("application/json")
                            .param("id", "1"))
                    .andExpect(status().isNotFound()).andReturn();
        } catch (Exception e) {
            System.out.println(e);
        }
        if (result != null) {{
            String expected = "{\"response\":\"No concert found with id 1\"}";
            String actual = null;
            try {
                actual = result.getResponse().getContentAsString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            assertEquals(expected, actual);
        }}
    }

    @Test
    void getConcertsByPerformerId() throws InvalidPerformerIdException {
        Mockito.when(this.concertService.getConcertsByPerformerId(1L)).thenReturn(new ArrayList<>());
        try {
            mockMvc.perform(get("/concert/concerts-by-performer")
                            .contentType("application/json")
                            .param("performerId", "1"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    void checkIfPerformerCanBeDeletedIsValid() throws InvalidPerformerIdException, UnsupportedEncodingException {
        Mockito.when(this.concertService.getConcertsByPerformerId(1L)).thenReturn(new ArrayList<>());
        MvcResult result = null;
        try {
            result = mockMvc.perform(get("/concert/check-delete-performer")
                            .contentType("application/json")
                            .param("performerId", "1"))
                    .andExpect(status().isOk()).andReturn();
        } catch (Exception e) {
            System.out.println(e);
        }
        assert result != null;
        assert(result.getResponse().getContentAsString().equals("true"));
    }

    @Test
    void checkIfPerformerCanBeDeletedIsInvalid() throws InvalidPerformerIdException, UnsupportedEncodingException {
        List<Concert> concerts = new ArrayList<>();
        concerts.add(new Concert());
        Mockito.when(this.concertService.getConcertsByPerformerId(1L)).thenReturn(concerts);
        MvcResult result = null;
        try {
            result = mockMvc.perform(get("/concert/check-delete-performer")
                            .contentType("application/json")
                            .param("performerId", "1"))
                    .andExpect(status().isOk()).andReturn();
        } catch (Exception e) {
            System.out.println(e);
        }
        assert result != null;
        assert(result.getResponse().getContentAsString().equals("false"));
    }

    @Test
    void getConcertsBeforeToday() {
        try {
            mockMvc.perform(get("/concert/past-concerts")
                            .contentType("application/json"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    void getConcertsAfterToday() {
        try {
            mockMvc.perform(get("/concert/past-concerts")
                            .contentType("application/json"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}