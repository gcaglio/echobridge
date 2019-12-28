package net.cantylab;

import java.io.*;
import java.net.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import net.cantylab.devices.Device;



/**
 * Implementa il server UDP per la discovery da parte dell'echo
 * e istruisce l'echo su come trovare il file di setup.
 * 
 * @author giuliano
 * @date 2019/12/18
 *
 */
public class BroadcastUdpServer implements Runnable{

	private int port = 1900;
	private ArrayList<Device> device_list = null;
		
	private BroadcastUdpServer() {
		//disabled
	}
	
	public BroadcastUdpServer(ArrayList<Device> devices) {
		this.device_list = devices;
	}
	public void run() {

		Hashtable<String, String> echo_discovery = new Hashtable<>();
		String discovery = "";
		
				
		try {
			 
			 InetAddress addr = InetAddress.getByName("239.255.255.250");
			 
			 MulticastSocket sock = new MulticastSocket(port);
//			 sock.setTimeToLive(ttl);
			 sock.joinGroup(addr);
			 sock.setLoopbackMode(true);
			 //sock.setSoTimeout(timeout);
			 
	         //DatagramSocket serverSocket = new DatagramSocket(bcast);
	            byte[] receiveData = new byte[1024];
	            byte[] sendData;
	            while(true){
	            	try {
	            	  System.out.println("AlexaUdpServer - socket opened");
	                  DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	            	  sock.receive(receivePacket);
	                  //serverSocket.receive(receivePacket);
	                  InetAddress echo_address = receivePacket.getAddress();
	                  int echo_port = receivePacket.getPort();
	                  String sentence = new String( receivePacket.getData());	                  
	                  
	                  System.out.println("AlexaUdpServer - incoming text from "+echo_address.toString()+":" + echo_port +" : \r\n" + sentence + "#EOT#\r\n");
	                  
	                  /* il messaggio di scan è 1 invio solo, più righe:
	                  M-SEARCH * HTTP/1.1
	                  Host: 239.255.255.250:1900
	                  Man: "ssdp:discover"
	                  MX: 3
	                  ST: ssdp:all
	                  */

	                  for (Device d:device_list) {
	                	  String message =getEchoResponse("192.168.123.110", d.getServerPort(), d.getUUID(), "setup.xml");
		                  sendData = message.getBytes();
		                  System.out.println("Sending back data : \r" + message + "#\r");
		                  DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, echo_address, echo_port);
		                  sock.send(sendPacket);
	                  }
	                  	                  
	                  //String capitalizedSentence = sentence.toUpperCase();
	                  //sendData = capitalizedSentence.getBytes();
	                  //DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
	                  //serverSocket.send(sendPacket);
	            	}catch(Exception ei)
	            	{
	            		System.out.println("Eccezione durante receivePacket alexa echo udp server." + ei.getMessage());
	            	}
	            }
	            
		} catch (IOException e) {
			System.out.println("Errore critico durante tentativo di apertura porta " + port + " ." + e.getMessage());
			e.printStackTrace();
		}

	}
	
	private static String getEchoResponse(String domoticWebServerIp, int domoticWebServerPort, String UUID, String file) {
		String message = "HTTP/1.1 200 OK\r\n";
		message+="CACHE-CONTROL: max-age=20\r\n";
		message+="DATE: Sun, 24 Apr 2016 21:48:39 GMT\r\n";
		message+="EXT:\r\n";
		message+="LOCATION: http://"+domoticWebServerIp+":"+domoticWebServerPort+"/"+file+"\r\n";
		message+="OPT: \"http://schemas.upnp.org/upnp/1/0/\"; ns=01\r\n";
		//message+="01-NLS: c66d1ad0-707e-495e-a21a-1d640eed4547\r\n";
		message+="01-NLS: b9200ebb-736d-4b93-bf03-835149d13983\r\n";
		message+="SERVER: Unspecified, UPnP/1.0, Unspecified\r\n";
		message+="ST: urn:Belkin:device:**\r\n";
		message+="USN: uuid:Socket-1_0-"+UUID+"::urn:Belkin:device:**";
		
		//System.out.println("MESSAGE=" + message);
		return message;
	}


}
