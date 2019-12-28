package net.cantylab;

import java.net.ServerSocket;
import java.net.Socket;

import net.cantylab.devices.Device;


public class DeviceServer implements Runnable{

	private int port=80;
	private String device_uuid=null;
	private String device_name=null;
	private Device d = null;
	private boolean run = false;
	
	public DeviceServer(Device d) {
		this.port = d.getServerPort();
		this.device_uuid = d.getUUID();
		this.device_name = d.getFriendlyName();
		this.d = d;
	}
	
    public void run(){
    	run = true;

    	try {
    		
			ServerSocket server = new ServerSocket(port,200);
			
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

    }
    
    public void terminate() {
    	run = false;
    }
}
