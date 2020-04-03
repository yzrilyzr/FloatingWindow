package com.yzrilyzr.engine2d;
import android.graphics.*;
import com.yzrilyzr.icondesigner.*;
import java.util.*;

public class Eg
{
	public static boolean showfps=true,showgrid=true;
	public static int fpslimit=60;
	public static int bgcolor=0xffaaaaaa;
	public static GameActivity gameact;
	public static Paint p;
	public static HashMap<String,Bitmap> cache=new HashMap<String,Bitmap>();
	public static float scale=1;
	public static int drawcount=0;
	public static void setCacheCount(int c)
	{
		GameActivity.cachecount=c;
	}
	/*public static void disableTouch(){
	 gameact.disabletouch=true;
	 }
	 public static void enableTouch(){
	 gameact.disabletouch=false;
	 }*/
	public static float getAbsWidth()
	{
		return gameact.getAbsWidth();
	}
	public static float getAbsHeight()
	{
		return gameact.getAbsHeight();
	}
	public static float p(float p)
	{
		if(getAbsHeight()>getAbsWidth())return (p*getAbsWidth()*scale/100f);
		else return(p*getAbsHeight()*scale/100f);
	}
	public static int pi(float p)
	{
		return (int)p(p);
	}
	//画布,文件,重心,缩放,重心偏移x、y(相对重心refRect，绝对重心于屏幕),映射绝对区域,重心参考
	//动画偏移x、y%,时间(0～1),动画重心
	//动画缩放%,缩放中心x、y
	//动画旋转%,旋转中心x、y
	//动画透明度%

	//x y重心偏移是相对于各自的比例
	public static void drawVec(Canvas c, String p1,int gravity,float scale, float dxperc,float dyperc,RectF maprect,RectF refrect,
		float atransx,float atransy,float atranstime,int atransgravity,
		float ascale,float ascpx,float ascpy,
		float arotate,float aropx,float aropy,
		float aalpha)
	{		
		if(aalpha==0||ascale==0||scale==0||p1==null)return;
		try
		{
			Bitmap b=null;
			Matrix m=new Matrix();
			if(!cache.containsKey(p1))
			{
				VECfile vf=VECfile.readFileFromIs(gameact.getAssets().open(p1+".vec"));
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
			float x=0,y=0,w=b.getWidth(),h=b.getHeight();
			if(atransgravity==0)
			{
				//相对坐标
				if(refrect!=null)
				{
					if(hasFlag(gravity,Gravity.CENTER))
					{
						x=refrect.centerX()-w/2;
						y=refrect.centerY()-h/2;
					}
					if(hasFlag(gravity,Gravity.LEFT))x=refrect.left;
					if(hasFlag(gravity,Gravity.TOP))y=refrect.top;
					if(hasFlag(gravity,Gravity.RIGHT))x=(refrect.right-w);
					if(hasFlag(gravity,Gravity.BOTTOM))y=(refrect.bottom-h);
					x+=refrect.width()*(dxperc+atransx)/100f;
					y+=refrect.height()*(dyperc+atransy)/100f;
				}
				//绝对坐标
				else
				{
					if(hasFlag(gravity,Gravity.CENTER))
					{
						x=(getAbsWidth()-w)/2;
						y=(getAbsHeight()-h)/2;
					}
					if(hasFlag(gravity,Gravity.LEFT))x=0;
					if(hasFlag(gravity,Gravity.TOP))y=0;
					if(hasFlag(gravity,Gravity.RIGHT))x=(getAbsWidth()-w);
					if(hasFlag(gravity,Gravity.BOTTOM))y=(getAbsHeight()-h);
					x+=getAbsWidth()*(dxperc+atransx)/100f;
					y+=getAbsHeight()*(dyperc+atransy)/100f;
				}
			}
			else
			{
				if(refrect!=null)
				{
					if(hasFlag(gravity,Gravity.CENTER))
					{
						x=refrect.centerX()-w/2;
						y=refrect.centerY()-h/2;
					}
					if(hasFlag(gravity,Gravity.LEFT))x=refrect.left;
					if(hasFlag(gravity,Gravity.TOP))y=refrect.top;
					if(hasFlag(gravity,Gravity.RIGHT))x=(refrect.right-w);
					if(hasFlag(gravity,Gravity.BOTTOM))y=(refrect.bottom-h);
				}
				//绝对坐标
				else
				{
					if(hasFlag(gravity,Gravity.CENTER))
					{
						x=(getAbsWidth()-w)/2;
						y=(getAbsHeight()-h)/2;
					}
					if(hasFlag(gravity,Gravity.LEFT))x=0;
					if(hasFlag(gravity,Gravity.TOP))y=0;
					if(hasFlag(gravity,Gravity.RIGHT))x=(getAbsWidth()-w);
					if(hasFlag(gravity,Gravity.BOTTOM))y=(getAbsHeight()-h);
				}
				float x2=0,y2=0;
				if(hasFlag(atransgravity,Gravity.CENTER))
				{
					x2=(getAbsWidth()-w)/2;
					y2=(getAbsHeight()-h)/2;
				}
				if(hasFlag(atransgravity,Gravity.LEFT))x2=0;
				if(hasFlag(atransgravity,Gravity.TOP))y2=0;
				if(hasFlag(atransgravity,Gravity.RIGHT))x2=(getAbsWidth()-w);
				if(hasFlag(atransgravity,Gravity.BOTTOM))y2=(getAbsHeight()-h);
				x+=(refrect==null?getAbsWidth():refrect.width())*dxperc/100f;
				y+=(refrect==null?getAbsHeight():refrect.height())*dyperc/100f;
				x2+=getAbsWidth()*atransx/100f;
				y2+=getAbsHeight()*atransy/100f;
				x+=(x2-x)*atranstime;
				y+=(y2-y)*atranstime;
			}
			m.postScale(ascale,ascale,w*ascpx/100f,h*ascpy/100f);
			m.postRotate(arotate*360f,w*aropx/100f,h*aropy/100f);
			m.postTranslate(x,y);
			p.setAlpha((int)(aalpha*255f));
			c.drawBitmap(b,m,p);
			drawcount++;
			if(maprect!=null)
			{
				maprect.set(0,0,w,h);
				m.mapRect(maprect);
			}
			if(showgrid)
			{
				RectF re=new RectF(0,0,w,h);
				m.mapRect(re);
				p.setColor(0xff4444ff);
				p.setStyle(Paint.Style.STROKE);
				p.setStrokeWidth(1);
				c.drawRect(re,p);
				p.setStyle(Paint.Style.FILL);
				p.setTextSize(p(3));
				RectF r=new RectF(re);
				r.left*=100f/getAbsWidth();
				r.top*=100f/getAbsHeight();
				r.right*=100f/getAbsWidth();
				r.bottom*=100f/getAbsHeight();
				c.drawText(String.format("%.2f",r.left),re.left,re.centerY(),p);
				c.drawText(String.format("%.2f",r.top),re.centerX(),re.top,p);
				c.drawText(String.format("%.2f",r.right),re.right,re.centerY(),p);
				c.drawText(String.format("%.2f",r.bottom),re.centerX(),re.bottom,p);
				c.drawText(String.format("%.2f,%.2f",r.centerX(),r.centerY()),re.centerX(),re.centerY(),p);

			}
		}
		catch(Exception e)
		{
			p.setColor(0xffff0000);
			c.drawText(String.format("资源载入失败:%s",p1),0,p.getTextSize()*5,p);
		}
	}
	public static void drawVec(Canvas c, String p1,int gravity,float scale, float dxperc,float dyperc,
		float atransx,float atransy,
		float ascale,float ascpx,float ascpy,
		float arotate,float aropx,float aropy,
		float aalpha)
	{
		drawVec(c,p1,gravity,scale,dxperc,dyperc,null,null,
			atransx,atransy,0,0,
			ascale,ascpx,ascpy,
			arotate,aropx,aropy,
			aalpha);
	}
	public static void drawVec(Canvas c,String vec,int Gravity,float scale,float dx,float dy)
	{
		drawVec(c,vec,Gravity,scale,dx,dy,
			0,0,1,0,0,0,0,0,1);
	}
	public static void drawVec(Canvas c,String vec,int Gravity,float scale)
	{
		drawVec(c,vec,Gravity,scale,0,0,
			0,0,
			1,0,0,
			0,0,0,
			1);
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
	public static void startScene(Scene sc)
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
	public static float getSinValueByTime(float time,float starttime,float endtime)
	{
		float sec=endtime-starttime;
		if(sec<=0)sec=1;
		float x=limit((time-starttime)/sec,0f,1f);
		return SinFunc(x);
	}
	public static float SinFunc(float x)
	{
		x=limit(x,0,1);
		float y=(float)Math.sin(x*Math.PI);
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
	public static int random(int min,int max)
    {
        return (int)Math.floor(Math.random()*(max-min))+min;
    }
	public static double getArc(double x,double y,double r)
	{
		double a=Math.asin(y/r);
		if(x<0)a=Math.PI-a;
		if(x>0&&y<0)a+=2*Math.PI;
		return a;
	}
	public interface GameCBK
	{
		public abstract void render(Canvas c,float dt);
		public abstract void start();
		public abstract void stop();
		//public abstract void touch(MotionEvent e);
	}
	public static class Gravity
	{
		public static int CENTER=1,LEFT=2,RIGHT=4,TOP=8,BOTTOM=16;
	}
}
