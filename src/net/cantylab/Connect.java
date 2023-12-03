package net.cantylab;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import net.cantylab.devices.Device;

class Connect implements Runnable
{
	// dichiarazione delle variabili socket e dei buffer
	Socket client;
	BufferedReader in;
	PrintWriter out;
	
	private String device_name;
	private String device_uuid;
	private Device d = null;
	
	public Connect(Socket client, Device d)
	{
		this.client = client;
		this.device_name = d.getFriendlyName();
		this.device_uuid = d.getUUID();
		this.d = d;

	}

	public void run()
	{
		try
		{
			// inizializza i buffer in entrata e uscita
			//InputStreamReader isr = new InputStreamReader(client.getInputStream());
			BufferedInputStream bis = new BufferedInputStream(client.getInputStream());
			//in = new BufferedReader(isr);
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);


//			System.out.println("Sto servendo il client che ha indirizzo "+client.getInetAddress());

			// eventuali elaborazioni e scambi di informazioni con il client

			// ...
			String row=null;
			String body="";
			boolean is_soap=false;
			int c = 0;
			//while (  (c = isr.read()) != -1) {
			byte[] buf = new byte[10];
			Date da = new Date();
			System.out.println("<"+device_name+"> : HTTP request started on " + da.getTime() );
			
			StringBuffer b = new StringBuffer();
			while (bis.read(buf) != -1) {
				//Date da_i= new Date();
				//System.out.println("<"+device_name+"> loop : " + da.getTime() );
				
				b.append( new String (buf));

				if (b.indexOf("#GetBinaryState")>0)
					break;

				if (b.indexOf("</s:En")>0)
					break;
			
				if (b.indexOf("setup.xml")>0)
					break;				
			}
			
			body = b.toString();
			Date da2= new Date();
			System.out.println("<"+device_name+"> : HTTP request prework finished on " + da2.getTime() + " time spent (sec) : " + (da2.getTime()-da.getTime())/1000 );
			
			
			if (body.startsWith("GET ")) {
				if (body.indexOf("/setup.xml")>0) {
					String setup = getSetup();
					String headers = getHttpResponseHeaders(setup.getBytes().length);
					out.print(headers);
					out.print(setup);
					out.flush();
					
					//System.out.print(headers);
					//System.out.print(setup);
				}
			}else if (body.startsWith("POST ")) {
				if (body.indexOf("/upnp/control/basicevent1")>0) {
					// request to get or set device state
					
					
					if (body.indexOf("GetBinaryState")>0) {
						String response = getBinaryState();
						String headers = getHttpResponseHeaders(response.getBytes().length);
						out.print(headers);
						out.print(response);
						out.flush();

						//System.out.print(headers);
						//System.out.print(response);

					}
					if (body.indexOf("SetBinaryState")>0) {
						if (body.indexOf("<BinaryState>1</BinaryState>")>0)
						{
							// turn on virtual switch
							d.turnOn();
							System.out.println("<"+device_name+"> : turning on." );
							String response = getBinaryState();
							String headers = getHttpResponseHeadersSet(response.getBytes().length);
							out.print(headers);
							out.print(response);
							out.flush();
							
							//System.out.print(headers);
							//System.out.print(response);

						}
						else if  (body.indexOf("<BinaryState>0</BinaryState>")>0)
						{
							// turn off virtual switch
							d.turnOff();
							System.out.println("<"+device_name+"> : turning off." );
							String response = getBinaryState();
							String headers = getHttpResponseHeadersSet(response.getBytes().length);
							out.print(headers);
							out.print(response);
							out.flush();
							
							//System.out.print(headers);
							//System.out.print(response);
							
						}
					}
				}
			}

			// chiusura dei buffer e del socket
			bis.close();
			out.flush();
 			out.close();
 			client.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private String getSetup() {
		String xml="";
		xml += "<?xml version=\"1.0\"?>";
		xml += "<root xmlns=\"urn:Belkin:device-1-0\">";
		xml += "<device>\r\n";
		xml += "  <deviceType>urn:Belkin:device:controllee:1</deviceType>\r\n";
	    xml += "  <friendlyName>"+device_name+"</friendlyName>\r\n";
	    xml += "  <manufacturer>Belkin International Inc.</manufacturer>\r\n";
	    xml += "  <modelName>Emulated Socket</modelName>\r\n";
	    xml += "  <modelNumber>3.1415</modelNumber>\r\n";
	    xml += "  <UDN>uuid:Socket-1_0-"+device_uuid+"</UDN>\r\n";
	    
	    if (d.getState())
	    	xml += "  <binaryState>1</binaryState>\r\n";
	    else
	    	xml += "  <binaryState>0</binaryState>\r\n";
	    
	    xml += "  <serviceList>\r\n";
	    xml += "    <service>\r\n";
	    xml += "      <serviceType>urn:Belkin:service:basicevent:1</serviceType>\r\n";
	    xml += "      <serviceId>urn:Belkin:serviceId:basicevent1</serviceId>\r\n";
	    xml += "      <controlURL>/upnp/control/basicevent1</controlURL>\r\n";
		xml += "      <eventSubURL>/upnp/event/basicevent1</eventSubURL>\r\n";
        xml += "	  <SCPDURL>/eventservice.xml</SCPDURL>\r\n";
		xml += "    </service>\r\n";
		xml += "  </serviceList>\r\n";
		xml += "</device>\r\n";
		xml += "</root>\r\n";
		
		return xml + "\r\n\r\n";
	}

	
	private String getHttpResponseHeadersSet(int length) {
		String headers="";
		headers += "HTTP/1.1 200 OK\r\n";
		headers += "Content-Type: text/plain; charset=utf-8\r\n";
		headers +="Content-Length: " + length + "\r\n";
		headers +="Connection: close\r\n";
		return headers+"\r\n";
	}
	
	private String getHttpResponseHeaders(int length) {
		String headers="";
		headers += "HTTP/1.1 200 OK\r\n";
		headers += "Content-Type: text/xml; charset=utf-8\r\n";
		headers +="Content-Length: " + length + "\r\n";
		headers +="Connection: close\r\n";
		return headers+"\r\n";
	}
	
	private String getBinaryState() {
		String response="<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body>\r\n";
		response += "<u:GetBinaryStateResponse xmlns:u=\"urn:Belkin:service:basicevent:1\">\r\n";
        response +="<BinaryState>";
        
        if (d.getState())
        {
        	response+="1";
        }else
        {
        	response+="0";
        }
        response += "</BinaryState>\r\n";
        response += "</u:GetBinaryStateResponse>\r\n";
        response += "</s:Body> </s:Envelope>\r\n";
		
		return response;
	}
	
}