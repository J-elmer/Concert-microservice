package com.example.se_track_concert.service;

import com.example.se_track_concert.controller.DTO.NewConcertDTO;
import com.example.se_track_concert.controller.DTO.UpdateConcertDTO;
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

import java.util.ArrayList;
import java.util.List;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final WebClient webClient;
    private final String performerUri = "http://host.docker.internal:6060/performer/check-id?id=";
    private final String deleteReviewUri = "http://host.docker.internal:7070/review/delete?reviewId=";
    private final String getReviewsUri = "http://host.docker.internal:7070/review/id-by-performer?performerId=";

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
    public void updateConcert(UpdateConcertDTO updateConcertDTO) throws ConcertNotFoundException, InvalidPerformerIdException {
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
    private Concert compareUpdateStatement(UpdateConcertDTO updateConcertDTO, Concert concertToUpdate) throws InvalidPerformerIdException {
        if (updateConcertDTO.getPerformerId() > 0 &&
                concertToUpdate.getPerformerId() != updateConcertDTO.getPerformerId()) {
            if (!this.checkIfPerformerIsValid(updateConcertDTO.getPerformerId())) {
                throw new InvalidPerformerIdException();
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

    private boolean checkIfPerformerIsValid(long performerId) {
        ResponseEntity<Boolean> response = webClient.get().uri(performerUri + performerId).retrieve().toEntity(Boolean.class).block();
        if (response != null) {
            return Boolean.TRUE.equals(response.getBody());
        }
        return false;
    }

    private ArrayList<String> getReviewsOfPerformer(long performerId) {
        Mono<Object[]> response = webClient.get().uri(getReviewsUri + performerId).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Object[].class).log();
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
