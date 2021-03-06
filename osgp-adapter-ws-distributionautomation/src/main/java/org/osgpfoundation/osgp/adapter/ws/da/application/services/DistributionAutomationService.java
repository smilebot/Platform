/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgpfoundation.osgp.adapter.ws.da.application.services;

import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import org.osgpfoundation.osgp.adapter.ws.da.application.exceptionhandling.ResponseNotFoundException;
import org.osgpfoundation.osgp.adapter.ws.da.domain.entities.RtuResponseData;
import org.osgpfoundation.osgp.adapter.ws.da.infra.jms.DistributionAutomationRequestMessage;
import org.osgpfoundation.osgp.adapter.ws.da.infra.jms.DistributionAutomationRequestMessageSender;
import org.osgpfoundation.osgp.adapter.ws.da.infra.jms.DistributionAutomationRequestMessageType;
import org.osgpfoundation.osgp.domain.da.entities.RtuDevice;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetDeviceModelRequest;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetDeviceModelResponse;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetHealthStatusRequest;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetHealthStatusResponse;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetPQValuesPeriodicRequest;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetPQValuesRequest;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetPQValuesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Service
@Transactional(value = "wsTransactionManager")
@Validated
public class DistributionAutomationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistributionAutomationService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private DistributionAutomationRequestMessageSender requestMessageSender;

    @Autowired
    private RtuResponseDataService responseDataService;

    public DistributionAutomationService() {
        // Parameterless constructor required for transactions
    }

    public String enqueueGetPQValuesRequest(@Identification final String organisationIdentification,
                                            @Identification final String deviceIdentification, @NotNull final GetPQValuesRequest getPQValuesRequest) throws OsgpException {

        LOGGER.debug("enqueueGetPQValuesRequest called with organisation {} and device {}", organisationIdentification, deviceIdentification);

        return processRequest(organisationIdentification, deviceIdentification, getPQValuesRequest, DeviceFunction.GET_POWER_QUALITY_VALUES,
                DistributionAutomationRequestMessageType.GET_POWER_QUALITY_VALUES);
    }

    public GetPQValuesResponse dequeueGetPQValuesResponse(final String correlationUid) throws OsgpException {

        LOGGER.debug("dequeueGetPQValuesResponse called with correlation uid {}", correlationUid);

        return (GetPQValuesResponse) processResponse(correlationUid);
    }

    public String enqueueGetPQValuesPeriodicRequest(final String organisationIdentification, final String deviceIdentification,
                                                    final GetPQValuesPeriodicRequest getPQValuesPeriodicRequest) throws OsgpException {

        LOGGER.debug("enqueueGetPQValuesPeriodicRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        return processRequest(organisationIdentification, deviceIdentification, getPQValuesPeriodicRequest, DeviceFunction.GET_POWER_QUALITY_VALUES,
                DistributionAutomationRequestMessageType.GET_POWER_QUALITY_VALUES_PERIODIC);
    }

    public GetPQValuesResponse dequeueGetPQValuesPeriodicResponse(final String correlationUid) throws OsgpException {

        LOGGER.debug("dequeueGetPQValuesPeriodicResponse called with correlation uid {}", correlationUid);
        return (GetPQValuesResponse) processResponse(correlationUid);
    }

    public String enqueueGetDeviceModelRequest(final String organisationIdentification, final String deviceIdentification,
                                               final GetDeviceModelRequest getDeviceModelRequest) throws OsgpException {

        LOGGER.debug("enqueueGetDeviceModelRequest called with organisation {} and device {}", organisationIdentification, deviceIdentification);
        return processRequest(organisationIdentification, deviceIdentification, getDeviceModelRequest, DeviceFunction.GET_DEVICE_MODEL,
                DistributionAutomationRequestMessageType.GET_DEVICE_MODEL);
    }

    public GetDeviceModelResponse dequeueGetDeviceModelResponse(final String correlationUid) throws OsgpException {

        LOGGER.debug("dequeueGetDeviceModelResponse called with correlation uid {}", correlationUid);
        return (GetDeviceModelResponse) processResponse(correlationUid);
    }

    public String enqueueGetHealthStatusRequest(final String organisationIdentification, final String deviceIdentification,
                                                final GetHealthStatusRequest getHealthStatusRequest) throws OsgpException {

        LOGGER.debug("enqueueGetHealthStatusRequest called with organisation {} and device {}", organisationIdentification, deviceIdentification);
        return processRequest(organisationIdentification, deviceIdentification, getHealthStatusRequest, DeviceFunction.GET_HEALTH_STATUS,
                DistributionAutomationRequestMessageType.GET_HEALTH_STATUS);
    }

    public GetHealthStatusResponse dequeueGetHealthResponse(final String correlationUid) throws OsgpException {

        LOGGER.debug("dequeueGetHealthResponse called with correlation uid {}", correlationUid);
        return (GetHealthStatusResponse) processResponse(correlationUid);
    }

    private String processRequest(final String organisationIdentification, final String deviceIdentification, final Serializable request,
                                  final DeviceFunction deviceFunction, final DistributionAutomationRequestMessageType messageType) throws OsgpException {
        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification, deviceIdentification);

        final RtuDevice device = this.domainHelperService.findDevice(deviceIdentification);
        this.domainHelperService.isAllowed(organisation, device, deviceFunction);

        final DistributionAutomationRequestMessage message = new DistributionAutomationRequestMessage(messageType, correlationUid,
                organisationIdentification, deviceIdentification, request);

        try {
            this.requestMessageSender.send(message);
        } catch (final ArgumentNullOrEmptyException e) {
            throw new TechnicalException(ComponentType.WS_DISTRIBUTION_AUTOMATION, e);
        }
        return correlationUid;
    }

    private Serializable processResponse(final String correlationUid) throws OsgpException {
        final RtuResponseData responseData = this.responseDataService.dequeue(correlationUid, ResponseMessage.class);
        final ResponseMessage response = (ResponseMessage) responseData.getMessageData();

        switch (response.getResult()) {
            case OK:
                if (response.getDataObject() != null) {
                    return response.getDataObject();
                }
                // Should not get here
                throw new TechnicalException(ComponentType.WS_DISTRIBUTION_AUTOMATION, "Response message contains no data.");
            case NOT_FOUND:
                throw new ResponseNotFoundException(ComponentType.WS_DISTRIBUTION_AUTOMATION, "Response message not found.");
            case NOT_OK:
                if (response.getOsgpException() != null) {
                    throw response.getOsgpException();
                }
                throw new TechnicalException(ComponentType.WS_DISTRIBUTION_AUTOMATION, "Response message not ok.");
            default:
                // Should not get here
                throw new TechnicalException(ComponentType.WS_DISTRIBUTION_AUTOMATION, "Response message contains invalid result.");
        }
    }
}
