package net.cantylab.utility;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;


/**
 * Classe con le facility per lavorare con URL
 * 
 * @author giuliano
 *
 */
public class UrlUtility {

	
	
	/**
	 * Scarica l'URL passato per parametro come GET e restituisce
	 * uno stringbuffer con il contenuto dell'url in caso abbia una risposta.
	 * 
	 * in caso di eccezioni restituisce null.
	 *  
	 * @param url
	 * @param POST (true or false)
	 * 
	 * @return
	 */
	public static StringBuffer downloadUrl(URL url, boolean POST)
	{
		return downloadUrl(url, POST,-1,-1, null);
	}



	/**
	 * Scarica l'URL passato per parametro come GET o POST e restituisce
	 * uno stringbuffer con il contenuto dell'url in caso abbia una risposta.
	 * in caso di eccezioni restituisce null.
	 * 
	 * Non invia un payload.
	 *  
	 * @param url
	 * @param POST (true or false)
	 * @param timeout (milliseconds of timeout, to disable and use default <0)
	 * @return
	 */
	public static StringBuffer downloadUrl(URL url, boolean POST, int timeoutConn, int timeoutRead)
	{
		return downloadUrl(url, POST, timeoutConn, timeoutRead, null);
	}
	
	/**
	 * Scarica l'URL passato per parametro come GET o POST e restituisce
	 * uno stringbuffer con il contenuto dell'url in caso abbia una risposta.
	 * in caso di eccezioni restituisce null.
	 * 
	 * Invia un payload in formato JSON
	 *  
	 * @param url
	 * @param POST (true or false)
	 * @param timeout (milliseconds of timeout, to disable and use default <0)
	 * @param json_post_payload
	 * @return
	 */
	public static StringBuffer downloadUrl(URL url, boolean POST, int timeoutConn, int timeoutRead, String json_post_payload)
	{
	      InputStream is = null;
	      DataInputStream dis=null;
	      String s;

	      StringBuffer sb = null;
	      
	      try {

	    	  if (POST){
	    		  
		    	  HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		    	  connection.setDoInput(true); 
		    	  connection.setDoOutput(true);
	    		  if (timeoutConn>0){
	    			  connection.setConnectTimeout(timeoutConn);
	    		  }
	    		  if (timeoutRead>0){
	    			  connection.setReadTimeout(timeoutRead);
	    		  }
		    	  connection.setRequestMethod("POST"); 
		    	  connection.setFollowRedirects(true); 
	
		    	  
		    	  String query="";
		    	  if (url.toString().indexOf("?")>0)
		    	  {
		    		  query = url.toString().substring( url.toString().indexOf("?")+1 );
		    	  }
	
		    	  //connection.setRequestProperty("Accept-Language","it"); 
		    	  //connection.setRequestProperty("Accept", "application/cfm, image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, image/png, //*/*"); 
		    	  //connection.setRequestProperty("Accept-Encoding","gzip"); 
	
		    	  
		    	  //connection.setRequestProperty("Content-length",String.valueOf (query.length()));
		    	  if (json_post_payload!=null && json_post_payload.indexOf("{")>=0)
		    		  connection.setRequestProperty("Content-length",String.valueOf (json_post_payload.length()));
		    	  else
		    		  connection.setRequestProperty("Content-length",String.valueOf (query.length()));
		    
		    	  if (json_post_payload!=null && json_post_payload.indexOf("{")>=0)
		    		  connection.setRequestProperty("Content-Type","application/json");
		    	  else
		    		  connection.setRequestProperty("Content-Type","application/x-www- form-urlencoded");
		    		  
		    	  //logging
		    	  if (json_post_payload!=null && json_post_payload.indexOf("{")<0)
		    		  System.err.println("UrlUtility : invoked downloadUrl with NON-JSON PAYLOAD \r\n***" + json_post_payload + "\r\n***");
		    	  
		    	  connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)"); 
	
	
	
		    	  // open up the output stream of the connection 
		    	  DataOutputStream output = new DataOutputStream( connection.getOutputStream() ); 
	
		    	  
		  		
		  		
		    	  // write out the data 
		    	  //int queryLength = query.length(); 
		    	  //output.writeBytes( query );
		    	  if (json_post_payload!=null)
		    	  {
		    		  output.writeBytes(json_post_payload);
		    	  }
		    	  output.close();
	
		    	  int respCode=connection.getResponseCode(); 
		    	  String respMsg=connection.getResponseMessage(); 
	
		    	  // get ready to read the response 
		    	  dis = new DataInputStream( connection.getInputStream() );

	    	  }else
	    	  {
	    		  // --- GET ---	
		    	  System.out.println( (new Date()).getTime() + " - Opening connection to " + url.getHost() + ".");
	    		  URLConnection con = url.openConnection();
	    		  if (timeoutConn>0){
	    			  con.setConnectTimeout(timeoutConn);
	    		  }
	    		  if (timeoutRead>0){
	    			  con.setReadTimeout(timeoutRead);
	    		  }
	    		  
	    		  //con.setReadTimeout(timeout);
	    		  is = con.getInputStream();	    		  
	    		  
		    	  //is = url.openStream();         // throws an IOException
	
		    	  dis = new DataInputStream(new BufferedInputStream(is));
		    	  System.out.println( (new Date()).getTime() + " - Finished reading from " + url.getHost() + ".");
	    	  }
	    	  
	    	  sb = new StringBuffer();
	    	  while ((s = dis.readLine()) != null) {
	    		  sb.append(s);
	    		  sb.append("\r\n");
	    	  }
	    	  
	      } catch (MalformedURLException mue) {
	    	  System.err.println("UrlUtility : malformedURLexception durante downloadUrl di [" + url.toString() + "] : " + mue.getMessage());
	    	  System.out.println( (new Date()).getTime() + " - Malformed URL Exception");
	    	  sb=null;
	      } catch (IOException ioe) {
	    	  System.err.println("UrlUtility : ioexception durante downloadUrl di [" + url.toString() + "] : " + ioe.getMessage());
		      System.out.println( (new Date()).getTime() + " - IO exception.");
		      sb=null;
	      } finally {
	         try {
	        	 if (is!=null)
	        		 is.close();
	        	 
	        	 if (dis!=null)
	        		 dis.close();
	         } catch (IOException ioe) {
	            // just going to ignore this one
	         }

	      }		
	      System.out.println( (new Date()).getTime() + " - End 'downloadUrl' subroutine.");
	      return sb;
	}

	
	
	
}




