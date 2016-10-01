/**
 * Copyright 2014-2016 Smart Society Services B.V.
 */
package com.alliander.osgp.domain.core.valueobjects;

public class DeviceAuthorization {

    private String functionGroup;
    private String organisation;

    public DeviceAuthorization() {
    }

    public DeviceAuthorization(final String functionGroup, final String organisation) {
        this.functionGroup = functionGroup;
        this.organisation = organisation;
    }

    public String getFunctionGroup() {
        return this.functionGroup;
    }

    public String getOrganisation() {
        return this.organisation;
    }

    public void setFunctionGroup(final String functionGroup) {
        this.functionGroup = functionGroup;
    }

    public void setOrganisation(final String organisation) {
        this.organisation = organisation;
    }

}
