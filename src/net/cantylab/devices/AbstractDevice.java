package net.cantylab.devices;

import org.w3c.dom.Node;

public class AbstractDevice implements Device{
	
	protected boolean state = false;
	protected int port=80;
	protected Node n = null;
	
	public AbstractDevice(Node n, int port) {
		this.n = n;
		this.port = port;
	}
	
	private  AbstractDevice() {
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public String getFriendlyName() {
		// TODO Auto-generated method stub
		return n.getAttributes().getNamedItem("friendly-name").getNodeValue();
	}
	@Override
	public String getUUID() {
		// TODO Auto-generated method stub
		return n.getAttributes().getNamedItem("uuid").getNodeValue();
	}
	@Override
	public int getServerPort() {
		// TODO Auto-generated method stub
		return port;
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
