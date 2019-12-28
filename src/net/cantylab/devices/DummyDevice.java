package net.cantylab.devices;

public class DummyDevice extends AbstractDevice{

	public DummyDevice(String friendly_name, String uuid, int port)
	{
		this.friendly_name = friendly_name;
		this.uuid = uuid;
		this.server_port = port;
	}
}
