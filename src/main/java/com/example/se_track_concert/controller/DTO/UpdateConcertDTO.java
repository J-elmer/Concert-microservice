package com.example.se_track_concert.controller.DTO;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalTime;

public final class UpdateConcertDTO {

    @Positive(message = "ConcertId required in order to update, negative id not allowed")
    private final long id;
    private final long performerId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDate day;
    private final String stage;
    @DateTimeFormat(style = "hh:mm")
    private final LocalTime beginTime;
    @DateTimeFormat(style = "hh:mm")
    private final LocalTime endTime;

    public UpdateConcertDTO(long id, long performerId, LocalDate day, String stage, LocalTime beginTime, LocalTime endTime) {
        this.id = id;
        this.performerId = performerId;
        this.day = day;
        this.stage = stage;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public long getId() {
        return id;
    }

    public long getPerformerId() {
        return performerId;
    }

    public LocalDate getDay() {
        return day;
    }

    public String getStage() {
        return stage;
    }

    public LocalTime getBeginTime() {
        return beginTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
