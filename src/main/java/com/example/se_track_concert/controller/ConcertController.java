package com.example.se_track_concert.controller;

import com.example.se_track_concert.controller.DTO.JsonResponseDTO;
import com.example.se_track_concert.controller.DTO.NewConcertDTO;
import com.example.se_track_concert.controller.DTO.UpdateConcertDTO;
import com.example.se_track_concert.controller.DTO.ValidReviewDTO;
import com.example.se_track_concert.exception.ConcertNotFoundException;
import com.example.se_track_concert.exception.PerformerNotFoundException;
import com.example.se_track_concert.model.Concert;
import com.example.se_track_concert.service.ConcertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("concert")
public class ConcertController {

    private final ConcertService concertService;

    @Autowired
    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    /**
     * endpoint to get all concerts
     *
     * @return List of all concerts in db
     */
    @GetMapping(value = "/all")
    public List<Concert> getAllConcerts() {
        return this.concertService.getAllConcerts();
    }

    /**
     * endpoint to search concerts by stage name
     *
     * @param stage search parameter
     * @return List of concerts matching the criteria
     */
    @GetMapping(value = "/by-stage")
    public List<Concert> getConcertsByStage(@RequestParam String stage) {
        return this.concertService.getConcertsByStage(stage);
    }

    /**
     * endpoint to get a concert by id
     *
     * @param id of concert
     * @return Concert
     */
    @GetMapping("/{id}")
    public Concert getById(@PathVariable Long id) {
        Concert concert = this.concertService.getConcertById(id);
        if (concert == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Concert with id " + id + " not found");
        }
        return concert;
    }

    /**
     * endpoint to create new concert with a simple dto class
     *
     * @param newConcertDTO contains information necessary to save the concert
     * @return HttpStatus 200 if everything went well, otherwise 400
     */
    @PostMapping("/new")
    public ResponseEntity<?> createNewConcert(@Validated @RequestBody NewConcertDTO newConcertDTO) {
        try {
            this.concertService.createNewConcert(newConcertDTO);
        } catch (PerformerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body(new JsonResponseDTO("No performer found with id " + newConcertDTO.getPerformerId()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    /**
     * endpoint to update concert. Multiple checks are done to ensure only an existing concert is updated
     *
     * @param updateConcertDTO information needed for the update
     * @return HttpStatus 200 if all went well, 400 if something went wrong
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateConcert(@Validated @RequestBody UpdateConcertDTO updateConcertDTO) {
        try {
            this.concertService.updateConcert(updateConcertDTO);
        } catch (ConcertNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseDTO("Invalid concert ID"));
        } catch (PerformerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body(new JsonResponseDTO("No performer found with id " + updateConcertDTO.getPerformerId()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /**
     * endpoint to delete a concert by id.
     *
     * @param id of concert
     * @return HttpStatus 200 if all went well, 400 if something went wrong
     */
    @DeleteMapping(value = "/delete")
    public ResponseEntity<?> deleteConcert(@RequestParam Long id) {
        try {
            this.concertService.deleteConcert(id);
        } catch (ConcertNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body(new JsonResponseDTO("No concert found with id " + id));
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /**
     * endpoint to check if a concert can be reviewed, it checks if it exists and whether the date has passed
     *
     * @param id of concert
     * @return HttpStatus 200 if it can be reviewed, else 400
     */
    @GetMapping(value = "/valid-review")
    public ResponseEntity<?> checkIfConcertCanBeReviewed(@RequestParam Long id) {
        Concert concert = this.concertService.getConcertById(id);
        System.out.println(concert);
        if (concert == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new JsonResponseDTO("No concert found with id " + id));
        }
        if (concert.getDay().isAfter(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseDTO("Concert has not been performed yet, no review possible"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ValidReviewDTO(true, concert.getPerformerId()));
    }
}
