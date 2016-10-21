package com.alliander.osgp.adapter.domain.microgrids.infra.jms.core;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

public class OsgpCoreRequestMessageCreator implements MessageCreator {

    private RequestMessage requestMessage;
    private String messageType;
    private String ipAddress;
    private Long scheduleTime;

    public OsgpCoreRequestMessageCreator(final RequestMessage requestMessage, final String messageType,
            final String ipAddress, final Long scheduleTime) {
        this.requestMessage = requestMessage;
        this.messageType = messageType;
        this.ipAddress = ipAddress;
        this.scheduleTime = scheduleTime;
    }

    @Override
    public Message createMessage(final Session session) throws JMSException {
        final ObjectMessage objectMessage = session.createObjectMessage();

        objectMessage.setJMSType(this.messageType);
        objectMessage.setJMSCorrelationID(this.requestMessage.getCorrelationUid());
        objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                this.requestMessage.getOrganisationIdentification());
        objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION, this.requestMessage.getDeviceIdentification());
        objectMessage.setStringProperty(Constants.IP_ADDRESS, this.ipAddress);
        if (this.scheduleTime != null) {
            objectMessage.setLongProperty(Constants.SCHEDULE_TIME, this.scheduleTime);
        }
        objectMessage.setObject(this.requestMessage.getRequest());

        return objectMessage;
    }

}
