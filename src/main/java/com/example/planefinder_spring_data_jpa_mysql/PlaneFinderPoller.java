package com.example.planefinder_spring_data_jpa_mysql;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class PlaneFinderPoller {
    @NonNull
    private final AircraftRepository repository;
    private WebClient client =
            WebClient.create("http://localhost:7634/api/aircraft");

    @Transactional
    @Scheduled(fixedRate = 1000)
    public void pollPlanes() {
        try {
            repository.deleteAll();

            List<Aircraft> aircraftList = client.get()
                    .retrieve()
                    .bodyToFlux(Aircraft.class)
                    .filter(plane -> plane != null && !plane.getReg().isEmpty())
                    .collectList()
                    .block();

            if (aircraftList != null) {
                aircraftList.forEach(plane -> {
                    // Сбрасываем ID, чтобы создавались новые записи
                    plane.setId(null);
                    repository.save(plane);
                });
            }

            repository.findAll().forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Error in plane polling: " + e.getMessage());
        }
    }
}
