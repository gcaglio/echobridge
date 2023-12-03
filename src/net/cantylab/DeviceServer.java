package net.cantylab;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import net.cantylab.devices.Device;


public class DeviceServer implements Runnable{

	private int port=80;
	private String device_uuid=null;
	private String device_name=null;
	private Device d = null;
	private boolean run = false;
	private String ip = null;
	
	public DeviceServer(Device d) {
		this.port = d.getServerPort();
		this.device_uuid = d.getUUID();
		this.device_name = d.getFriendlyName();
		this.d = d;
		this.ip = d.getServerIp();
	}
	
    public void run(){
    	run = true;
    	ServerSocket server = null;
    	try {
    		
    		server = new ServerSocket(port,200,InetAddress.getByName(ip));
			
	    	while(run)
			{
					// chiamata bloccante, in attesa di una nuova connessione
					Socket client = server.accept();
		
					// la nuova richiesta viene gestita da un thread indipendente, si ripete il ciclo
					
					Thread conn_handler = new Thread(new  Connect(client, d));
					conn_handler.start();
					
			}    		
	    	
		}catch(Exception e)
		{
			e.printStackTrace();
		}
    	if (server!=null) {
    		try { 
    			server.close();
    		}catch(Exception e)
    		{
    			/*NOP*/
    		}
    	}
    	
    }
    
    public void terminate() {
    	run = false;
    }
}
