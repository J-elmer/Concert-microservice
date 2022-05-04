package com.example.se_track_concert.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
    private final Environment env;

    private final String reviewApi;

    @Autowired
    public ReviewApiService(Environment env) {
        this.env = env;
        this.reviewApi = this.env.getProperty("review.api");
        this.webClient = WebClient.create();
    }

    /**
     * checks whether concert has any reviews associated with it
     * @param concertId of concert to check
     * @return boolean whether there are concerts or not
     */
    public boolean checkIfConcertHasReviews(long concertId) {
        String getReviewsByConcertUri = this.reviewApi + "review-by-concert?concertId=";
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
        String getReviewIdsByPerformerUri = this.reviewApi + "id-by-performer?performerId=";
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
        String deleteReviewUri = reviewApi + "delete?reviewId=";
        for (String reviewId: reviewIds) {
            webClient.delete().uri(deleteReviewUri + reviewId).retrieve().toEntity(ResponseEntity.class).block();
        }
    }
}
