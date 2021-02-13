package com.yzrilyzr.game;
import android.graphics.*;
import android.view.*;
import com.yzrilyzr.icondesigner.*;
import java.util.concurrent.*;
import java.lang.reflect.*;

public class Ui
{
	public String id,event;
	public VECfile vec;
	public float x=0,y=0,width=1000,height=1000,mx,my;
	public Bitmap bmp;
	public Matrix matrix=new Matrix();
	public Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
	public int backcolor=0;
	public RectF rectf;
	public int gravity;
	public CopyOnWriteArrayList<BaseAnim> anim=new CopyOnWriteArrayList<BaseAnim>();
	public CopyOnWriteArrayList<BaseAnim> eanim=new CopyOnWriteArrayList<BaseAnim>();
	public boolean bound=false;
	float downx,downy;
	boolean down=false;
	public CopyOnWriteArrayList<Ui> child=new CopyOnWriteArrayList<Ui>();
	public Ui parent=null;
	public boolean exit=false;
	public void reverseAnim()
	{
		int lt=0;
		for(BaseAnim b:anim)lt=Math.max(lt,b.delay+b.duration);
		for(BaseAnim b:anim){
			b.delay=lt-b.delay-b.duration;
			b.reverse();
		}
		for(Ui x:child)x.reverseAnim();
	}
	public boolean onTouch(Scene sc,MotionEvent p2)
	{
		switch(p2.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				{
					float x=p2.getX(),y=p2.getY();
					if(rectf.contains(x,y))
					{
						downx=x;
						downy=y;
						down=true;
						return true;
					}
					else
					{
						down=false;
						return false;
					}
				}
			case MotionEvent.ACTION_UP:
				if(event!=null&&down==true)
				{
					float x=p2.getX(),y=p2.getY();
					if(rectf.contains(x,y))
					{
						down=false;
						try
						{
							Method m=sc.getClass().getMethod(event,Ui.class);
							m.invoke(sc,this);
						}
						catch(NoSuchMethodException e)
						{
							Utils.alert("错误:\""+id+"\"按键事件的方法\""+event+"\"未定义");
						}
						catch (Throwable e)
						{
							Utils.alert(e);
						}
						return true;
					}
				}
				break;

		}
		return false;
	}
	public void measure()
	{
		float rwid=parent==null?Utils.getWidth():parent.width;
		float rhei=parent==null?Utils.getHeight():parent.height;
		if((int)height==-2&&(int)width==-2)
			if(rwid>rhei)height=rhei;
			else width=rwid;
		if((int)height==-2)height=width*(float)vec.getHeight()/(float)vec.getWidth();
		if((int)width==-2)width=height*(float)vec.getWidth()/(float)vec.getHeight();

	}
	public void init()
	{
		//if(height==-2)height=100;
		if(vec!=null)bmp=VECfile.createBitmap(vec,(int)width,(int)height);
		//if(bmp==null)bmp=Bitmap.createBitmap((int)width,(int)height,Bitmap.Config.ARGB_8888);
		float rwid=parent==null?Utils.getWidth():parent.width;
		float rhei=parent==null?Utils.getHeight():parent.height;
		mx=0;
		my=0;
		p.setTextSize(Utils.px(12));
		if(gravity==2||gravity==5||gravity==8)mx+=rwid/2-width/2;
		if(gravity==4||gravity==5||gravity==6)my+=rhei/2-height/2;

		if(gravity==7||gravity==8||gravity==9)my+=rhei-height;
		if(gravity==3||gravity==6||gravity==9)mx+=rwid-width;
		rectf=new RectF();
	}
	public void onDraw(Canvas c)
	{
		if(parent==null)
		{
			matrix.setTranslate(x+mx,y+my);
		}
		else
		{
			matrix.set(parent.matrix);
			matrix.preTranslate(x+mx,y+my);
		}
		if(backcolor!=0)p.setColor(backcolor);
		p.setStyle(Paint.Style.FILL);
		if(!exit)for(BaseAnim an:anim)an.doAnim();
		else {
			if(eanim.size()==0)return;
			for(BaseAnim an:eanim)an.doAnim();
		}
		rectf.set(0,0,width,height);
		matrix.mapRect(rectf);
		/*if(bound||backcolor!=0)
		{
			c.save();
			c.clipRect(rectf);
			c.drawColor(backcolor);
			if(bmp!=null)c.drawBitmap(bmp,matrix,p);
			c.restore();
		}*/
		if(backcolor!=0)c.drawRect(rectf,p);
		if(bmp!=null)c.drawBitmap(bmp,matrix,p);
		c.drawText(String.format("Index:%d,%dx%d",Utils.draws,(int)rectf.width(),(int)rectf.height()),rectf.left,rectf.top+p.getTextSize(),p);
		Utils.draws++;
		p.setStyle(Paint.Style.STROKE);
		c.drawRect(rectf,p);
	}
}
