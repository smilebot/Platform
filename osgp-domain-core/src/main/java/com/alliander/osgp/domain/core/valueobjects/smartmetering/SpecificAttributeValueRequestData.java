/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

public class SpecificAttributeValueRequestData implements Serializable, ActionRequest {

    private static final long serialVersionUID = 2732162650167261497L;

    private final int classId;
    private final int attribute;
    private final ObisCodeValues obisCode;

    public SpecificAttributeValueRequestData(int classId, int attribute, ObisCodeValues obisCode) {
        super();
        this.classId = classId;
        this.attribute = attribute;
        this.obisCode = obisCode;
    }

    public int getClassId() {
        return this.classId;
    }

    public int getAttribute() {
        return this.attribute;
    }

    public ObisCodeValues getObisCode() {
        return this.obisCode;
    }

    @Override
    public void validate() throws FunctionalException {
        // not needed here
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + this.attribute;
        result = (prime * result) + this.classId;
        result = (prime * result) + ((this.obisCode == null) ? 0 : this.obisCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final SpecificAttributeValueRequestData other = (SpecificAttributeValueRequestData) obj;
        if (this.attribute != other.attribute) {
            return false;
        }
        if (this.classId != other.classId) {
            return false;
        }
        if (this.obisCode == null) {
            if (other.obisCode != null) {
                return false;
            }
        } else if (!this.obisCode.equals(other.obisCode)) {
            return false;
        }
        return true;
    }

    @Override
    public DeviceFunction getDeviceFunction() {
        return DeviceFunction.GET_SPECIFIC_ATTRIBUTE_VALUE;
    }
}