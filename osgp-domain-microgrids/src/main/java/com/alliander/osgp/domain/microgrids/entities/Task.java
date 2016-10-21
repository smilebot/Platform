package com.alliander.osgp.domain.microgrids.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.alliander.osgp.domain.microgrids.valueobjects.TaskStatusType;
import com.alliander.osgp.shared.domain.entities.AbstractEntity;

@Entity
public class Task extends AbstractEntity {

    /**
     *
     */
    private static final long serialVersionUID = -1231883978623839983L;

    @Column
    private String taskIdentification;

    @Column(name = "task_status")
    @Enumerated(EnumType.STRING)
    private TaskStatusType taskStatus;

    @Column
    private Date startTime;

    @Column
    private Date endTime;

    protected Task() {
        // default constructor for Hibernate
    }

    public Task(final String taskIdentification) {
        this.taskIdentification = taskIdentification;
        this.taskStatus = TaskStatusType.AVAILABLE;
    }

    public String getTaskIdentification() {
        return this.taskIdentification;
    }

    public TaskStatusType getTaskStatus() {
        return this.taskStatus;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void start() {
        this.startTime = new Date();
        this.taskStatus = TaskStatusType.RUNNING;
    }

    public void finish() {
        this.endTime = new Date();
        this.taskStatus = TaskStatusType.AVAILABLE;
    }
}
