package com.alliander.osgp.domain.controllableload.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.controllableload.entities.ClsDevice;

@Repository
public interface ClsDeviceRepository extends JpaRepository<ClsDevice, Long>, JpaSpecificationExecutor<ClsDevice> {

    ClsDevice findByDeviceIdentification(String deviceIdentification);
}
