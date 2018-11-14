package se.kth.ict.id2203.ports.reconfigurable.cfg;

import se.sics.kompics.PortType;

public class ConfigPort extends PortType {
	{
		request(Config.class);
		indication(Configuration.class);
	}
}
