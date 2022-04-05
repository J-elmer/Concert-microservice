package com.example.se_track_concert.service;

import com.example.se_track_concert.controller.DTO.NewConcertDTO;
import com.example.se_track_concert.controller.DTO.UpdateConcertDTO;
import com.example.se_track_concert.exception.ConcertHasReviewsException;
import com.example.se_track_concert.exception.ConcertNotFoundException;
import com.example.se_track_concert.exception.InvalidPerformerIdException;
import com.example.se_track_concert.model.Concert;
import com.example.se_track_concert.repository.ConcertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final WebClient webClient;
    private final String performerUri = "http://host.docker.internal:6060/performer/check-id?id=";
    private final String deleteReviewUri = "http://host.docker.internal:7070/review/delete?reviewId=";
    private final String getReviewIdsByPerformerUri = "http://host.docker.internal:7070/review/id-by-performer?performerId=";
    private final String getReviewsByConcertUri = "http://host.docker.internal:7070/review/review-by-concert?concertId=";

    @Autowired
    public ConcertService(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
        this.webClient = WebClient.create();
    }

    /**
     *
     * @return List of concerts
     */
    public List<Concert> getAllConcerts() {
        return this.concertRepository.findAll();
    }

    /**
     *
     * @param stage string to search for
     * @return List of concerts matching criteria
     */
    public List<Concert> getConcertsByStage(String stage) {
        return this.concertRepository.findByStageContainsIgnoreCase(stage);
    }

    /**
     *
     * @param id of concert
     * @return Concert or null
     */
    public Concert getConcertById(Long id) {
        return this.concertRepository.findConcertById(id);
    }

    /**
     *
     * @param newConcertDTO DTO class with information needed
     * @throws InvalidPerformerIdException if performer is null
     */
    public void createNewConcert(NewConcertDTO newConcertDTO) throws InvalidPerformerIdException {
        if (!this.checkIfPerformerIsValid(newConcertDTO.getPerformerId())) {
            throw new InvalidPerformerIdException();
        }
        Concert concertToBeSaved = new Concert(
                newConcertDTO.getPerformerId(),
                newConcertDTO.getDay(),
                newConcertDTO.getStage(),
                newConcertDTO.getBeginTime(),
                newConcertDTO.getEndTime());

        this.concertRepository.save(concertToBeSaved);
    }

    /**
     *
     * @param updateConcertDTO DTO class with information needed to update
     * @throws ConcertNotFoundException when concert is not found
     * @throws InvalidPerformerIdException when performer is not found
     */
    public void updateConcert(UpdateConcertDTO updateConcertDTO) throws ConcertNotFoundException, InvalidPerformerIdException, ConcertHasReviewsException {
        Concert concertToUpdate = this.concertRepository.findConcertById(updateConcertDTO.getId());
        if (concertToUpdate == null) {
            throw new ConcertNotFoundException();
        }
        this.concertRepository.save(this.compareUpdateStatement(updateConcertDTO, concertToUpdate));
    }

    /**
     * helper method which compares the DTO update statement with the given concert and returns a concert that can be saved to the db
     * @param updateConcertDTO DTO class with update statement
     * @param concertToUpdate concert that needs to be updated
     * @return Concert that can be saved to db
     * @throws InvalidPerformerIdException if performer is not found
     */
    private Concert compareUpdateStatement(UpdateConcertDTO updateConcertDTO, Concert concertToUpdate) throws InvalidPerformerIdException, ConcertHasReviewsException {
        if (updateConcertDTO.getPerformerId() > 0 &&
                concertToUpdate.getPerformerId() != updateConcertDTO.getPerformerId()) {
            if (!this.checkIfPerformerIsValid(updateConcertDTO.getPerformerId())) {
                throw new InvalidPerformerIdException();
            }
            if (this.checkIfConcertHasReviews(concertToUpdate.getId())) {
                throw new ConcertHasReviewsException();
            }
            concertToUpdate.setPerformerId(updateConcertDTO.getPerformerId());
        }
        if (updateConcertDTO.getDay() != null && updateConcertDTO.getDay() != concertToUpdate.getDay()) {
            concertToUpdate.setDay(updateConcertDTO.getDay());
        }
        if (updateConcertDTO.getStage() != null && !updateConcertDTO.getStage().equals(concertToUpdate.getStage())) {
            concertToUpdate.setStage(updateConcertDTO.getStage());
        }
        if (updateConcertDTO.getBeginTime() != null && updateConcertDTO.getBeginTime() != concertToUpdate.getBeginTime()) {
            concertToUpdate.setBeginTime(updateConcertDTO.getBeginTime());
        }
        if (updateConcertDTO.getEndTime() != null && updateConcertDTO.getEndTime() != concertToUpdate.getEndTime()) {
            concertToUpdate.setEndTime(updateConcertDTO.getEndTime());
        }
        return concertToUpdate;
    }

    /**
     *
     * @param id of concert
     * @throws ConcertNotFoundException when concert is not found
     */
    public void deleteConcert(Long id) throws ConcertNotFoundException {
        Concert concertToDelete = this.concertRepository.findConcertById(id);
        if (concertToDelete == null) {
            throw new ConcertNotFoundException();
        }
        ArrayList<String> reviewIds = getReviewsOfPerformer(concertToDelete.getPerformerId());
        this.deleteReviews(reviewIds);
        this.concertRepository.delete(concertToDelete);
    }

    /**
     * returns concerts with a certain performer id
     * @param performerId of performer
     * @return List of concerts
     * @throws InvalidPerformerIdException if performer is not found
     */
    public List<Concert> getConcertsByPerformerId(long performerId) throws InvalidPerformerIdException {
        if (!checkIfPerformerIsValid(performerId)) {
            throw new InvalidPerformerIdException();
        }
        return this.concertRepository.findConcertByPerformerId(performerId);
    }

    /**
     * find concerts after certain date
     * @param date to compare
     * @return list of concerts after this date
     */
    public List<Concert> getConcertsAfterDate(LocalDate date) {
        return this.concertRepository.findByDayAfter(date);
    }

    /**
     * find concerts before certain date
     * @param date to compare
     * @return list of concerts after this date
     */
    public List<Concert> getConcertsBeforeDate(LocalDate date) {
        return this.concertRepository.findByDayBefore(date);
    }

    private boolean checkIfPerformerIsValid(long performerId) {
        ResponseEntity<Boolean> response = webClient.get().uri(performerUri + performerId).retrieve().toEntity(Boolean.class).block();
        if (response != null) {
            return Boolean.TRUE.equals(response.getBody());
        }
        return false;
    }

    private boolean checkIfConcertHasReviews(long concertId) {
        Mono<Object[]> response = webClient.get().uri(getReviewsByConcertUri + concertId).
                accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Object[].class).log();
        Object[] objects = response.block();
        if (objects.length > 0) {
            return true;
        }
        return false;
    }

    private ArrayList<String> getReviewsOfPerformer(long performerId) {
        Mono<Object[]> response = webClient.get().uri(getReviewIdsByPerformerUri + performerId).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Object[].class).log();
        Object[] objects = response.block();
        ArrayList<String> reviewIds = new ArrayList<>();
        if (objects != null) {
            for (Object object: objects) {
                reviewIds.add(object.toString());
            }
        }
        return reviewIds;
    }

    private void deleteReviews(ArrayList<String> reviewIds) {
        for (String reviewId: reviewIds) {
            webClient.delete().uri(deleteReviewUri + reviewId).retrieve().toEntity(ResponseEntity.class).block();
        }
    }
}
