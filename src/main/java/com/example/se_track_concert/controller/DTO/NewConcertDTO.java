package com.example.se_track_concert.controller.DTO;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalTime;

public final class NewConcertDTO {

    @Positive(message = "Performer of concert mandatory")
    private final long performerId;
    @NotNull(message = "Date of concert mandatory")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDate day;
    @NotEmpty(message = "Stage of concert mandatory")
    private final String stage;
    @NotNull(message = "Begin time of concert mandatory")
    @DateTimeFormat(style = "hh:mm")
    private final LocalTime beginTime;
    @NotNull(message = "End time of concert mandatory")
    @DateTimeFormat(style = "hh:mm")
    private final LocalTime endTime;

    public NewConcertDTO(long performerId, LocalDate day, String stage, LocalTime beginTime, LocalTime endTime) {
        this.performerId = performerId;
        this.day = day;
        this.stage = stage;
        this.beginTime = beginTime;
        this.endTime = endTime;
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
