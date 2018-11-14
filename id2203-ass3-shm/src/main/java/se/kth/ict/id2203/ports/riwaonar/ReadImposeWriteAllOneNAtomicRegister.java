package se.kth.ict.id2203.ports.riwaonar;

import se.kth.ict.id2203.ports.rowaonrr.ReadRequest;
import se.kth.ict.id2203.ports.rowaonrr.ReadResponse;
import se.kth.ict.id2203.ports.rowaonrr.WriteRequest;
import se.kth.ict.id2203.ports.rowaonrr.WriteResponse;
import se.sics.kompics.PortType;

public class ReadImposeWriteAllOneNAtomicRegister extends PortType {
	{
		indication(ReadResponse.class);
		indication(WriteResponse.class);
		request(ReadRequest.class);
		request(WriteRequest.class);
	}
}
