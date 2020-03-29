package com.yzrilyzr.engine2d;
import android.graphics.*;
import java.util.*;
import com.yzrilyzr.icondesigner.*;
import java.io.*;

public class Eg
{
	public static boolean showfps=true;
	public static int fpslimit=120;
	public static int bgcolor=0xffaaaaaa;
	public static GameActivity gameact;
	public static Paint p;
	public static HashMap<String,Bitmap> cache=new HashMap<String,Bitmap>();
	public static float scale=1;
	public static void setCacheCount(int c)
	{
		GameActivity.cachecount=c;
	}
	public static float getAbsWidth()
	{
		return gameact.getAbsWidth();
	}
	public static float getAbsHeight()
	{
		return gameact.getAbsHeight();
	}
	public static int pi(float p)
	{
		return (int)(p*scale);
	}
	//画布,文件,重心,缩放,重心偏移x、y
	//动画(相对于图像大小)偏移x、y%
	//动画缩放%,缩放中心x、y
	//动画旋转%,旋转中心x、y
	//动画透明度%
	public static void drawVec(Canvas c, String p1,int gravity,float scale, float dxperc,float dyperc,
		float atransx,float atransy,
		float ascale,float ascpx,float ascpy,
		float arotate,float aropx,float aropy,
		float aalpha)
	{		
		try
		{
			Bitmap b=null;
			Matrix m=new Matrix();
			if(!cache.containsKey(p1))
			{
				VECfile vf=VECfile.readFileFromIs(gameact.getAssets().open(p1+".vec"));
				vf.getWidth();
				int w=1,h=1;
				if(getAbsHeight()>getAbsWidth())
				{
					w=(int)(getAbsWidth()*scale/100f);
					h=(int)(getAbsWidth()*scale/100f*vf.getHeight()/vf.getWidth());
				}
				else
				{
					h=(int)(getAbsHeight()*scale/100f);
					w=(int)(getAbsHeight()*scale/100f*vf.getWidth()/vf.getHeight());
				}
				b=VECfile.createBitmap(vf,w,h);
				cache.put(p1,b);
			}
			else b=cache.get(p1);
			int x=0,y=0,w=b.getWidth(),h=b.getHeight();
			if(hasFlag(gravity,Gravity.RIGHT))x=(int)(getAbsWidth()-w);
			if(hasFlag(gravity,Gravity.BOTTOM))y=(int)(getAbsHeight()-h);
			if(hasFlag(gravity,Gravity.CENTER))
			{
				x=(int)(getAbsWidth()-w)/2;
				y=(int)(getAbsHeight()-h)/2;
			}
			x+=getAbsWidth()*dxperc/100f;
			y+=getAbsHeight()*dyperc/100f;
			m.postScale(ascale,ascale,w*ascpx/100f,h*ascpy/100f);
			m.postRotate(arotate*360f,w*aropx/100f,h*aropy/100f);
			m.postTranslate(x+atransx*w,y+atransy*h);
			p.setAlpha((int)(aalpha*255f));
			c.drawBitmap(b,m,p);
		}
		catch(Exception e)
		{
			p.setColor(0xffff0000);
			c.drawText(String.format("资源载入失败:%s",p1),0,p.getTextSize()*5,p);
		}
	}
	public static void drawVec(Canvas c,String vec,int Gravity,float scale)
	{
		drawVec(c,vec,Gravity,scale,0,0,0,0,1,0,0,0,0,0,1);
	}
	public static void delay(int ms)
	{
		try
		{
			Thread.sleep(ms);
		}
		catch (InterruptedException e)
		{}
	}
	public static void setBackground(int p0)
	{
		bgcolor=p0;
	}
	public static void setContext(GameActivity p0)
	{
		gameact=p0;
	}
	public static void setPaint(Paint o)
	{
		p=o;
	}
	public static void loadScene(Scene sc)
	{
		gameact.startScene(sc);
	}
	public static boolean hasFlag(long flag,long f)
	{
		return (flag&f)==f;
	}
	/*public void setFlag(Shape src,long all)
	 {
	 long sr=src.flag;
	 flag=(flag|all)-(sr|all)+sr;
	 }
	 public void setFlag(long f,long all)
	 {
	 flag=(flag|all)-all+f;
	 }*/
	public static float getNLinearValueByTime(float time,float starttime,float endtime)
	{
		float sec=endtime-starttime;
		if(sec<=0)sec=1;
		float x=limit((time-starttime)/sec,0f,1f);
		return NonLinearFunc(x);
	}
	public static float NonLinearFunc(float x)
	{
		x=limit(x,0,1);
		float y=x<0.5?(float)Math.pow(x,2)*2f:-(float)Math.pow(x-1f,2)*2f+1f;
		return limit(y,0,1);
	}
	public static float limit(float x,float min,float max)
	{
		return Math.max(Math.min(x,max),min);
	}
	public static long limit(long x,long min,long max)
	{
		return Math.max(Math.min(x,max),min);
	}
	public static int limit(int x,int min,int max)
	{
		return Math.max(Math.min(x,max),min);
	}

	public interface GameCBK
	{
		public abstract void render(Canvas c,float dt);
		public abstract void start();
		public abstract void stop();
		public abstract void pause();
	}
	public static class Gravity
	{
		public static int CENTER=1,LEFT=2,RIGHT=4,TOP=8,BOTTOM=16;
	}
}
