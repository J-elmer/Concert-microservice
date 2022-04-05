package com.example.se_track_concert.repository;

import com.example.se_track_concert.model.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    List<Concert> findByStageContainsIgnoreCase(String stage);

    Concert findConcertById(Long id);

    List<Concert> findConcertByPerformerId(long performerId);

    List<Concert> findByDayAfter(LocalDate date);

    List<Concert> findByDayBefore(LocalDate date);
}
