/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.microgrids.application.config;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.alliander.osgp.adapter.domain.microgrids.application.tasks.CommunicationMonitoringTask;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@EnableScheduling
@PropertySource("file:${osp/osgpAdapterDomainMicrogrids/config}")
public class CommunicationMonitoringConfig {

    private static final String PROPERTY_NAME_CRON_EXPRESSION = "communication.monitoring.task.cron.expression";
    private static final String PROPERTY_NAME_SCHEDULER_POOL_SIZE = "communication.monitoring.task.scheduler.pool.size";
    private static final String PROPERTY_NAME_SCHEDULER_THREAD_NAME_PREFIX = "communication.monitoring.task.scheduler.thread.name.prefix";
    private static final String PROPERTY_NAME_MAX_ALLOWED_TIME_WITHOUT_COMMUNICATION = "communication.monitoring.task.max.allowed.time.without.communication";

    private static final String DEFAULT_CRON_EXPRESSION = "0 */5 * * * *";
    private static final Integer DEFAULT_POOL_SIZE = 1;
    private static final String DEFAULT_THREAD_NAME_PREFIX = "microgrids-communication-monitoring-";
    private static final Integer DEFAULT_MAX_ALLOWED_TIME_WITHOUT_COMMUNICATION = 5;

    @Resource
    private Environment environment;

    @Autowired
    private CommunicationMonitoringTask communicationMonitoringTask;

    @Bean
    public CronTrigger communicationMonitoringTaskCronTrigger() {
        return new CronTrigger(this.cronExpression());
    }

    @Bean(destroyMethod = "shutdown")
    public TaskScheduler communicationMonitoringTaskScheduler() {
        final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(this.schedulerPoolSize());
        taskScheduler.setThreadNamePrefix(this.schedulerThreadNamePrefix());
        taskScheduler.setWaitForTasksToCompleteOnShutdown(false);
        taskScheduler.setAwaitTerminationSeconds(10);
        taskScheduler.initialize();
        taskScheduler.schedule(this.communicationMonitoringTask, this.communicationMonitoringTaskCronTrigger());
        return taskScheduler;
    }

    @Bean
    public Integer maxAllowedTimeWithoutCommunication() {
        Integer result;
        final String value = this.environment.getProperty(PROPERTY_NAME_MAX_ALLOWED_TIME_WITHOUT_COMMUNICATION);
        if (StringUtils.isNotBlank(value) && StringUtils.isNumeric(value)) {
            result = Integer.parseInt(value);
        } else {
            result = DEFAULT_MAX_ALLOWED_TIME_WITHOUT_COMMUNICATION;
        }
        return result;
    }

    private String cronExpression() {
        String value = this.environment.getProperty(PROPERTY_NAME_CRON_EXPRESSION);
        if (StringUtils.isBlank(value)) {
            value = DEFAULT_CRON_EXPRESSION;
        }
        return value;
    }

    private Integer schedulerPoolSize() {
        Integer result;
        final String value = this.environment.getProperty(PROPERTY_NAME_SCHEDULER_POOL_SIZE);
        if (StringUtils.isNotBlank(value) && StringUtils.isNumeric(value)) {
            result = Integer.parseInt(value);
        } else {
            result = DEFAULT_POOL_SIZE;
        }
        return result;
    }

    private String schedulerThreadNamePrefix() {
        String value = this.environment.getProperty(PROPERTY_NAME_SCHEDULER_THREAD_NAME_PREFIX);
        if (StringUtils.isBlank(value)) {
            value = DEFAULT_THREAD_NAME_PREFIX;
        }
        return value;
    }

}
