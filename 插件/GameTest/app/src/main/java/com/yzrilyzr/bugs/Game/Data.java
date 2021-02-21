package com.yzrilyzr.bugs.Game;
import com.yzrilyzr.game.*;
import java.io.*;

public class Data
{
	//玩家等级 经验 分数
	public static int level=0,exp=0;
	//塔解锁，升级解锁，地图解锁
	public static int unlocktower=2,unlocklevel=0,unlockmap=0;
	//统计数据
	public static long tmoney=0,tbugs=0,tlives=0,tscore=0;
	public static int[] scores=new int[20];
	private static long savetime=System.currentTimeMillis();
	public static int getNextlevelExp(){
		return (int)(100f*Math.pow(1.2f,level));
	}
	public static void save(){
		if(System.currentTimeMillis()-savetime<500)return;
		try{
			Utils.print("save");
			ByteArrayOutputStream b=new ByteArrayOutputStream();
			FileOutputStream f=new FileOutputStream(Utils.mainDir+"/data");
			DataOutputStream d=new DataOutputStream(b);
			d.writeInt(level);
			d.writeInt(exp);
			
			d.writeInt(unlocktower);
			d.writeInt(unlocklevel);
			d.writeInt(unlockmap);
			
			d.writeLong(tmoney);
			d.writeLong(tbugs);
			d.writeLong(tlives);
			d.writeLong(tscore);
			
			d.writeInt(scores.length);
			for(int s:scores)d.writeInt(s);
			
			byte[] a=b.toByteArray();
			long c=0;
			for(byte dp:a)c+=(int)dp;
			d.writeLong(c);
			f.write(b.toByteArray());
			f.flush();
			f.close();
		}catch(Throwable e){
			Utils.alert(e);
		}
		savetime=System.currentTimeMillis();
	}
	public static void load(){
		try
		{
			if(!new File(Utils.mainDir+"/data").exists()){
				save();
				return;
			}
			FileInputStream fis=new FileInputStream(Utils.mainDir+"/data");
			DataInputStream dis=new DataInputStream(fis);
			byte[] bc=new byte[dis.available()-8];
			dis.read(bc);
			long cc=0;
			for(byte dp:bc)cc+=(int)dp;
			long c=dis.readLong();
			if(c!=cc)throw new Exception("数据读取错误");
			dis.close();
			dis=new DataInputStream(new ByteArrayInputStream(bc));
			//
			level=dis.readInt();
			exp=dis.readInt();
			
			unlocktower=dis.readInt();
			unlocklevel=dis.readInt();
			unlockmap=dis.readInt();
			
			tmoney=dis.readLong();
			tbugs=dis.readLong();
			tlives=dis.readLong();
			tscore=dis.readLong();
			
			scores=new int[dis.readInt()];
			for(int i=0;i<scores.length;i++)scores[i]=dis.readInt();
		}
		catch (Exception e)
		{
			Utils.alert(e);
		}
	}
}
