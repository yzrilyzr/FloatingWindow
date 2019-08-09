package com.yzrilyzr.connection;
import java.net.*;

import com.yzrilyzr.myclass.util;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class myFTP_Server
{
	static boolean serverrun=false;
	static Thread serverthread=null;
	static ConcurrentHashMap<String,String> users=new ConcurrentHashMap<String,String>();//usr pwd
	//static ConcurrentHashMap<String,Integer> loginuser=new ConcurrentHashMap<String,Integer>();//ip port
	protected static String upath=null;
	protected static boolean enableU=false;
	protected static int port=3721,maxthread=10;
	protected static ServerSocket server;
	public static void startServer()
	{
		if(serverthread!=null)
		{
			print("The server is Running");
			return;
		}
		serverrun=true;
		serverthread=new Thread(new Runnable(){
			@Override
			public void run()
			{
				try
				{
					ExecutorService exec=Executors.newFixedThreadPool(maxthread);
					server=new ServerSocket(port);
					//server=new DatagramSocket(port);
					//server.setSoTimeout(1000);
					util.toast("myFTP服务器已启动");
					while(serverrun)
					{
						try
						{
							//DatagramPacket p=new DatagramPacket(new byte[1024],1024);
							Socket sc=server.accept();
							exec.execute(new Process(sc));
						}
						catch(Throwable e)
						{}
					}
					exec.shutdown();
					server.close();
					serverthread=null;
					util.toast("myFTP服务器已停止");
				}
				catch(Throwable e)
				{
					e.printStackTrace();
					serverthread=null;
					util.toast("myFTP服务器已停止");
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
	public static void print(Object o)
	{
		System.out.println("<FTPSERVER>"+o);
	}
	static class Process implements Runnable
	{
		private Socket p;

		public Process(Socket p)
		{
			this.p = p;
		}
		@Override
		public void run()
		{
			try
			{
				DataInputStream i=new DataInputStream(p.getInputStream());
				DataOutputStream o=new DataOutputStream(p.getOutputStream());
				byte c=i.readByte();
				if(c==C.LOGIN)
				{
					print("用户登录");
					String usr=i.readUTF();
					String pwd=i.readUTF();
					if(enableU||pwd.equals(users.get(usr)))
					{
						o.writeByte(C.LOGINSUC);
						//loginuser.put(p.getInetAddress().getHostAddress(),p.getPort());
					}
					else o.writeByte(C.LOGINFAIL);
				}
				//else if(loginuser.get(p.getInetAddress().getHostAddress())==p.getPort())
				if(c==C.LIST)
				{
					String rpath=i.readUTF(),path="";
					Matcher m=Pattern.compile("ftp://.*?:[0-9]*").matcher(rpath);
					String aa="";
					while(m.find())aa=m.group();
					rpath=rpath.substring(aa.length());
					if("".equals(path)||path==null)
					{
						if(enableU)path=upath;
						else path=util.sdcard;
					}
					//rpath=rpath.substring(aa.length());
					path=path+"/"+rpath;
					File fs=new File(path);
					print("list:"+path);
					if(!fs.exists())o.writeByte(C.FILENOTEXIST);
					else
					{
						File[] f=fs.listFiles();
						if(f==null)o.writeByte(C.PERMISSIONDENIED);
						else
						{
							o.writeByte(C.LIST);
							o.writeInt(f.length);
							for(File x:f)
							{
								o.writeUTF(x.getName()+"");
								o.writeLong(x.length());
								o.writeLong(x.lastModified());
								o.writeBoolean(x.canRead());
								o.writeBoolean(x.canWrite());
								o.writeBoolean(x.isFile());
								o.writeBoolean(x.isDirectory());
							}
						}
					}
				}
				else if(c==C.GETFILE)
				{
					String rpath=i.readUTF(),path="";
					Matcher m=Pattern.compile("ftp://.*?:[0-9]*").matcher(rpath);
					String aa="";
					while(m.find())aa=m.group();
					rpath=rpath.substring(aa.length());
					if("".equals(path)||path==null)
					{
						if(enableU)path=upath;
						else path=util.sdcard;
					}
					//rpath=rpath.substring(aa.length());
					path=path+"/"+rpath;
					File x=new File(path);
					if(!x.exists())o.writeByte(C.FILENOTEXIST);
					else
					{
						o.writeByte(C.GETFILE);
						o.writeUTF(x.getName()+"");
						o.writeLong(x.length());
						o.writeLong(x.lastModified());
						o.writeBoolean(x.canRead());
						o.writeBoolean(x.canWrite());
						o.writeBoolean(x.isFile());
						o.writeBoolean(x.isDirectory());
					}
				}
				o.flush();
				i.close();
				o.close();
				p.close();
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}
}
