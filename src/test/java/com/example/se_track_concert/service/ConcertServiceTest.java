package com.example.se_track_concert.service;

import com.example.se_track_concert.controller.DTO.NewConcertDTO;
import com.example.se_track_concert.controller.DTO.UpdateConcertDTO;
import com.example.se_track_concert.exception.ConcertHasReviewsException;
import com.example.se_track_concert.exception.ConcertNotFoundException;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @InjectMocks
    private ConcertService concertService;

    @Mock
    private ConcertRepository concertRepository;
    @Mock
    private PerformerApiService performerApiService;
    @Mock
    private ReviewApiService reviewApiService;

    private static Concert concertUnderTest1 = new Concert(1, LocalDate.of(2020,10,10), "test", LocalTime.of(10, 0), LocalTime.of(10, 0));
    private static Concert concertUnderTest2 = new Concert(2, LocalDate.of(2090,10,10), "stage", LocalTime.of(10, 0), LocalTime.of(10, 0));

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
        Mockito.when(performerApiService.checkIfPerformerIsValid(1)).thenReturn(true);

        NewConcertDTO newConcert = new NewConcertDTO(1, LocalDate.of(2020,10,10), "test", LocalTime.of(10, 0), LocalTime.of(10, 0));
        NewConcertDTO newConcert2 = new NewConcertDTO(2, LocalDate.of(2090,10,10), "stage", LocalTime.of(10, 0), LocalTime.of(10, 0));

        this.concertService.createNewConcert(newConcert);
        verify(this.concertRepository, times(1)).save(concertUnderTest1);
        assertThrows(InvalidPerformerIdException.class, () -> this.concertService.createNewConcert(newConcert2));
    }

    @Test
    void updateConcert() throws ConcertHasReviewsException, InvalidPerformerIdException, ConcertNotFoundException {
        Mockito.when(this.concertRepository.findConcertById(1L)).thenReturn(concertUnderTest1);

        UpdateConcertDTO updateConcert = new UpdateConcertDTO(1, 1, LocalDate.now(), "test", LocalTime.of(15, 0), LocalTime.of(15, 0));
        UpdateConcertDTO updateConcert2 = new UpdateConcertDTO(2, 2, LocalDate.now(), "stage", LocalTime.of(15, 0), LocalTime.of(15, 0));

        this.concertService.updateConcert(updateConcert);
        verify(this.concertRepository, times(1)).save(concertUnderTest1);
        assertThrows(ConcertNotFoundException.class, () -> this.concertService.updateConcert(updateConcert2));

    }

    @Test
    void deleteConcert() throws ConcertNotFoundException {
        Mockito.when(this.concertRepository.findConcertById(1L)).thenReturn(concertUnderTest1);
        Mockito.when(this.reviewApiService.getReviewsOfPerformer(1L)).thenReturn(new ArrayList<>());

        this.concertService.deleteConcert(1L);
        verify(this.concertRepository, times(1)).delete(concertUnderTest1);
    }

    @Test
    void getConcertsByPerformerId() throws InvalidPerformerIdException {
        Mockito.when(performerApiService.checkIfPerformerIsValid(1)).thenReturn(true);
        Mockito.when(this.concertRepository.findConcertByPerformerId(1L)).thenReturn(new ArrayList<>());

        this.concertService.getConcertsByPerformerId(1L);

        verify(this.concertRepository, times(1)).findConcertByPerformerId(1L);
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