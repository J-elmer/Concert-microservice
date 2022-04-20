package com.example.se_track_concert.service;

import com.example.se_track_concert.controller.DTO.NewConcertDTO;
import com.example.se_track_concert.exception.InvalidPerformerIdException;
import com.example.se_track_concert.model.Concert;
import com.example.se_track_concert.repository.ConcertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @InjectMocks
    private ConcertService concertService;

    private static Concert concertUnderTest1 = new Concert(1, LocalDate.of(2020,10,10), "test", LocalTime.now(), LocalTime.now());
    private static Concert concertUnderTest2 = new Concert(2, LocalDate.of(2090,10,10), "stage", LocalTime.now(), LocalTime.now());
    @Mock
    private ConcertRepository concertRepository;

    @Test
    void getAllConcerts() {
        Mockito.when(this.concertRepository.findAll()).thenReturn(List.of(concertUnderTest1, concertUnderTest2));
        assertEquals(List.of(concertUnderTest1, concertUnderTest2), this.concertService.getAllConcerts());
    }

    @Test
    void getConcertsByStage() {
        Mockito.when(this.concertRepository.findByStageContainsIgnoreCase("test")).thenReturn((List.of(concertUnderTest1)));
        assertEquals(List.of(concertUnderTest1), this.concertService.getConcertsByStage("test"));
    }

    @Test
    void getConcertById() {
        Mockito.when(this.concertRepository.findConcertById(1L)).thenReturn(concertUnderTest1);
        assertEquals(concertUnderTest1, this.concertService.getConcertById(1L));
        assertNotEquals(concertUnderTest1, this.concertService.getConcertById(2L));
    }

    @Test
    void createNewConcert() throws InvalidPerformerIdException {
        // TODO figure out how to mock the api call
        fail();
    }

    @Test
    void updateConcert() {
        // TODO figure out how to mock the api call
        fail();
    }

    @Test
    void deleteConcert() {
        // TODO figure out how to mock the api call
        fail();
    }

    @Test
    void getConcertsByPerformerId() {
        // TODO figure out how to mock the api call
        fail();
    }

    @Test
    void getConcertsAfterDate() {
        Mockito.when(this.concertRepository.findByDayAfter(LocalDate.now())).thenReturn(List.of(concertUnderTest2));
        assertEquals(List.of(concertUnderTest2), this.concertService.getConcertsAfterDate(LocalDate.now()));
    }

    @Test
    void getConcertsBeforeDate() {
        Mockito.when(this.concertRepository.findByDayBefore(LocalDate.now())).thenReturn(List.of(concertUnderTest1));
        assertEquals(List.of(concertUnderTest1), this.concertService.getConcertsBeforeDate(LocalDate.now()));
    }
}