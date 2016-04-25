package com.alliander.osgp.domain.controllableload.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeviceStatus implements Serializable {

    private static final long serialVersionUID = -2748510642984541286L;

    private List<RelayValue> relayValues;

    public DeviceStatus(final List<RelayValue> relayValues) {
        this.relayValues = new ArrayList<RelayValue>(relayValues);
    }

    public List<RelayValue> getRelayValues() {
        return Collections.unmodifiableList(this.relayValues);
    }

}
