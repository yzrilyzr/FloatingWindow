package com.yzrilyzr.connection;
import java.net.*;

import android.content.SharedPreferences;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class myFTP_Server
{
	static boolean serverrun=false;
	static Thread serverthread=null;
	static ConcurrentHashMap<String,User> users=new ConcurrentHashMap<String,User>();//usr pwd
	static ConcurrentHashMap<String,User> loginuser=new ConcurrentHashMap<String,User>();//ip port
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
					loadUser();
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
	public static void print(Object o,Object... p)
	{
		System.out.printf("<FTPSERVER>"+o,p);
		System.out.println();
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
				long cid=i.readLong();
				User user=loginuser.get(cid+":"+p.getInetAddress().getHostAddress());
				boolean login=user!=null;
				print("ip:%s port:%d user:%s",p.getInetAddress().getHostAddress(),p.getPort(),user==null?"未登录":user.usr);
				if(c==C.LOGIN)
				{
					String usr=i.readUTF();
					String pwd=i.readUTF();
					print("用户%s登录",usr);
					if(enableU||(usr!=null&&users.get(usr)!=null&&pwd.equals(users.get(usr).pwd)))
					{
						o.writeByte(C.LOGINSUC);
						loginuser.put(cid+":"+p.getInetAddress().getHostAddress(),users.get(usr));
					}
					else o.writeByte(C.LOGINFAIL);
				}
				else if(enableU||login)
					if(c==C.LIST)
					{
						String rpath=i.readUTF(),path="";
						Matcher m=Pattern.compile("ftp://.*?:[0-9]*").matcher(rpath);
						String aa="";
						while(m.find())aa=m.group();
						rpath=rpath.substring(aa.length());
						if("".equals(path)||path==null)
						{
							if(!login)path=upath;
							else if(login&&"".equals(user.path)){
								path=util.mainDir+"myFtp/"+user.usr;
								File cu=new File(path);
								if(!cu.exists())cu.mkdirs();
							}
							else path=user.path;
						}
						//rpath=rpath.substring(aa.length());
						path=path+"/"+rpath;
						print("获取文件列表:%s",path);
						File fs=new File(path);
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
							if(enableU&&!login)path=upath;
							else if("".equals(user.path)){
								path=util.mainDir+"myFtp/"+user.usr;
								File cu=new File(path);
								if(!cu.exists())cu.mkdirs();
							}
							else path=user.path;
						}
						//rpath=rpath.substring(aa.length());
						path=path+"/"+rpath;
						print("获取文件:%s",path);
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
	public static void loadUser()
	{
		try
		{
			File f=new File(util.ctx.getDir("myFtp",util.ctx.MODE_PRIVATE).getAbsolutePath()+"/users");
			if(!f.exists())return;
			DataInputStream d=new DataInputStream(new FileInputStream(f));
			int c=d.readInt();
			users.clear();
			for(int i=0;i<c;i++)
			{
				User u=new User();
				u.usr=d.readUTF();
				u.pwd=d.readUTF();
				u.path=d.readUTF();
				users.put(u.usr,u);
			}
			d.close();
		}
		catch (Exception e)
		{
			print("载入用户数据库失败");
		}
	}
	public static void saveUser()
	{
		try
		{
			File f=new File(util.ctx.getDir("myFtp",util.ctx.MODE_PRIVATE).getAbsolutePath()+"/users");
			if(!f.exists())f.createNewFile();
			DataOutputStream d=new DataOutputStream(new FileOutputStream(f));
			d.writeInt(users.size());
			for(User u:users.values())
			{
				d.writeUTF(u.usr);
				d.writeUTF(u.pwd);
				d.writeUTF(u.path);
			}
			d.flush();
			d.close();
		}
		catch (Exception e)
		{
			print("保存用户数据库失败");
		}
	}
	public static void addUser(String u,String p,String pt)
	{
		users.put(u,new User(u,hash(p),pt));
	}
	public static void removeUser(String u)
	{
		users.remove(u);
	}
	public static String hash(String p)
	{
		return p;
	}
	public static class User
	{
		public String usr,pwd,path;
		public User()
		{}
		public User(String usr, String pwd, String path)
		{
			this.usr = usr;
			this.pwd = pwd;
			this.path = path;
		}
	}
}
