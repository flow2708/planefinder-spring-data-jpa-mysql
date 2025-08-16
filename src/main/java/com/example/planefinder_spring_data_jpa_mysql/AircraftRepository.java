package com.example.planefinder_spring_data_jpa_mysql;

import org.springframework.data.repository.CrudRepository;

public interface AircraftRepository extends CrudRepository<Aircraft, Long> {

}
