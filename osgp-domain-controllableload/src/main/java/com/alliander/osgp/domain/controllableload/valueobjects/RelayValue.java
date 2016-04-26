package com.alliander.osgp.domain.controllableload.valueobjects;

import java.io.Serializable;

public class RelayValue implements Serializable {

    private static final long serialVersionUID = 4079712538795694610L;

    private Integer index;
    private boolean on;
    private Integer dimValue;

    public RelayValue(final Integer index, final boolean on, final Integer dimValue) {
        this.index = index;
        this.on = on;
        this.dimValue = dimValue;
    }

    public Integer getIndex() {
        return this.index;
    }

    public boolean isOn() {
        return this.on;
    }

    public Integer getDimValue() {
        return this.dimValue;
    }

    public void setIndex(final Integer index) {
        this.index = index;
    }

    public void setOn(final boolean on) {
        this.on = on;
    }

    public void setDimValue(final Integer dimValue) {
        this.dimValue = dimValue;
    }
}
