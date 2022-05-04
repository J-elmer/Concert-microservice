package com.example.se_track_concert.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

/**
 * Service to communicate with reviewApi
 */
@Service
public class ReviewApiService {

    private final WebClient webClient;

    private final String deleteReviewUri = "http://host.docker.internal:7070/review/delete?reviewId=";
    private final String getReviewIdsByPerformerUri = "http://host.docker.internal:7070/review/id-by-performer?performerId=";
    private final String getReviewsByConcertUri = "http://host.docker.internal:7070/review/review-by-concert?concertId=";

    @Autowired
    public ReviewApiService() {
        this.webClient = WebClient.create();
    }

    /**
     * checks whether concert has any reviews associated with it
     * @param concertId of concert to check
     * @return boolean whether there are concerts or not
     */
    public boolean checkIfConcertHasReviews(long concertId) {
        Mono<Object[]> response = webClient.get().uri(getReviewsByConcertUri + concertId).
                accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Object[].class).log();
        Object[] objects = response.block();
        assert objects != null;
        return objects.length > 0;
    }

    /**
     * retrieves reviews associated with a performer
     * @param performerId of performer
     * @return list of reviews
     */
    public ArrayList<String> getReviewsOfPerformer(long performerId) {
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

    /**
     * deletes reviews
     * @param reviewIds revieId's to delete
     */
    public void deleteReviews(ArrayList<String> reviewIds) {
        for (String reviewId: reviewIds) {
            webClient.delete().uri(deleteReviewUri + reviewId).retrieve().toEntity(ResponseEntity.class).block();
        }
    }
}
