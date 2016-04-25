package com.alliander.osgp.adapter.ws.controllableload.infra.persistence;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import com.alliander.osgp.domain.controllableload.clsdevice.ClsDeviceSpecifications;
import com.alliander.osgp.domain.controllableload.entities.ClsDevice;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;

public class JpaClsDeviceSpecifications implements ClsDeviceSpecifications {

    @Override
    public Specification<ClsDevice> forOrganisation(final Organisation organisation)
            throws ArgumentNullOrEmptyException {

        if (organisation == null) {
            throw new ArgumentNullOrEmptyException("organisation");
        }

        return new Specification<ClsDevice>() {
            @Override
            public Predicate toPredicate(final Root<ClsDevice> clsDeviceRoot, final CriteriaQuery<?> query,
                    final CriteriaBuilder cb) {

                final Subquery<Long> subquery = query.subquery(Long.class);
                final Root<DeviceAuthorization> deviceAuthorizationRoot = subquery.from(DeviceAuthorization.class);
                subquery.select(deviceAuthorizationRoot.get("device").get("id").as(Long.class));
                subquery.where(cb.equal(deviceAuthorizationRoot.get("organisation"), organisation.getId()));

                return cb.in(clsDeviceRoot.get("id")).value(subquery);
            }
        };
    }

}
