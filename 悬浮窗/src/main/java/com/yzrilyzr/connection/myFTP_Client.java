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
import java.util.ArrayList;
import java.io.ByteArrayInputStream;

public class myFTP_Client
{
	DatagramSocket client;
	InetSocketAddress serveraddr;
	boolean running=false;
	ArrayList<Receive> res=new ArrayList<Receive>();
	public myFTP_Client(String ip,int port)
	{
		try
		{
			serveraddr=new InetSocketAddress(ip,port);
			client=new DatagramSocket();
			//client.setSoTimeout(5000);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	public void list(String path)
	{
		ByteArrayOutputStream b=new ByteArrayOutputStream();
		DataOutputStream d=new DataOutputStream(b);
		try
		{
			d.writeByte(myFTP_Server.C.LIST);
			d.writeUTF(path);
			send(b);
		}
		catch (IOException e)
		{}
	}
	public void start()
	{
		running=true;
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				while(running)
				{
					try
					{
						byte[] b=new byte[4096];
						DatagramPacket p=new DatagramPacket(b,b.length);
						client.receive(p);
						for(Receive r:res)r.onReceive(new DataInputStream(new ByteArrayInputStream(p.getData())));
					}
					catch(Throwable e)
					{
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	public void addCallBack(Receive r)
	{
		res.add(r);
	}
	public void removeCallBack(Receive r)
	{
		res.remove(r);
	}
	public void login(String usr,String pwd)
	{
		ByteArrayOutputStream b=new ByteArrayOutputStream();
		DataOutputStream d=new DataOutputStream(b);
		try
		{
			d.writeByte(myFTP_Server.C.LOGIN);
			d.writeUTF(usr);
			d.writeUTF(pwd);
			send(b);
		}
		catch (IOException e)
		{}
	}

	private void send(final ByteArrayOutputStream b)
	{
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				try
				{
					byte[] by=b.toByteArray();
					DatagramPacket k=new DatagramPacket(by,by.length);
					k.setAddress(serveraddr.getAddress());
					k.setPort(serveraddr.getPort());
					client.send(k);
				}
				catch(Throwable e)
				{

				}
			}
		}).start();

	}
	public void logout()
	{}
	public void stop()
	{
		try
		{
			running=false;
			client.close();
		}
		catch(Throwable e)
		{

		}
	}
	public interface Receive
	{
		public abstract void onReceive(DataInputStream d)throws Throwable;
	}
}
