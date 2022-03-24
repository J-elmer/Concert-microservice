package com.example.se_track_concert.controller.DTO;

public final class ValidReviewDTO {

    private boolean canBeReviewed;
    private long performerId;

    public ValidReviewDTO(boolean canBeReviewed, long performerId) {
        this.canBeReviewed = canBeReviewed;
        this.performerId = performerId;
    }

    public boolean isCanBeReviewed() {
        return canBeReviewed;
    }

    public void setCanBeReviewed(boolean canBeReviewed) {
        this.canBeReviewed = canBeReviewed;
    }

    public long getPerformerId() {
        return performerId;
    }

    public void setPerformerId(long performerId) {
        this.performerId = performerId;
    }
}
