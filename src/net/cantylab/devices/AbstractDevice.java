package net.cantylab.devices;

public class AbstractDevice implements Device{
	
	protected String uuid;
	protected String friendly_name;
	protected int server_port;
	protected boolean state = false;

	@Override
	public String getFriendlyName() {
		// TODO Auto-generated method stub
		return friendly_name;
	}
	@Override
	public String getUUID() {
		// TODO Auto-generated method stub
		return uuid;
	}
	@Override
	public int getServerPort() {
		// TODO Auto-generated method stub
		return server_port;
	}
	@Override
	public boolean getState() {
		// TODO Auto-generated method stub
		return state;
	}
	@Override
	public void turnOn() {
		state=true;
		
	}
	@Override
	public void turnOff() {
		state=false;
	}

	
}
