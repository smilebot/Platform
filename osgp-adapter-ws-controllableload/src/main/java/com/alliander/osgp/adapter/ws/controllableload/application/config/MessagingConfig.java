/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.controllableload.application.config;

import javax.annotation.Resource;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

import com.alliander.osgp.adapter.ws.controllableload.infra.jms.ControllableLoadRequestMessageSender;
import com.alliander.osgp.adapter.ws.controllableload.infra.jms.ControllableLoadResponseMessageFinder;
import com.alliander.osgp.adapter.ws.infra.jms.LoggingMessageSender;

@Configuration
@PropertySource("file:${osp/osgpAdapterWsControllableLoad/config}")
public class MessagingConfig {

    @Resource
    private Environment environment;

    // JMS Settings
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.activemq.broker.url";

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";

    // JMS Settings: Controllable Load logging
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_QUEUE = "jms.controllableload.logging.queue";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_EXPLICIT_QOS_ENABLED = "jms.controllableload.logging.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_DELIVERY_PERSISTENT = "jms.controllableload.logging.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_TIME_TO_LIVE = "jms.controllableload.logging.time.to.live";

    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_INITIAL_REDELIVERY_DELAY = "jms.controllableload.logging.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_MAXIMUM_REDELIVERIES = "jms.controllableload.logging.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_MAXIMUM_REDELIVERY_DELAY = "jms.controllableload.logging.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_REDELIVERY_DELAY = "jms.controllableload.logging.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_BACK_OFF_MULTIPLIER = "jms.controllableload.logging.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_USE_EXPONENTIAL_BACK_OFF = "jms.controllableload.logging.use.exponential.back.off";

    // JMS Settings: Controllable Load requests
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_QUEUE = "jms.controllableload.requests.queue";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_EXPLICIT_QOS_ENABLED = "jms.controllableload.requests.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_TIME_TO_LIVE = "jms.controllableload.responses.time.to.live";

    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_DELIVERY_PERSISTENT = "jms.controllableload.requests.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_TIME_TO_LIVE = "jms.controllableload.requests.time.to.live";

    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.controllableload.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_MAXIMUM_REDELIVERIES = "jms.controllableload.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.controllableload.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_REDELIVERY_DELAY = "jms.controllableload.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_BACK_OFF_MULTIPLIER = "jms.controllableload.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.controllableload.requests.use.exponential.back.off";

    // JMS Settings: Controllable Load responses
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_QUEUE = "jms.controllableload.responses.queue";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_EXPLICIT_QOS_ENABLED = "jms.controllableload.responses.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_DELIVERY_PERSISTENT = "jms.controllableload.responses.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_RECEIVE_TIMEOUT = "jms.controllableload.responses.receive.timeout";

    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.controllableload.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_MAXIMUM_REDELIVERIES = "jms.controllableload.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.controllableload.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_REDELIVERY_DELAY = "jms.controllableload.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_BACK_OFF_MULTIPLIER = "jms.controllableload.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.controllableload.responses.use.exponential.back.off";

    // === JMS SETTINGS ===

    @Bean(destroyMethod = "stop")
    public PooledConnectionFactory pooledConnectionFactory() {
        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(this.connectionFactory());
        return pooledConnectionFactory;
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setRedeliveryPolicyMap(this.redeliveryPolicyMap());
        activeMQConnectionFactory
                .setBrokerURL(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL));

        activeMQConnectionFactory.setNonBlockingRedelivery(true);

        return activeMQConnectionFactory;
    }

    @Bean
    public RedeliveryPolicyMap redeliveryPolicyMap() {
        final RedeliveryPolicyMap redeliveryPolicyMap = new RedeliveryPolicyMap();
        redeliveryPolicyMap.setDefaultEntry(this.defaultRedeliveryPolicy());
        redeliveryPolicyMap.put(this.controllableLoadRequestsQueue(), this.controllableLoadRequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.controllableLoadResponsesQueue(), this.controllableLoadResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.controllableLoadLoggingQueue(), this.controllableLoadLoggingRedeliveryPolicy());
        return redeliveryPolicyMap;
    }

    @Bean
    public RedeliveryPolicy defaultRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(
                Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(
                Long.parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY)));

        return redeliveryPolicy;
    }

    // === JMS SETTINGS: CONTROLLABLE LOAD REQUESTS ===

    @Bean
    public ActiveMQDestination controllableLoadRequestsQueue() {
        return new ActiveMQQueue(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_QUEUE));
    }

    /**
     * @return
     */
    @Bean(name = "wsControllableLoadOutgoingRequestsJmsTemplate")
    public JmsTemplate controllableLoadRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.controllableLoadRequestsQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean
    public RedeliveryPolicy controllableLoadRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.controllableLoadRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));

        return redeliveryPolicy;
    }

    /**
     * @return
     */
    @Bean(name = "wsControllableLoadOutgoingRequestsMessageSender")
    public ControllableLoadRequestMessageSender controllableLoadRequestMessageSender() {
        return new ControllableLoadRequestMessageSender();
    }

    // === JMS SETTINGS: CONTROLLABLE LOAD RESPONSES ===

    /**
     * @return
     */
    @Bean(name = "wsControllableLoadIncomingResponsesJmsTemplate")
    public JmsTemplate controllableLoadResponsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.controllableLoadResponsesQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_RECEIVE_TIMEOUT)));
        return jmsTemplate;
    }

    @Bean
    public ActiveMQDestination controllableLoadResponsesQueue() {
        return new ActiveMQQueue(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_QUEUE));
    }

    @Bean
    public RedeliveryPolicy controllableLoadResponsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.controllableLoadRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    /**
     * @return
     */
    @Bean(name = "wsControllableLoadIncomingResponsesMessageFinder")
    public ControllableLoadResponseMessageFinder controllableLoadResponseMessageFinder() {
        return new ControllableLoadResponseMessageFinder();
    }

    // === JMS SETTINGS: CONTROLLABLE LOAD LOGGING ===

    @Bean
    public ActiveMQDestination controllableLoadLoggingQueue() {
        return new ActiveMQQueue(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_QUEUE));
    }

    /**
     * @return
     */
    @Bean
    public JmsTemplate loggingJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.controllableLoadLoggingQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    /**
     * @return
     */
    @Bean
    public RedeliveryPolicy controllableLoadLoggingRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.controllableLoadLoggingQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_CONTROLLABLE_LOAD_LOGGING_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public LoggingMessageSender loggingMessageSender() {
        return new LoggingMessageSender();
    }

}
mmm