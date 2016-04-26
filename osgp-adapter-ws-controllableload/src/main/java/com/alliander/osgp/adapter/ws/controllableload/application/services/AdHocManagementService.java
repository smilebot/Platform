/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.controllableload.application.services;

import static org.springframework.data.jpa.domain.Specifications.where;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.controllableload.infra.jms.ControllableLoadRequestMessage;
import com.alliander.osgp.adapter.ws.controllableload.infra.jms.ControllableLoadRequestMessageSender;
import com.alliander.osgp.adapter.ws.controllableload.infra.jms.ControllableLoadRequestMessageType;
import com.alliander.osgp.adapter.ws.controllableload.infra.jms.ControllableLoadResponseMessageFinder;
import com.alliander.osgp.domain.controllableload.clsdevice.ClsDeviceSpecifications;
import com.alliander.osgp.domain.controllableload.entities.ClsDevice;
import com.alliander.osgp.domain.controllableload.repositories.ClsDeviceRepository;
import com.alliander.osgp.domain.controllableload.valueobjects.RelayValue;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Service(value = "wsControllableLoadAdHocManagementService")
@Transactional(value = "transactionManager")
@Validated
public class AdHocManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementService.class);

    @Autowired
    private int pageSize;

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private ClsDeviceRepository clsDeviceRepository;

    @Autowired
    private ClsDeviceSpecifications clsDeviceSpecifications;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    @Qualifier("wsControllableLoadOutgoingRequestsMessageSender")
    private ControllableLoadRequestMessageSender controllableLoadRequestMessageSender;

    @Autowired
    @Qualifier("wsControllableLoadIncomingResponsesMessageFinder")
    private ControllableLoadResponseMessageFinder controllableLoadResponseMessageFinder;

    public AdHocManagementService() {
        // Parameterless constructor required for transactions
    }

    public Page<ClsDevice> findAllDevices(@Identification final String organisationIdentification, final int pageNumber)
            throws FunctionalException {
        LOGGER.debug("findAllDevices called with organisation {} and pageNumber {}", organisationIdentification,
                pageNumber);

        Page<ClsDevice> clsDevicesPage = null;

        try {

            final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

            final Specifications<ClsDevice> specifications = where(
                    this.clsDeviceSpecifications.forOrganisation(organisation));

            final PageRequest request = new PageRequest(pageNumber, this.pageSize, Sort.Direction.DESC,
                    "deviceIdentification");

            clsDevicesPage = this.clsDeviceRepository.findAll(specifications, request);
        } catch (final ArgumentNullOrEmptyException e) {
            LOGGER.error("Invalid organisation", e);
        }

        return clsDevicesPage;
    }

    public String enqueueSwitchDeviceRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            @Size(min = 1, max = 4) @Valid final List<RelayValue> relayValues) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

        final ClsDevice clsDevice = this.domainHelperService.findDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, clsDevice, DeviceFunction.SWITCH_DEVICE);

        LOGGER.debug("enqueueSwitchDeviceRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final ControllableLoadRequestMessage message = new ControllableLoadRequestMessage(
                ControllableLoadRequestMessageType.SWITCH_DEVICE, correlationUid, organisationIdentification,
                deviceIdentification, (Serializable) relayValues, null);

        this.controllableLoadRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueSwitchDeviceResponse(final String correlationUid) throws OsgpException {

        return this.controllableLoadResponseMessageFinder.findMessage(correlationUid);
    }

    public String enqueueGetStatusRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

        final ClsDevice clsDevice = this.domainHelperService.findDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, clsDevice, DeviceFunction.GET_STATUS);

        LOGGER.debug("enqueueGetStatusRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final ControllableLoadRequestMessage message = new ControllableLoadRequestMessage(
                ControllableLoadRequestMessageType.GET_STATUS, correlationUid, organisationIdentification,
                deviceIdentification, null, null);

        this.controllableLoadRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueGetStatusResponse(final String correlationUid) throws OsgpException {

        return this.controllableLoadResponseMessageFinder.findMessage(correlationUid);
    }
}