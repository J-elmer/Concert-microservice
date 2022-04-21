package com.example.se_track_concert;

import com.example.se_track_concert.controller.ConcertController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SeTrackConcertApplicationTests {

    @Test
    void contextLoads() {
        assertThat(ConcertController.class).isNotNull();
    }

}
