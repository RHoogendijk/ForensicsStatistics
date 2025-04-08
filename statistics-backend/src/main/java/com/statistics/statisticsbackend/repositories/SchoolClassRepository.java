package com.statistics.statisticsbackend.repositories;

import com.statistics.statisticsbackend.models.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, String> {

}
