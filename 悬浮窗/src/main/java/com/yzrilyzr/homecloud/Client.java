package com.yzrilyzr.homecloud;
import android.content.*;
import android.view.*;
import android.view.View.*;
import com.yzrilyzr.floatingwindow.*;
import com.yzrilyzr.myclass.*;
import com.yzrilyzr.ui.*;
import java.net.*;
import java.util.*;

import com.yzrilyzr.floatingwindow.Window;
import android.widget.*;
import com.yzrilyzr.floatingwindow.viewholder.*;
import java.io.*;
import android.os.*;
import android.widget.LinearLayout.*;

public class Client 
{
	//Socket client;
	InetSocketAddress serveraddr;
	long CID=0;
	//boolean running=false;
	Context ctx;
	Window w,loginw;
	static boolean isLogin=false;

	/*Receive rec=new Receive(){
	 @Override
	 public void onReceive(byte c)
	 {
	 // TODO: Implement this method
	 }
	 };*/
	public Client(final String ip,final int port)
	{
		try
		{
			do CID=new Random().nextLong();while(CID==0);
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
	public void connect()
	{

	}
	/*public FtpFile getFile(final String path)
	 {
	 final FtpFile[] cachemfile=new FtpFile[1];
	 final boolean[] ca=new boolean[]{true};
	 new Thread(new Runnable(){
	 @Override
	 public void run()
	 {
	 try
	 {
	 Socket client=new Socket();
	 client.connect(serveraddr);
	 DataOutputStream o=new DataOutputStream(client.getOutputStream());
	 o.writeByte(C.GETFILE);
	 o.writeLong(CID);
	 o.writeUTF(path);
	 o.flush();
	 DataInputStream d=new DataInputStream(client.getInputStream());
	 byte c=d.readByte();
	 if(c==C.GETFILE)
	 {
	 String g=d.readUTF();
	 long h=d.readLong(),j=d.readLong();
	 boolean z=d.readBoolean(),x=d.readBoolean();
	 boolean n=d.readBoolean(),b=d.readBoolean();
	 cachemfile[0]=new FtpFile(path,"",g,h,j,z,x,n,b);
	 cachemfile[0].path();
	 }
	 else if(c==C.PERMISSIONDENIED)util.toast("没有权限访问");
	 else if(c==C.FILENOTEXIST)util.toast("文件不存在");
	 ca[0]=false;
	 client.close();
	 rec(c);
	 }
	 catch(Throwable e)
	 {
	 e.printStackTrace();
	 util.toast("ftp连接失败");
	 }
	 }
	 }).start();
	 long t=System.currentTimeMillis();
	 while(ca[0]&&System.currentTimeMillis()-t<3000)
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

	 public void setReceive(Receive rec)
	 {
	 this.rec = rec;
	 }

	 public boolean delete(final String path)
	 {
	 return false;
	 }
	 public boolean exists(final String path)
	 {
	 return false;
	 }
	 public boolean mkdirs(final String path)
	 {
	 return false;
	 }
	 public boolean createNewFile(final String path)
	 {
	 return false;
	 }
	 public boolean renameTo(final FtpFile path,final String n)
	 {
	 return false;
	 }
	 public FtpInputStream openFile(final String path)
	 {
	 return null;
	 }
	 public FtpFile[] list(final String path)
	 {
	 final FtpFile[][] cachemfile=new FtpFile[1][];
	 final boolean[] ca=new boolean[]{true};
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
	 o.writeLong(CID);
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
	 ca[0]=false;
	 client.close();
	 rec(c);
	 }
	 catch(Throwable e)
	 {
	 e.printStackTrace();
	 rec(C.TIMEOUT);
	 }
	 }
	 }).start();
	 long t=System.currentTimeMillis();
	 while(ca[0]&&System.currentTimeMillis()-t<3000)
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
	 private void rec(final byte c)
	 {
	 new Handler(util.ctx.getMainLooper()).post(new Runnable(){
	 @Override
	 public void run()
	 {
	 if(rec!=null)rec.onReceive(c);
	 }
	 });
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
	 client.setSoTimeout(1000);
	 client.connect(serveraddr);
	 DataOutputStream o=new DataOutputStream(client.getOutputStream());
	 o.writeByte(C.LOGIN);
	 o.writeLong(CID);
	 o.writeUTF(usr);
	 o.writeUTF(pwd);
	 o.flush();
	 DataInputStream d=new DataInputStream(client.getInputStream());
	 byte c=d.readByte();
	 client.close();
	 rec(c);
	 }
	 catch(Throwable e)
	 {
	 e.printStackTrace();
	 rec(C.TIMEOUT);
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
	 public abstract void onReceive(byte c)
	 }*/
}
