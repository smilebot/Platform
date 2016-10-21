package com.alliander.osgp.domain.microgrids.repositories;

import org.springframework.data.repository.CrudRepository;

import com.alliander.osgp.domain.microgrids.entities.Task;

public interface TaskRepository extends CrudRepository<Task, Long> {
    Task findByTaskIdentification(String taskIdentification);
}
