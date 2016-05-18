/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

public class FindEventsQuery implements Serializable, ActionRequest {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 150978792120024431L;

    private final EventLogCategory eventLogCategory;
    private final DateTime from;
    private final DateTime until;

    public FindEventsQuery(final EventLogCategory eventLogCategory, final DateTime from, final DateTime until) {
        this.eventLogCategory = eventLogCategory;
        this.from = from;
        this.until = until;
    }

    public EventLogCategory getEventLogCategory() {
        return this.eventLogCategory;
    }

    public DateTime getFrom() {
        return this.from;
    }

    public DateTime getUntil() {
        return this.until;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.alliander.osgp.domain.core.valueobjects.smartmetering.ActionValueObject
     * #validate()
     */
    @Override
    public void validate() throws FunctionalException {

        if (!this.from.isBefore(this.until)) {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_SMART_METERING,
                    new Exception("The 'from' timestamp designates a time after 'until' timestamp."));
        }
    }

}