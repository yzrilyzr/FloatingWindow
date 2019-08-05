package com.yzrilyzr.connection;
import java.io.*;
import java.net.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import com.yzrilyzr.myclass.util;

public class myFTP
{
	static boolean serverrun=false;
	static Thread serverthread=null;
	static ConcurrentHashMap<String,String> users=new ConcurrentHashMap<String,String>();//usr pwd
	static ConcurrentHashMap<String,Integer> loginuser=new ConcurrentHashMap<String,Integer>();//ip port
	public static void startServer()
	{
		if(serverthread!=null)
		{
			print("The server is Running");
			return;
		}
		serverrun=true;
		serverthread=new Thread(new Runnable(){
			DatagramSocket server;
			@Override
			public void run()
			{
				try
				{
					server=new DatagramSocket();
					while(serverrun)
					{
						DatagramPacket p=new DatagramPacket(new byte[1024],1024);
						server.receive(p);
						DataInputStream i=new DataInputStream(new ByteArrayInputStream(p.getData()));
						ByteArrayOutputStream os=new ByteArrayOutputStream();
						DataOutputStream o=new DataOutputStream(os);
						byte c=i.readByte();
						if(c==C.LOGIN)
						{
							String usr=i.readUTF();
							String pwd=i.readUTF();
							if(pwd.equals(users.get(usr)))
							{
								o.writeByte(C.LOGINSUC);
								loginuser.put(p.getAddress().getHostAddress(),p.getPort());
							}
							else o.writeByte(C.LOGINFAIL);
						}
						else if(loginuser.get(p.getAddress().getHostAddress())==p.getPort())
							if(c==C.LIST)
							{
								String path=i.readUTF();
								if(path==null)path=util.sdcard;
								File fs=new File(path);
								if(!fs.exists())o.writeByte(C.FILENOTEXIST);
								else{
									File[] f=fs.listFiles();
									if(f==null)o.writeByte(C.PERMISSIONDENIED);
									else{
										o.writeByte(C.LIST);
										o.writeInt(f.length);
										for(File x:f){
											o.writeUTF(x.getName());
											o.writeLong(x.length());
											o.writeLong(x.lastModified());
											o.writeBoolean(x.canRead());
											o.writeBoolean(x.canWrite());
										}
									}
								}
							}
						p.setData(os.toByteArray());
						server.send(p);
						i.close();
						o.close();
						p=null;
					}
					serverthread=null;
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		});
		serverthread.start();
	}
	public void getIP()
	{
		new Thread(Thread.currentThread().getThreadGroup(),new Runnable(){
			@Override
			public void run()
			{
				String strIP="";
				try
				{
					URL url = new URL("http://m.tool.chinaz.com/ipsel"); 
					URLConnection conn = url.openConnection(); 
					BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); 
					String line = null; 
					StringBuffer result = new StringBuffer(); 
					while((line = reader.readLine()) != null)result.append(line);
					reader.close();
					strIP=result.toString();
					strIP = strIP.substring(strIP.indexOf("您的IP地址")+30);
					strIP=strIP.substring(0,strIP.indexOf("</b>"));
				}
				catch(Throwable e)
				{
					try
					{
						strIP=InetAddress.getLocalHost().getHostAddress();
					}
					catch (UnknownHostException e2)
					{
						strIP="未知";
					}
				}
				System.out.println(strIP);
			}},"getIP_Server").start();
	}
	public static class C
	{
		public static final byte 
		LOGIN=1,
		LOGINSUC=2,
		LOGINFAIL=3,
		LOGOUT=4,
		HBT=5,
		LIST=6,
		FILENOTEXIST=7,
		PERMISSIONDENIED=8;
	}
	public static void print(Object o)
	{
		System.out.println("<FTPSERVER>"+o);
	}
}
