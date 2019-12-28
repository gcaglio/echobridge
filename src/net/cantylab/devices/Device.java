package net.cantylab.devices;

public interface Device {

	public String getFriendlyName();
	public String getUUID();
	public int getServerPort();
	public boolean getState();
	public void turnOn();
	public void turnOff();
	
}
