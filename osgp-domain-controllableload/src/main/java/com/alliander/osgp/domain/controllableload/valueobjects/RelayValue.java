package com.alliander.osgp.domain.controllableload.valueobjects;

import java.io.Serializable;

public class RelayValue implements Serializable {

    private static final long serialVersionUID = 4079712538795694610L;
    
    private int index;
    private boolean on;
    private int dimValue;
    
    public RelayValue(int index, boolean on, int dimValue) {
        this.index = index;
        this.on = on;
        this.dimValue = dimValue;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public boolean isOn() {
        return this.on;
    }
    
    public int dimValue() {
        return this.dimValue;
    }
}
