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
		final FtpFile[][] cachemfile=new FtpFile[2][];
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				try
				{
					Socket client=new Socket();
					client.connect(serveraddr);
					DataOutputStream o=new DataOutputStream(client.getOutputStream());
					o.writeByte(C.LIST);
					o.writeUTF(path);
					o.flush();
					DataInputStream d=new DataInputStream(client.getInputStream());
					byte c=d.readByte();
					if(c==C.LIST)
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
							//System.out.println(m[i].path);
						}
						cachemfile[0]=m;
					}
					else if(c==C.PERMISSIONDENIED)util.toast("没有权限访问");
					else if(c==C.FILENOTEXIST)util.toast("文件不存在");
					cachemfile[1]=new FtpFile[0];
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
		while(cachemfile[1]==null&&System.currentTimeMillis()-t<3000)
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
					o.writeByte(C.LOGIN);
					o.writeUTF(usr);
					o.writeUTF(pwd);
					o.flush();
					DataInputStream d=new DataInputStream(client.getInputStream());
					byte c=d.readByte();
					if(c==C.LOGINFAIL)
					{
						util.toast("登录失败");
					}
					else if(c==C.LOGINSUC)
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
