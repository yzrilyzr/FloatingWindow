package com.yzrilyzr.connection;
import android.os.Handler;
import com.yzrilyzr.myclass.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.net.Socket;

public class myFTP_Client
{
	//Socket client;
	InetSocketAddress serveraddr;
	//boolean running=false;
	Receive rec=new Receive(){

		@Override
		public void onReceive(DataInputStream d) throws Throwable
		{
			// TODO: Implement this method
		}
	};

	public myFTP_Client(final String ip,final int port)
	{
		
				try
				{
					serveraddr=new InetSocketAddress(ip,port);
					//client=new Socket();
					//client.setSoTimeout(5000);
					//client.connect(serveraddr);
					//client.close();
					//client.setSoTimeout(5000);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
					util.toast("ftp连接失败");
				}
	}
	public FtpFile[] list(final String path)
	{
		final FtpFile[][] cachemfile=new FtpFile[1][];
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				try
				{
					Socket client=new Socket();
					client.connect(serveraddr);
					DataOutputStream o=new DataOutputStream(client.getOutputStream());
					o.writeByte(myFTP_Server.C.LIST);
					o.writeUTF(path);
					o.flush();
					DataInputStream d=new DataInputStream(client.getInputStream());
					if(d.readByte()==myFTP_Server.C.LIST)
					{
						int fs=d.readInt();
						FtpFile[] m=new FtpFile[fs];
						for(int i=0;i<fs;i++)
						{
							String g=d.readUTF();
							long h=d.readLong(),j=d.readLong();
							boolean z=d.readBoolean(),x=d.readBoolean();
							boolean n=d.readBoolean(),b=d.readBoolean();
							m[i]=new FtpFile(path+"/"+g,path,g,h,j,z,x,n,b);
							System.out.println(m[i].path);
						}
						cachemfile[0]=m;
					}
					client.close();
				}
				catch(Throwable e)
				{
					e.printStackTrace();
					util.toast("ftp连接失败");
				}
			}
		}).start();
		long t=System.currentTimeMillis();
		while(cachemfile[0]==null&&System.currentTimeMillis()-t<3000)
		{
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{
				return null;
			}
		}
		return cachemfile[0];
	}

	public void onReceive(DataInputStream d)throws Throwable
	{
		byte c=d.readByte();
		if(c==myFTP_Server.C.LOGINSUC)
		{
			util.toast("登录成功");
			/*new Handler(ctx.getMainLooper()).post(new Runnable(){
			 @Override
			 public void run()
			 {
			 list();
			 }
			 });*/
		}
		else if(c==myFTP_Server.C.LOGINFAIL)
		{
			util.toast("登录失败");
			/*path=null;
			 new Handler(ctx.getMainLooper()).post(new Runnable(){
			 @Override
			 public void run()
			 {
			 list();
			 }
			 });*/
		}

	}/*
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
	 onReceive(new DataInputStream(new ByteArrayInputStream(p.getData())));
	 for(Receive r:res)r.onReceive(new DataInputStream(new ByteArrayInputStream(p.getData())));
	 }
	 catch(Throwable e)
	 {
	 e.printStackTrace();
	 }
	 }
	 }
	 }).start();
	 }*/
	public void login(final String usr,final String pwd)
	{
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				try
				{
					Socket client=new Socket();
					client.connect(serveraddr);
					DataOutputStream o=new DataOutputStream(client.getOutputStream());
					o.writeByte(myFTP_Server.C.LOGIN);
					o.writeUTF(usr);
					o.writeUTF(pwd);
					o.flush();
					DataInputStream d=new DataInputStream(client.getInputStream());
					byte c=d.readByte();
					if(c==myFTP_Server.C.LOGINFAIL)
					{
						util.toast("登录失败");
					}
					else if(c==myFTP_Server.C.LOGINSUC)
					{
						util.toast("登录成功");
					}
					client.close();
				}
				catch(Throwable e)
				{
					e.printStackTrace();
					util.toast("ftp连接失败");
				}
			}
		}).start();
	}
	/*
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

	 }*/
	public void logout()
	{}
	public void stop()
	{
		try
		{
			//running=false;
			//client.close();
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
