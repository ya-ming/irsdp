package se.kth.ict.id2203.ports.rowaonrr;

import se.sics.kompics.PortType;

public class ReadOneWriteAllOneNRegularRegister extends PortType {
	{
		indication(ReadResponse.class);
		indication(WriteResponse.class);
		request(ReadRequest.class);
		request(WriteRequest.class);
	}
}
