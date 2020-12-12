package net.cantylab.devices;

import java.net.URL;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.cantylab.utility.UrlUtility;

public class UrlDevice extends AbstractDevice{

	
	public UrlDevice(Node n, String ip, int port) {
		super(n,ip, port);
	}

	@Override
	public void turnOn() {
		XPath xPath = XPathFactory.newInstance().newXPath();
		String device_xpression= "./turnon";
		NodeList devices = null;
		
		try {
			devices = (NodeList) xPath.compile(device_xpression).evaluate(n, XPathConstants.NODESET);
		
			if (devices==null || devices.getLength()==0)
			{
				System.err.println("Device '"+getFriendlyName()+"' : missing <turnon /> node.");
				return;
			}
			
			Node turnon_node = devices.item(0);
			String method = turnon_node.getAttributes().getNamedItem("method").getNodeValue();
			String url = turnon_node.getAttributes().getNamedItem("url").getNodeValue();
			String params = turnon_node.getAttributes().getNamedItem("parameters").getNodeValue();
			String output_regex_check = turnon_node.getAttributes().getNamedItem("output_regex_check").getNodeValue();
			
			boolean ispost = "post".equals( method.toLowerCase() );
			
			try {
				StringBuffer response = UrlUtility.downloadUrl(new URL(url), ispost);
	
				if (response!=null) {
					if (response.toString().matches(output_regex_check))
						state = true;
				}else
					System.err.println("Device '"+getFriendlyName()+"' : error downloading url for <turnon /> action.");
			}catch(Exception e)
			{
				System.err.println("Device '"+getFriendlyName()+"' : malformed url for <turnon /> action.");
			}
		}catch(Exception oe)
		{
			System.err.println("Device '"+getFriendlyName()+"' : exception during turnon action : " + oe.getMessage());
		}
		
	}

	@Override
	public void turnOff() {
		XPath xPath = XPathFactory.newInstance().newXPath();
		String device_xpression= "./turnoff";
		NodeList devices = null;
		try {
			devices= (NodeList) xPath.compile(device_xpression).evaluate(n, XPathConstants.NODESET);
			
			if (devices==null || devices.getLength()==0)
			{
				System.err.println("Device '"+getFriendlyName()+"' : missing <turnoff /> node.");
			}
			
			Node turnon_node = devices.item(0);
			String method = turnon_node.getAttributes().getNamedItem("method").getNodeValue();
			String url = turnon_node.getAttributes().getNamedItem("url").getNodeValue();
			String params = turnon_node.getAttributes().getNamedItem("parameters").getNodeValue();
			String output_regex_check = turnon_node.getAttributes().getNamedItem("output_regex_check").getNodeValue();
			
			boolean ispost = "post".equals( url.toLowerCase() );
			
			try {
				StringBuffer response = UrlUtility.downloadUrl(new URL(url), ispost);
	
				if (response!=null) {
					if (response.toString().matches(output_regex_check))
						state=false;
				}else
					System.err.println("Device '"+getFriendlyName()+"' : error downloading url for <turnoff /> action.");
			}catch(Exception e)
			{
				System.err.println("Device '"+getFriendlyName()+"' : malformed url for <turnoff /> action.");
			}
		}catch(Exception oe) {
			System.err.println("Device '"+getFriendlyName()+"' : exception during turnoff action : " + oe.getMessage());			
		}
	}
	
	
	
}
