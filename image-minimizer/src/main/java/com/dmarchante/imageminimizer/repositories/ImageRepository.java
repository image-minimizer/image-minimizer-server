package com.dmarchante.imageminimizer.repositories;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@EnableScan
public interface ImageRepository extends CrudRepository<Tasks, UUID> {
    Optional<Tasks> findById(UUID id);
    List<Tasks> findByAssignee(String assignee);
}
