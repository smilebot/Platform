/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.controllableload.presentation.ws;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.method.MethodConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.controllableload.application.mapping.AdHocManagementMapper;
import com.alliander.osgp.adapter.ws.controllableload.application.services.AdHocManagementService;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.controllableload.adhocmanagement.DevicePage;
import com.alliander.osgp.adapter.ws.schema.controllableload.adhocmanagement.FindAllDevicesRequest;
import com.alliander.osgp.adapter.ws.schema.controllableload.adhocmanagement.FindAllDevicesResponse;
import com.alliander.osgp.adapter.ws.schema.controllableload.adhocmanagement.GetStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.controllableload.adhocmanagement.GetStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.controllableload.adhocmanagement.GetStatusRequest;
import com.alliander.osgp.adapter.ws.schema.controllableload.adhocmanagement.GetStatusResponse;
import com.alliander.osgp.adapter.ws.schema.controllableload.adhocmanagement.SwitchDeviceAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.controllableload.adhocmanagement.SwitchDeviceAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.controllableload.adhocmanagement.SwitchDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.controllableload.adhocmanagement.SwitchDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.controllableload.common.AsyncResponse;
import com.alliander.osgp.adapter.ws.schema.controllableload.common.OsgpResultType;
import com.alliander.osgp.domain.controllableload.entities.ClsDevice;
import com.alliander.osgp.domain.controllableload.valueobjects.RelayValue;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.valueobjects.DeviceStatus;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

//MethodConstraintViolationException is deprecated.
//Will by replaced by equivalent functionality defined
//by the Bean Validation 1.1 API as of Hibernate Validator 5.
@SuppressWarnings("deprecation")
@Endpoint
public class ControllableLoadAdHocManagementEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllableLoadAdHocManagementEndpoint.class);
    private static final String NAMESPACE = "http://www.alliander.com/schemas/osgp/controllableload/adhocmanagement/2014/10";
    private static final ComponentType COMPONENT_WS_CONTROLLABLE_LOAD = ComponentType.WS_CONTROLLABLE_LOAD;

    private static final String EXCEPTION_OCCURRED = "Exception Occurred";

    private final AdHocManagementService adHocManagementService;
    private final AdHocManagementMapper adHocManagementMapper;

    @Autowired
    public ControllableLoadAdHocManagementEndpoint(
            @Qualifier("wsControllableLoadAdHocManagementService") final AdHocManagementService adHocManagementService,
            @Qualifier("controllableLoadAdhocManagementMapper") final AdHocManagementMapper adHocManagementMapper) {
        this.adHocManagementService = adHocManagementService;
        this.adHocManagementMapper = adHocManagementMapper;
    }

    @PayloadRoot(localPart = "FindAllDevicesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public FindAllDevicesResponse findAllDevices(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final FindAllDevicesRequest request) throws OsgpException {

        LOGGER.info("Finding All Devices Request received from organisation: {}.", organisationIdentification);

        final FindAllDevicesResponse response = new FindAllDevicesResponse();

        try {
            final Page<ClsDevice> page = this.adHocManagementService.findAllDevices(organisationIdentification,
                    request.getPage());

            final DevicePage devicePage = new DevicePage();
            devicePage.setTotalPages(page.getTotalPages());
            devicePage.getDevices().addAll(this.adHocManagementMapper.mapAsList(page.getContent(),
                    com.alliander.osgp.adapter.ws.schema.controllableload.adhocmanagement.Device.class));
            response.setDevicePage(devicePage);
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURRED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_WS_CONTROLLABLE_LOAD,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    // === SWITCH ===

    @PayloadRoot(localPart = "SwitchDeviceRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SwitchDeviceAsyncResponse switchDevice(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SwitchDeviceRequest request) throws OsgpException {

        LOGGER.info("Switch Device Request received from organisation: {} for device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        final SwitchDeviceAsyncResponse response = new SwitchDeviceAsyncResponse();

        try {
            final List<RelayValue> relayValues = new ArrayList<>();
            relayValues.addAll(this.adHocManagementMapper.mapAsList(request.getRelayValue(), RelayValue.class));

            final String correlationUid = this.adHocManagementService.enqueueSwitchDeviceRequest(
                    organisationIdentification, request.getDeviceIdentification(), relayValues);

            final AsyncResponse asyncResponse = new AsyncResponse();

            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());

            response.setAsyncResponse(asyncResponse);
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURRED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_WS_CONTROLLABLE_LOAD,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "SwitchDeviceAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SwitchDeviceResponse getSwitchDeviceResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SwitchDeviceAsyncRequest request) throws OsgpException {

        LOGGER.info("Get Set Light Response received from organisation: {} with correlationUid: {}.",
                organisationIdentification, request.getAsyncRequest().getCorrelationUid());

        final SwitchDeviceResponse response = new SwitchDeviceResponse();

        try {
            final ResponseMessage message = this.adHocManagementService
                    .dequeueSwitchDeviceResponse(request.getAsyncRequest().getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    // === GET STATUS ===

    @PayloadRoot(localPart = "GetStatusRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetStatusAsyncResponse getStatus(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetStatusRequest request) throws OsgpException {

        LOGGER.info("Get Status received from organisation: {} for device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        final GetStatusAsyncResponse response = new GetStatusAsyncResponse();

        try {
            final String correlationUid = this.adHocManagementService
                    .enqueueGetStatusRequest(organisationIdentification, request.getDeviceIdentification());

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "GetStatusAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetStatusResponse getGetStatusResponse(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetStatusAsyncRequest request) throws OsgpException {

        LOGGER.info("Get Status Response received from organisation: {} for correlationUid: {}.",
                organisationIdentification, request.getAsyncRequest().getCorrelationUid());

        final GetStatusResponse response = new GetStatusResponse();

        try {
            final ResponseMessage message = this.adHocManagementService
                    .dequeueGetStatusResponse(request.getAsyncRequest().getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));

                if (message.getDataObject() != null) {
                    final DeviceStatus deviceStatus = (DeviceStatus) message.getDataObject();
                    if (deviceStatus != null) {
                        response.setDeviceStatus(this.adHocManagementMapper.map(deviceStatus,
                                com.alliander.osgp.adapter.ws.schema.controllableload.adhocmanagement.DeviceStatus.class));
                    }
                }
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    private void handleException(final Exception e) throws OsgpException {
        // Rethrow exception if it already is a functional or technical
        // exception,
        // otherwise throw new technical exception.
        LOGGER.error("Exception occurred: ", e);
        if (e instanceof OsgpException) {
            throw (OsgpException) e;
        } else {
            throw new TechnicalException(COMPONENT_WS_CONTROLLABLE_LOAD, e);
        }
    }
}