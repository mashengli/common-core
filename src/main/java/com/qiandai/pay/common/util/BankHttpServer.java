package com.qiandai.pay.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;

public class BankHttpServer extends SimpleHttpServer.HttpServer {
	
	public BankHttpServer(SimpleHttpServer simpleHttpServer, int port) {
		simpleHttpServer.super(port);
	}

	@Override
	public void doGet(SimpleHttpServer.HttpProcessor p){
		
		try {
			p.writeSuccess(); 
			p.outputStream.write("GET: /Servlet HTTP/1.0 \r\n".getBytes());  
	    	p.outputStream.write("Connection: close \r\n".getBytes());  
	    	p.outputStream.write("Host: localhost:8080 \r\n".getBytes());  
	    	p.outputStream.write("Content-Type: application/x-www-form-urlencoded \r\n".getBytes()); 
	    	p.outputStream.write("".getBytes()); 
		} catch (IOException ignored) {
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("\r\n");		             
		sb.append("success");		
		PrintWriter printWriter = new PrintWriter(p.outputStream);
		printWriter.println(sb.toString());
		printWriter.flush();
	}

	@Override
	public void doPost(SimpleHttpServer.HttpProcessor p, String date) {
		try {
			p.writeSuccess(); 
//			p.outputStream.write("POST: /Servlet HTTP/1.0 \r\n".getBytes());  
//	    	p.outputStream.write("Connection: close \r\n".getBytes());  
//	    	p.outputStream.write("Host: localhost:8080 \r\n".getBytes());  
//	    	p.outputStream.write("Content-Type: application/x-www-form-urlencoded \r\n".getBytes()); 
//	    	p.outputStream.write("".getBytes()); 
		} catch (IOException ignored) {
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("\r\n");		             
		sb.append("success");		         
		PrintWriter printWriter=new PrintWriter(p.outputStream);
		printWriter.println(sb.toString());
		printWriter.flush();
	}

	@Override
	public void doMultiPart(SimpleHttpServer.HttpProcessor p, byte[] inputData, String filename,int start,int end) {
		OutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + File.separator + filename);
			fileOut.write(inputData, start, end-start);  
		} catch (IOException ignored) {
		} finally{
			IOUtils.closeQuietly(fileOut);
		}
	}

}
