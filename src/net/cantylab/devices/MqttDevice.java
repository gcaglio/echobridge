package net.cantylab.devices;

import java.net.URL;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.cantylab.utility.UrlUtility;

public class MqttDevice extends AbstractDevice{

	
	public MqttDevice(Node n, String ip, int port) {
		super(n,ip, port);
	}
/*
	@Override
	public void turnOn() {
		turnOff();
		return;
	}*/

	@Override
	public void turnOn() {

		XPath xPath = XPathFactory.newInstance().newXPath();
		String device_xpression= "./turnon";
		NodeList devices = null;
		
		try {
			devices = (NodeList) xPath.compile(device_xpression).evaluate(n, XPathConstants.NODESET);
		
			if (devices==null || devices.getLength()==0)
			{
				System.err.println("ERROR : device '"+getFriendlyName()+"' : missing <turnon /> node.");
				return;
			}
			
			Node turnon_node = devices.item(0);
			String mqtt_server=turnon_node.getAttributes().getNamedItem("mqtt_server").getNodeValue();
			String mqtt_port=turnon_node.getAttributes().getNamedItem("mqtt_port").getNodeValue();
			String mqtt_topic=turnon_node.getAttributes().getNamedItem("topic").getNodeValue();
			String mqtt_message=turnon_node.getAttributes().getNamedItem("message").getNodeValue();
			
			String mqtt_username=turnon_node.getAttributes().getNamedItem("mqtt_username").getNodeValue();
			String mqtt_password=turnon_node.getAttributes().getNamedItem("mqtt_password").getNodeValue();
			
			MemoryPersistence persistence = new MemoryPersistence();
			int qos = 2;
			
			MqttClient sampleClient = null;
			try {
				/*String broker = "tcp://"+mqtt_server+":"+mqtt_port;
	            sampleClient = new MqttClient( broker, "ECHOBRIDGE" + getUUID(), persistence );

	            MqttConnectOptions connOpts = new MqttConnectOptions();
	            if (mqtt_username!=null && mqtt_username.trim().length()>0)
	            	connOpts.setUserName(mqtt_username);

	            if (mqtt_password!=null && mqtt_password.trim().length()>0)
	            	connOpts.setPassword(mqtt_password.toCharArray());

	            
	            connOpts.setCleanSession(true);
	            System.out.println("INFO : " + getFriendlyName() + " - connecting to broker: "+broker);
	            sampleClient.connect(connOpts); */
				
				// try secure connection first
				try {
					String broker = "ssl://"+mqtt_server+":"+mqtt_port;
		            sampleClient = new MqttClient( broker, "ECHOBRIDGE" + getUUID(), persistence );
		            MqttConnectOptions connOpts = new MqttConnectOptions();
		            if (mqtt_username!=null && mqtt_username.trim().length()>0)
		            	connOpts.setUserName(mqtt_username);
	
		            if (mqtt_password!=null && mqtt_password.trim().length()>0)
		            	connOpts.setPassword(mqtt_password.toCharArray());
	
		            connOpts.setCleanSession(true);
		            
		            System.out.println("INFO : " + getFriendlyName() + " - Connecting to broker: "+broker);
		            sampleClient.connect(connOpts);
		            
				}catch (Exception e) {
					//fallback to plain connection
					System.err.println("WARNING : " + getFriendlyName() + " - Error connecting securely. Trying fallback to plain connection.");
					e.printStackTrace();
					
					String broker = "tcp://"+mqtt_server+":"+mqtt_port;
		            sampleClient = new MqttClient( broker, "ECHOBRIDGE" + getUUID(), persistence );
		            MqttConnectOptions connOpts = new MqttConnectOptions();
		            if (mqtt_username!=null && mqtt_username.trim().length()>0)
		            	connOpts.setUserName(mqtt_username);
	
		            if (mqtt_password!=null && mqtt_password.trim().length()>0)
		            	connOpts.setPassword(mqtt_password.toCharArray());
	
		            connOpts.setCleanSession(true);
		            
		            System.out.println("INFO : " + getFriendlyName() + " - Connecting to broker: "+broker);
		            sampleClient.connect(connOpts);					
				}
		            
		        boolean isconnected = sampleClient!=null && sampleClient.isConnected();
		        if (!isconnected) {
		        	System.err.println("ERROR : " + getFriendlyName() + " - Unable to connect to broker with MQTTS and MQTTS.");
		        	return;
		        }
	            				
				
				
				
				
	            System.out.println("INFO : " + getFriendlyName() + " - Connected");
	            System.out.println("INFO : " + getFriendlyName() + " - Publishing message: "+mqtt_message);
	            MqttMessage message = new MqttMessage(mqtt_message.getBytes());
	            message.setQos(qos);
	            sampleClient.publish(mqtt_topic, message);
	            System.out.println("INFO : " + getFriendlyName() + " - Message published");
	            state = true;
	            sampleClient.disconnect();
	            System.out.println("INFO : " + getFriendlyName() + " - Disconnected");
	        } catch(MqttException me) {
	        	System.err.println("ERROR : " + getFriendlyName() + " - Device '"+getFriendlyName()+"' : error publishing on MQTT.");
	            System.err.println("        reason "+me.getReasonCode());
	            System.err.println("        msg "+me.getMessage());
	            System.err.println("        loc "+me.getLocalizedMessage());
	            System.err.println("        cause "+me.getCause());
	            System.err.println("        excep "+me);
	            
	            try {
	            	sampleClient.disconnect();
	            }catch(Exception e) {
	            	/* TODO */
	            }	            
	        }
			
		}catch(Exception oe)
		{
			System.err.println("ERROR : device '"+getFriendlyName()+"' : exception during turnon action : " + oe.getMessage());
		}
		
	}

	@Override
	public void turnOff() {
		XPath xPath = XPathFactory.newInstance().newXPath();
		String device_xpression= "./turnoff";
		NodeList devices = null;
		try {
			devices = (NodeList) xPath.compile(device_xpression).evaluate(n, XPathConstants.NODESET);
		
			if (devices==null || devices.getLength()==0)
			{
				System.err.println("ERROR : device '"+getFriendlyName()+"' : missing <turnoff /> node.");
				return;
			}
			
			Node turnoff_node = devices.item(0);
			String mqtt_server=turnoff_node.getAttributes().getNamedItem("mqtt_server").getNodeValue();
			String mqtt_port=turnoff_node.getAttributes().getNamedItem("mqtt_port").getNodeValue();
			String mqtt_topic=turnoff_node.getAttributes().getNamedItem("topic").getNodeValue();
			String mqtt_message=turnoff_node.getAttributes().getNamedItem("message").getNodeValue();
			String mqtt_username=turnoff_node.getAttributes().getNamedItem("mqtt_username").getNodeValue();
			String mqtt_password=turnoff_node.getAttributes().getNamedItem("mqtt_password").getNodeValue();

			
			MemoryPersistence persistence = new MemoryPersistence();
			int qos = 0;
			
			MqttClient sampleClient = null;
			try {
				
				// try secure connection first
				try {
					String broker = "ssl://"+mqtt_server+":"+mqtt_port;
		            sampleClient = new MqttClient( broker, "ECHOBRIDGE" + getUUID(), persistence );
		            MqttConnectOptions connOpts = new MqttConnectOptions();
		            if (mqtt_username!=null && mqtt_username.trim().length()>0)
		            	connOpts.setUserName(mqtt_username);
	
		            if (mqtt_password!=null && mqtt_password.trim().length()>0)
		            	connOpts.setPassword(mqtt_password.toCharArray());
	
		            connOpts.setCleanSession(true);
		            
		            System.out.println("INFO : " + getFriendlyName() + " - Connecting to broker: "+broker);
		            sampleClient.connect(connOpts);
		            
				}catch (Exception e) {
					//fallback to plain connection
					System.err.println("WARNING : " + getFriendlyName() + " - Error connecting securely. Trying fallback to plain connection.");
					e.printStackTrace();
					
					String broker = "tcp://"+mqtt_server+":"+mqtt_port;
		            sampleClient = new MqttClient( broker, "ECHOBRIDGE" + getUUID(), persistence );
		            MqttConnectOptions connOpts = new MqttConnectOptions();
		            if (mqtt_username!=null && mqtt_username.trim().length()>0)
		            	connOpts.setUserName(mqtt_username);
	
		            if (mqtt_password!=null && mqtt_password.trim().length()>0)
		            	connOpts.setPassword(mqtt_password.toCharArray());
	
		            connOpts.setCleanSession(true);
		            
		            System.out.println("INFO : " + getFriendlyName() + " - Connecting to broker: "+broker);
		            sampleClient.connect(connOpts);					
				}
		            
		        boolean isconnected = sampleClient!=null && sampleClient.isConnected();
		        if (!isconnected) {
		        	System.err.println("ERROR : " + getFriendlyName() + " - Unable to connect to broker with MQTTS and MQTTS.");
		        	return;
		        }
	            
	            
	            
				
/*				String broker = "tcp://"+mqtt_server+":"+mqtt_port;
	            sampleClient = new MqttClient( broker, "ECHOBRIDGE" + getUUID(), persistence );
	            MqttConnectOptions connOpts = new MqttConnectOptions();
	            if (mqtt_username!=null && mqtt_username.trim().length()>0)
	            	connOpts.setUserName(mqtt_username);

	            if (mqtt_password!=null && mqtt_password.trim().length()>0)
	            	connOpts.setPassword(mqtt_password.toCharArray());

	            connOpts.setCleanSession(true);
            
	            System.out.println("INFO : " + getFriendlyName() + " - Connecting to broker: "+broker);
	            sampleClient.connect(connOpts);
*/
	            System.out.println("INFO : " + getFriendlyName() + " - Connected");
	            System.out.println("INFO : " + getFriendlyName() + " - Publishing message: "+mqtt_message);
	            MqttMessage message = new MqttMessage(mqtt_message.getBytes());
	            message.setQos(qos);
	            sampleClient.publish(mqtt_topic, message);
	            System.out.println("INFO : " + getFriendlyName() + " - Message published");
	            state = false;
	            sampleClient.disconnect();
	            System.out.println("INFO : " + getFriendlyName() + " - Disconnected");
	        } catch(MqttException me) {
	        	System.err.println("ERROR : " + getFriendlyName() + " - Device '"+getFriendlyName()+"' : error publishing on MQTT.");
	            System.err.println("        reason "+me.getReasonCode());
	            System.err.println("        msg "+me.getMessage());
	            System.err.println("        loc "+me.getLocalizedMessage());
	            System.err.println("        cause "+me.getCause());
	            System.err.println("        excep "+me);
	            try {
	            	sampleClient.disconnect();
	            }catch(Exception e) {
	            	/* TODO */
	            }
	        }
			
		}catch(Exception oe)
		{
			System.err.println("ERROR : Device '"+getFriendlyName()+"' : exception during turnoff action : " + oe.getMessage());
		}	
	}
	
	
	
}
