package com.alliander.osgp.adapter.domain.microgrids.application.tasks;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.microgrids.application.services.CommunicatonRecoveryService;
import com.alliander.osgp.domain.microgrids.entities.RtuDevice;
import com.alliander.osgp.domain.microgrids.entities.Task;
import com.alliander.osgp.domain.microgrids.repositories.RtuDeviceRepository;
import com.alliander.osgp.domain.microgrids.repositories.TaskRepository;
import com.alliander.osgp.domain.microgrids.valueobjects.TaskStatusType;

@Component
public class CommunicationMonitoringTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationMonitoringTask.class);

    private static final String TASK_IDENTIFICATION = "MicrogridsCommunicationMonitoring";

    @Autowired
    private CommunicatonRecoveryService communicationRecoveryService;

    @Autowired
    private RtuDeviceRepository rtuDeviceRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private Integer maxAllowedTimeWithoutCommunication;

    @Override
    public void run() {
        LOGGER.info("Running communication monitoring task.");

        Task task = this.loadTask();

        if (this.taskAlreadyRunning(task)) {
            LOGGER.info("Communication Monitoring Task already running. Skipping this run.");
            return;
        }

        if (this.taskAlreadyRan(task)) {
            LOGGER.info(
                    "Communication Monitoring Task already ran within maximum allowed interval. Skipping this run.");
            return;
        }

        task = this.startTask(task);

        final List<RtuDevice> rtuDevices = this.loadDevices(task);

        for (final RtuDevice rtu : rtuDevices) {
            LOGGER.debug("Restoring communication for device {}", rtu.getDeviceIdentification());

            this.communicationRecoveryService.restoreCommunication(rtu);
        }

        this.finishTask(task);
    }

    private Task loadTask() {
        LOGGER.debug("Loading task from repository.");
        Task task = this.taskRepository.findByTaskIdentification(TASK_IDENTIFICATION);
        if (task == null) {
            LOGGER.info("No existing task found, creating new task");
            task = this.createNewTask();
        }
        return task;
    }

    private Task createNewTask() {
        LOGGER.debug("Creating new task.");
        Task task = new Task(TASK_IDENTIFICATION);
        task = this.taskRepository.save(task);
        return task;
    }

    private boolean taskAlreadyRunning(final Task task) {
        LOGGER.debug("Checking if task is already running.");
        return TaskStatusType.RUNNING.equals(task.getTaskStatus());
    }

    private boolean taskAlreadyRan(final Task task) {
        LOGGER.debug("Checking if task has already ran within maximum allowed time.");

        if (task.getStartTime() == null) {
            return false;
        }

        final DateTime now = DateTime.now();
        final DateTime taskStartTime = new DateTime(task.getStartTime());
        return (now.plusSeconds(10)).isBefore(taskStartTime.plusMinutes(this.maxAllowedTimeWithoutCommunication));
    }

    private Task startTask(final Task task) {
        LOGGER.debug("Starting task.");
        task.start();
        return this.taskRepository.save(task);
    }

    private Task finishTask(final Task task) {
        LOGGER.debug("Finishing task.");
        task.finish();
        return this.taskRepository.save(task);
    }

    private List<RtuDevice> loadDevices(final Task task) {
        LOGGER.debug("Loading devices from repository for which communication should be restored.");
        final DateTime lastCommunicationTime = new DateTime(task.getStartTime())
                .minusMinutes(this.maxAllowedTimeWithoutCommunication);
        return this.rtuDeviceRepository.findByIsActiveAndLastCommunicationTimeBefore(true,
                lastCommunicationTime.toDate());
    }

}
