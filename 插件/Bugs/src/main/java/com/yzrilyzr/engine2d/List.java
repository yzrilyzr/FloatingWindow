package com.yzrilyzr.engine2d;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.Menu;
import android.view.MotionEvent;
import java.util.concurrent.CopyOnWriteArrayList;
import android.graphics.PorterDuff;

public class List extends Ui
{
	private float ypos=0,lastypos=0,//y增量
	dy=0,lastdy=0,//拖动位移
	vy=0,//速度
	lasty,totalH;//按下时y
	private boolean touch=false,isScroll=false;
	private int scralpha=0;
	public CopyOnWriteArrayList<Ui> views=new CopyOnWriteArrayList<Ui>();
	public CopyOnWriteArrayList<Float> oys=new CopyOnWriteArrayList<Float>();//原始y
	//实际y=原始y+y增量+listy
	Canvas c;
	public List(int x,int y,int w,int h)
	{
		super(null,x,y,w,h);
		c=new Canvas(b);
	}
	public void measure()
	{
		ypos=0;
		totalH=0;
		scralpha=0;
		float max=0;
		oys.clear();
		for(Ui ui:views)
		{
			ui.x-=x;
			max=Math.max(ui.y+ui.h,max);
			oys.add(ui.y);
		}
		totalH=max;
	}
	@Override
	public void onDraw(Canvas ci)
	{
		if(!visible)
		{
			vy=0;
			return;
		}
		c.drawColor(0xffffffff,PorterDuff.Mode.CLEAR);
		//p.setColor(0xff15154a);
		//c.drawRect(x,y,x+w,y+h,p);
		int l=c.save();
		c.clipRect(0,0,w,h);
		if(vy!=0&&!touch)
		{
			ypos+=(vy*=0.95)*5f;
			if(vy>-0.1&&vy<0.1)
			{vy=0;}
		}
		ypos=limit(ypos,-totalH+h,0);
		if(ypos>0)ypos=0;
		//p.setColor(0xff15154a);
		for(int i=0;i<Math.min(views.size(),oys.size());i++)
		{
			Ui v=views.get(i);
			float oy=oys.get(i);
			//if()
			{
				v.y=oy+ypos;
				//v.h=y+hh;
				v.onDraw(c);
				//c.drawLine(x,v.h+v.y,x+w,v.y+v.h,p);
			}
			//else if(cy>h+cy)break;
			//cy+=h+1;
		}
		if(totalH!=0)
		{
			if(scralpha<255&&vy==0)scralpha+=51;
			p.setColor(0xffffffff-scralpha*0x1000000);
			float sy=(h-h*h/totalH)*ypos/(-totalH+h);
			c.drawRect(w-p(10),sy,w,sy+h*h/totalH,p);
		}
		c.restoreToCount(l);
		super.onDraw(ci);
	}

	@Override
	public void onMove(MotionEvent e)
	{
		// TODO: Implement this method
		scralpha=0;
		float py=e.getY();
		dy=py-lastdy;
		lastdy=py;
		ypos=lastypos+py-lasty;
		ypos=limit(ypos,-totalH+h,0);
		if(ypos>0)ypos=0;
		if(Math.abs(lastdy)>p(10))isScroll=true;
	}

	@Override
	public void onDown(MotionEvent e)
	{
		// TODO: Implement this method
		touch=true;
		isScroll=false;
		lasty=e.getY();
		lastdy=lasty;
		lastypos=ypos;
	}

	@Override
	public void onClick(MotionEvent e)
	{
		touch=false;
		vy=dy/2;
		//MainActivity.toast("u");
		if(!isScroll)
			for(Ui u:views){
				//MainActivity.toast("y:"+u.y+"  py:"+e.getY()+" ly:"+y+" ?y:"+ypos);
				if(u.contains(e.getX()-x,e.getY()-y))
				{
					u.onClick(e);
					break;
				}
				}
	}
	public void addView(Ui... m)
	{
		if(m!=null)
			for(Ui v:m)
				if(v!=null)
				{
					MainActivity.ui.remove(v);
					views.add(v);
					//v.parent=this;
				}
	}
}
