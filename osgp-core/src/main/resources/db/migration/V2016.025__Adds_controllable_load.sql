-- Creates the table for cls devices along with the proper permissions
CREATE TABLE cls_device (
    id bigserial NOT NULL
);

ALTER TABLE public.cls_device OWNER TO osp_admin;

ALTER TABLE ONLY cls_device ADD CONSTRAINT cls_device_pkey PRIMARY KEY (id);

GRANT ALL ON public.cls_device TO osp_admin;
GRANT SELECT ON public.cls_device TO osgp_read_only_ws_user;


-- Inserts the device function mapping for switch device function
insert into device_function_mapping  (function_group, function) values ('OWNER' ,'SWITCH_DEVICE');
insert into device_function_mapping  (function_group, function) values ('AD_HOC' ,'SWITCH_DEVICE');


-- Adds the incoming and outgoing queues for the Controllable Load queues
INSERT INTO domain_info(creation_time, modification_time, version, domain,
 domain_version, incoming_domain_requests_queue, outgoing_domain_responses_queue,
 outgoing_domain_requests_queue, incoming_domain_responses_queue)
VALUES (current_date, current_date, 0, 'CONTROLLABLE_LOAD', '1.0',
 'osgp-core.1_0.domain-controllableload.1_0.requests',
 'domain-controllableload.1_0.osgp-core.1_0.responses',
 'domain-controllableload.1_0.osgp-core.1_0.requests',
 'osgp-core.1_0.domain-controllableload.1_0.responses'
);

