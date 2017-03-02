/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.core.domain.model.domain.DomainResponseService;
import com.alliander.osgp.core.domain.model.protocol.ProtocolRequestService;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.infra.jms.ProtocolRequestMessage;

@Service
@Transactional
public class DeviceRequestMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRequestMessageService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private DomainResponseService domainResponseMessageSender;

    @Autowired
    private ProtocolRequestService protocolRequestService;

    @Autowired
    private SmartMeterRepository smartMeteringDeviceRepository;

    public void processMessage(final ProtocolRequestMessage message) throws FunctionalException {

        try {
            long duration;
            long startTime = System.nanoTime();

            final Device device = this.domainHelperService.findDevice(message.getDeviceIdentification());
            duration = (System.nanoTime() - startTime);  //divide by 1000000 to get milliseconds.
            LOGGER.info("Elkan: found device in {} ns, {} ms", duration, duration / 1000000);

            final ProtocolInfo protocolInfo;
            if (device.getGatewayDevice() == null) {
                protocolInfo = device.getProtocolInfo();
            } else {
                protocolInfo = device.getGatewayDevice().getProtocolInfo();
            }
            duration = (System.nanoTime() - duration);  //divide by 1000000 to get milliseconds.
            LOGGER.info("Elkan: found protocolinfo in {} ns, {} ms", duration, duration / 1000000);

            if (protocolInfo == null || !this.protocolRequestService.isSupported(protocolInfo)) {
                if (protocolInfo == null) {
                    LOGGER.error("Protocol unknown for device [{}]", device.getDeviceIdentification());
                } else {
                    LOGGER.error("Protocol [{}] with version [{}] unknown for device [{}], needs to be reloaded.",
                            protocolInfo.getProtocol(), protocolInfo.getProtocolVersion(),
                            device.getDeviceIdentification());
                }

                throw new FunctionalException(FunctionalExceptionType.PROTOCOL_UNKNOWN_FOR_DEVICE,
                        ComponentType.OSGP_CORE);
            }
            duration = (System.nanoTime() - duration);  //divide by 1000000 to get milliseconds.
            LOGGER.info("Elkan: Checked protocolinfo in {} ns, {} ms", duration, duration / 1000000);

            LOGGER.info("Device is using protocol [{}] with version [{}]", protocolInfo.getProtocol(),
                    protocolInfo.getProtocolVersion());

            final Organisation organisation = this.domainHelperService.findOrganisation(message
                    .getOrganisationIdentification());
            duration = (System.nanoTime() - duration);  //divide by 1000000 to get milliseconds.
            LOGGER.info("Elkan: found organisation in {} ns, {} ms", duration, duration / 1000000);

            this.domainHelperService.isAllowed(organisation, device,
                    Enum.valueOf(DeviceFunction.class, message.getMessageType()));
            duration = (System.nanoTime() - duration);  //divide by 1000000 to get milliseconds.
            LOGGER.info("Elkan: Verified protocolinfo in {} ns, {} ms", duration, duration / 1000000);

            this.protocolRequestService.send(message, protocolInfo);
            duration = (System.nanoTime() - duration);  //divide by 1000000 to get milliseconds.
            LOGGER.info("Elkan: Send message in {} ns, {} ms", duration, duration / 1000000);

        } catch (final FunctionalException e) {
            this.domainResponseMessageSender.send(message, e);
            throw e;
        }
    }
}
