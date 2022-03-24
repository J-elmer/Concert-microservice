package com.example.se_track_concert.repository;

import com.example.se_track_concert.model.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    List<Concert> findByStageContainsIgnoreCase(String stage);

    Concert findConcertById(Long id);

}
