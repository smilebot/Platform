/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.shared.domain.entities.AbstractEntity;

/**
 * Entity class which is the base for all smart devices. Other smart device
 * entities should inherit from this class. See {@link Ssld} /
 * {@link SmartMeter} as examples.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Device extends AbstractEntity {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -4119222373415540822L;

    /**
     * Device identification of a device. This is the main value used to find a
     * device.
     */
    @Identification
    @Column(unique = true, nullable = false, length = 40)
    protected String deviceIdentification;

    /**
     * Alias of a device. Can be any String assigned to this device and can be
     * used as alternate identification.
     */
    @Column
    protected String alias;

    /**
     * Location information of a device. City.
     */
    @Column(length = 255)
    protected String containerCity;

    /**
     * Location information of a device. Street name.
     */
    @Column(length = 255)
    protected String containerStreet;

    /**
     * Location information of a device. Postal Code.
     */
    @Column(length = 10)
    protected String containerPostalCode;

    /**
     * Location information of a device. Street number.
     */
    @Column(length = 255)
    protected String containerNumber;

    /**
     * Location information of a device. Municipality / City.
     */
    @Column(length = 255)
    protected String containerMunicipality;

    /**
     * Location information of a device. Latitude.
     */
    @Column
    protected Float gpsLatitude;

    /**
     * Location information of a device. Longitude.
     */
    @Column
    protected Float gpsLongitude;

    /**
     * Indicates the type of the device. Example { @see Ssld.SSLD_TYPE }
     */
    protected String deviceType;

    /**
     * IP address of a device.
     */
    @Column(length = 50)
    @Type(type = "com.alliander.osgp.shared.hibernate.InetAddressUserType")
    protected InetAddress networkAddress;

    /**
     * Indicates if a device has been activated for the first time. This value
     * is never updated after the first time a device becomes active.
     */
    protected boolean isActivated;

    public void setActivated(final boolean isActivated) {
        this.isActivated = isActivated;
    }

    /**
     * List of { @see DeviceAuthorization.class } containing authorizations for
     * this device. More that one organisation can be authorized to use one ore
     * more { @see DeviceFunctionGroup.class }.
     */
    @OneToMany(mappedBy = "device", targetEntity = DeviceAuthorization.class, fetch = FetchType.EAGER)
    protected final List<DeviceAuthorization> authorizations = new ArrayList<DeviceAuthorization>();

    /**
     * Protocol information indicates which protocol this device is using.
     */
    @ManyToOne()
    @JoinColumn(name = "protocol_info_id")
    protected ProtocolInfo protocolInfo;

    /**
     * Indicates if a device is in maintenance status.
     */
    @Column
    protected boolean inMaintenance;

    /**
     * Gateway device through which communication with this device is handled.
     */
    @ManyToOne()
    @JoinColumn(name = "gateway_device_id")
    protected Device gatewayDevice;

    /**
     * List of organisations which are authorized to use this device.
     */
    @Transient
    protected final List<String> organisations = new ArrayList<String>();

    /**
     * Firmware information indicates which firmware this device is using.
     */
    @ManyToOne()
    @JoinColumn(name = "firmware")
    protected Firmware firmware;

    /**
     * Firmware history information
     */
    @ManyToMany()
    @JoinTable(name = "firmware_history", joinColumns = { @JoinColumn(name = "device") }, inverseJoinColumns = {
            @JoinColumn(name = "firmware") })
    protected List<Firmware> firmwareHistory;

    public Device() {
        // Default constructor
    }

    public Device(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public Device(final String deviceIdentification, final String alias, final String containerCity,
            final String containerPostalCode, final String containerStreet, final String containerNumber,
            final String containerMunicipality, final Float gpsLatitude, final Float gpsLongitude) {
        this.deviceIdentification = deviceIdentification;
        this.alias = alias;
        this.containerCity = containerCity;
        this.containerPostalCode = containerPostalCode;
        this.containerStreet = containerStreet;
        this.containerNumber = containerNumber;
        this.containerMunicipality = containerMunicipality;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;
    }

    public DeviceAuthorization addAuthorization(final Organisation organisation,
            final DeviceFunctionGroup functionGroup) {
        final DeviceAuthorization authorization = new DeviceAuthorization(this, organisation, functionGroup);
        this.authorizations.add(authorization);
        return authorization;
    }

    public void addOrganisation(final String organisationIdentification) {
        this.organisations.add(organisationIdentification);
    }

    public void clearNetworkAddress() {
        this.networkAddress = null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Device device = (Device) o;
        return Objects.equals(this.deviceIdentification, device.deviceIdentification);
    }

    public String getAlias() {
        return this.alias;
    }

    public List<DeviceAuthorization> getAuthorizations() {
        return this.authorizations;
    }

    public String getContainerCity() {
        return this.containerCity;
    }

    public String getContainerMunicipality() {
        return this.containerMunicipality;
    }

    public String getContainerNumber() {
        return this.containerNumber;
    }

    public String getContainerPostalCode() {
        return this.containerPostalCode;
    }

    public String getContainerStreet() {
        return this.containerStreet;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public Float getGpsLatitude() {
        return this.gpsLatitude;
    }

    public Float getGpsLongitude() {
        return this.gpsLongitude;
    }

    public String getIpAddress() {
        return this.networkAddress == null ? null : this.networkAddress.getHostAddress();
    }

    public InetAddress getNetworkAddress() {
        return this.networkAddress;
    }

    /**
     * Get the organisations that are authorized for this device.
     *
     * @return List of OrganisationIdentification of organisations that are
     *         authorized for this device.
     */
    @Transient
    public List<String> getOrganisations() {
        return this.organisations;
    }

    /**
     * Get the owner organisation of the device.
     *
     * @return The organisation when an owner was set, null otherwise.
     */
    public Organisation getOwner() {
        if (this.authorizations != null) {
            for (final DeviceAuthorization authorization : this.authorizations) {
                if (authorization.getFunctionGroup().equals(DeviceFunctionGroup.OWNER)) {
                    return authorization.getOrganisation();
                }
            }
        }

        return null;
    }

    public ProtocolInfo getProtocolInfo() {
        return this.protocolInfo;
    }

    public Device getGatewayDevice() {
        return this.gatewayDevice;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.deviceIdentification);
    }

    public boolean isActivated() {
        return this.isActivated;
    }

    public boolean isInMaintenance() {
        return this.inMaintenance;
    }

    /**
     * This setter is only needed for testing. Don't use this in production
     * code.
     *
     * @param id
     *            The id.
     */
    public void setId(final Long id) {
        this.id = id;
    }

    public void updateInMaintenance(final boolean inMaintenance) {
        this.inMaintenance = inMaintenance;
    }

    public void updateMetaData(final String alias, final String containerCity, final String containerPostalCode,
            final String containerStreet, final String containerNumber, final String containerMunicipality,
            final Float gpsLatitude, final Float gpsLongitude) {
        this.alias = alias;
        this.containerCity = containerCity;
        this.containerPostalCode = containerPostalCode;
        this.containerStreet = containerStreet;
        this.containerNumber = containerNumber;
        this.containerMunicipality = containerMunicipality;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;
    }

    public void updateProtocol(final ProtocolInfo protocolInfo) {
        this.protocolInfo = protocolInfo;
    }

    public void updateRegistrationData(final InetAddress networkAddress, final String deviceType) {
        this.networkAddress = networkAddress;
        this.deviceType = deviceType;
        this.isActivated = true;
    }

    public void updateGatewayDevice(final Device gatewayDevice) {
        this.gatewayDevice = gatewayDevice;
    }

    public Firmware getFirmware() {
        return this.firmware;
    }

    public void setFirmware(final Firmware firmware) {
        this.firmware = firmware;
    }

    public List<Firmware> getFirmwareHistory() {
        return this.firmwareHistory;
    }

    public void addFirmwareHistory(final Firmware firmware) {
        this.firmwareHistory.add(firmware);
    }
}
