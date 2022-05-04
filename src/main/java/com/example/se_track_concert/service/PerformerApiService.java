package com.example.se_track_concert.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Service class to retrieve information from performerApi
 */
@Service
public class PerformerApiService {

    private final WebClient webClient;
    private final String performerUri = "http://host.docker.internal:6060/performer/check-id?id=";

    @Autowired
    public PerformerApiService() {
        this.webClient = WebClient.create();
    }

    /**
     * checks if the performer exists
     * @param performerId of performer to check
     * @return boolean whether performer exists
     */
    public boolean checkIfPerformerIsValid(long performerId) {
        ResponseEntity<Boolean> response = webClient.get().uri(performerUri + performerId).retrieve().toEntity(Boolean.class).block();
        if (response != null) {
            return Boolean.TRUE.equals(response.getBody());
        }
        return false;
    }
}
