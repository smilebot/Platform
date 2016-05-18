INSERT INTO protocol_info(creation_time
			 ,modification_time
			 ,version
			 ,protocol
			 ,protocol_version
			 ,outgoing_protocol_requests_queue
			 ,incoming_protocol_responses_queue
			 ,incoming_protocol_requests_queue
			 ,outgoing_protocol_responses_queue)
VALUES (current_date
			 ,current_date
			 ,0
			 ,'IEC61850-CLS'
			 ,'1.0'
			 ,'protocol-iec61850-cls.1_0.osgp-core.1_0.requests'
			 ,'osgp-core.1_0.protocol-iec61850-cls.1_0.responses'
			 ,'osgp-core.1_0.protocol-iec61850-cls.1_0.requests'
			 ,'protocol-iec61850-cls.1_0.osgp-core.1_0.responses');

