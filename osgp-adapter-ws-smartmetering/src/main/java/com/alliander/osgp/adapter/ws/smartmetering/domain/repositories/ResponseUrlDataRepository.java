/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.ResponseUrlData;

@Repository
public interface ResponseUrlDataRepository extends JpaRepository<ResponseUrlData, Long> {

    ResponseUrlData findSingleResultByCorrelationUid(String correlationUid);

}
