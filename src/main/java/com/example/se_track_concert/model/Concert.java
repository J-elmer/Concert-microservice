package com.example.se_track_concert.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    private Long id;
    private long performerId;
    private LocalDate day;
    private String stage;
    private LocalTime beginTime;
    private LocalTime endTime;

    public Concert() {

    }

    public Concert(long performerId, LocalDate day, String stage, LocalTime beginTime, LocalTime endTime) {
        this.performerId = performerId;
        this.day = day;
        this.stage = stage;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getPerformerId() {
        return performerId;
    }

    public void setPerformerId(long performerId) {
        this.performerId = performerId;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public LocalTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalTime beginTime) {
        this.beginTime = beginTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Concert{" +
                "id=" + id +
                ", performerId=" + performerId +
                ", day=" + day +
                ", stage='" + stage + '\'' +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(performerId, day, stage, beginTime, endTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Concert concert = (Concert) obj;
        return performerId == concert.getPerformerId() && day.equals(concert.getDay())
                && stage.equals(concert.getStage()) && beginTime.equals(concert.getBeginTime())
                && endTime.equals(concert.getEndTime());
    }
}
