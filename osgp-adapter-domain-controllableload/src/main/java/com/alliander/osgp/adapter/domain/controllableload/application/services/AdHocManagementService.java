/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.controllableload.application.services;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.controllableload.valueobjects.DeviceStatus;
import com.alliander.osgp.domain.controllableload.valueobjects.RelayValue;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.dto.valueobjects.controllableload.DeviceStatusDto;
import com.alliander.osgp.dto.valueobjects.controllableload.RelayValueDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainControllableLoadAdHocManagementService")
@Transactional(value = "transactionManager")
public class AdHocManagementService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementService.class);

    /**
     * Constructor
     */
    public AdHocManagementService() {
        // Parameterless constructor required for transactions...
    }

    // === SWITCH DEVICE ===

    public void switchDevice(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final List<RelayValue> relayValues, final String messageType)
            throws FunctionalException {

        LOGGER.debug("setLight called for device {} with organisation {}", deviceIdentification,
                organisationIdentification);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        final List<RelayValueDto> relayValuesDto = this.domainControllableLoadMapper.mapAsList(relayValues,
                RelayValueDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, (Serializable) relayValuesDto), messageType, device.getIpAddress());
    }

    // === GET STATUS ===

    /**
     * Retrieve status of device
     *
     * @param organisationIdentification
     *            identification of organisation
     * @param deviceIdentification
     *            identification of device
     * @param allowedDomainType
     *            domain type performing requesting the status
     *
     * @return status of device
     *
     * @throws FunctionalException
     */
    public void getStatus(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String messageType) throws FunctionalException {

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, null), messageType,
                device.getIpAddress());
    }

    public void handleGetStatusResponse(final DeviceStatusDto deviceStatusDto, final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handleResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = exception;
        DeviceStatus deviceStatus = null;
        try {
            if (deviceResult == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }

            deviceStatus = this.domainControllableLoadMapper.map(deviceStatusDto, DeviceStatus.class);

        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, osgpException, deviceStatus));
    }
}
