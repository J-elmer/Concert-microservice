package com.example.se_track_concert.controller.DTO;

/**
 * Simple object used to return JsonResponses containing a String in the controller
 */
public final class JsonResponseDTO {

    private final String response;

    public JsonResponseDTO(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}

