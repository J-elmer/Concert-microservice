package com.example.se_track_concert.service;

import com.example.se_track_concert.controller.DTO.NewConcertDTO;
import com.example.se_track_concert.controller.DTO.UpdateConcertDTO;
import com.example.se_track_concert.exception.ConcertNotFoundException;
import com.example.se_track_concert.exception.PerformerNotFoundException;
import com.example.se_track_concert.model.Concert;
import com.example.se_track_concert.repository.ConcertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;

    @Autowired
    public ConcertService(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
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
     * @throws PerformerNotFoundException if performer is null
     */
    public void createNewConcert(NewConcertDTO newConcertDTO) throws PerformerNotFoundException {
        // TODO check if performerId is valid
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
     * @throws PerformerNotFoundException when performer is not found
     */
    public void updateConcert(UpdateConcertDTO updateConcertDTO) throws ConcertNotFoundException, PerformerNotFoundException {
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
     * @throws PerformerNotFoundException if performer is not found
     */
    private Concert compareUpdateStatement(UpdateConcertDTO updateConcertDTO, Concert concertToUpdate) throws PerformerNotFoundException {
        if (updateConcertDTO.getPerformerId() > 0 &&
                concertToUpdate.getPerformerId() != updateConcertDTO.getPerformerId()) {
            // TODO Check if performerId is valid!
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
        // TODO: delete reviews too
        if (concertToDelete == null) {
            throw new ConcertNotFoundException();
        }
        this.concertRepository.delete(concertToDelete);
    }
}
