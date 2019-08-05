package com.yzrilyzr.connection;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;

public class myFTP_Client
{
	DatagramSocket client;
	InetSocketAddress serveraddr;
	public myFTP_Client(String ip,int port)
	{
		try
		{
			serveraddr=new InetSocketAddress(ip,port);
			client=new DatagramSocket();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	public void login(String usr,String pwd){
		ByteArrayOutputStream b=new ByteArrayOutputStream();
		DataOutputStream d=new DataOutputStream(b);
		try
		{
			d.writeByte(myFTP_Server.C.LOGIN);
			d.writeUTF(usr);
			d.writeUTF(pwd);
			byte[] by=b.toByteArray();
			DatagramPacket k=new DatagramPacket(by,by.length);
			k.setAddress(serveraddr.getAddress());
			k.setPort(serveraddr.getPort());
			client.send(k);
		}
		catch (IOException e)
		{}
	}
	
}
