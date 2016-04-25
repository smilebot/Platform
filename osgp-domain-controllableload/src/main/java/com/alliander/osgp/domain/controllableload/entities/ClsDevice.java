package com.alliander.osgp.domain.controllableload.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import com.alliander.osgp.domain.controllableload.valueobjects.RelayValue;
import com.alliander.osgp.domain.core.entities.Device;

@Entity
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue(value = "CLS")
public class ClsDevice extends Device {

    private static final long serialVersionUID = -2209277268238093202L;

    @Transient
    private List<RelayValue> relayValues = new ArrayList<>();

    public ClsDevice(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public List<RelayValue> getRelayValues() {
        return Collections.unmodifiableList(this.relayValues);
    }

    public void updateRelayValues(final List<RelayValue> relayValues) {
        this.relayValues = new ArrayList<RelayValue>(relayValues);
    }
}
