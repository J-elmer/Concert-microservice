package com.example.se_track_concert.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Service class to retrieve information from performerApi
 */
@Service
public class PerformerApiService {

    private final WebClient webClient;
    private final Environment env;

    @Autowired
    public PerformerApiService(Environment env) {
        this.env = env;
        this.webClient = WebClient.create();
    }

    /**
     * checks if the performer exists
     * @param performerId of performer to check
     * @return boolean whether performer exists
     */
    public boolean checkIfPerformerIsValid(long performerId) {
        String apiUrl = this.env.getProperty("performer.api") + "check-id?id=";
        ResponseEntity<Boolean> response = webClient.get().uri(apiUrl + performerId).retrieve().toEntity(Boolean.class).block();
        if (response != null) {
            return Boolean.TRUE.equals(response.getBody());
        }
        return false;
    }
}
