package com.alliander.osgp.domain.controllableload.clsdevice;

import org.springframework.data.jpa.domain.Specification;

import com.alliander.osgp.domain.controllableload.entities.ClsDevice;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;

public interface ClsDeviceSpecifications {

    Specification<ClsDevice> forOrganisation(final Organisation organisation) throws ArgumentNullOrEmptyException;
}
