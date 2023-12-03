package net.cantylab;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.cantylab.devices.Device;

public class EchoBridgeServer {
	public static void main(String args[])
	{
		String configFile =null;
		if (args.length==1)
		{
			configFile = args[0];
			System.out.println("INFO : starting server main thread with config file : " + configFile);			
		}else {
			System.err.println("ERROR : plase start server with \r\n");
			System.err.println("        java " + EchoBridgeServer.class + " <config_file_full_path>");
			return;
		}
		

		
		
		try
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(configFile));		

			XPath xPath = XPathFactory.newInstance().newXPath();
			String init_port_xpression= "/conf/server/@initial-port";
			int initial_port = ((Double) xPath.compile(init_port_xpression).evaluate(doc, XPathConstants.NUMBER)).intValue();

			String server_ip_address = "/conf/server/@listen_ipaddress";
			String ip = (String)xPath.compile(server_ip_address).evaluate(doc, XPathConstants.STRING);

			String device_xpression= "/conf/devices/device";
			NodeList devices = (NodeList) xPath.compile(device_xpression).evaluate(doc, XPathConstants.NODESET);
			int port = initial_port;
			
			ArrayList<Device> devices_obj= new ArrayList<>();
			// populate a list of devices to be started 
			for (int i=0; i<devices.getLength(); i++)
			{
				Node n=devices.item(i);
				String node_class = n.getAttributes().getNamedItem("class").getNodeValue();

				try {					   
					   Class c = Class.forName(node_class);
					   Device device = (Device)c.getConstructor(Node.class,String.class,int.class).newInstance(n,ip, port);
					   devices_obj.add(device);
				} catch (Exception nde) {
				   System.err.println("Error instantiating class '"+node_class+"' : " + nde.getMessage());
				   nde.printStackTrace();
				}
				
				// in case of error instantiating a device, skip intentionally the port. 
				port++;
			}
			
			// start the local HTTP webserver handler
			for (Device d : devices_obj) {
				System.out.println("INFO : starting thread for device " + d.getFriendlyName()+ " on port : " + d.getServerPort() );
				
				Thread s = new Thread(new DeviceServer(d));
				s.start();
				
				port++;
			}
			
			// start the UDP broadcast listener to respond to the echo discovery
			System.out.println("INFO : starting UDP broadcast discovery handler.");
			Thread udp_listener = new Thread(new BroadcastUdpServer(devices_obj));
			udp_listener.start();
			
			
			System.out.println("INFO : end startup." );

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
}
