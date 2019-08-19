package com.yzrilyzr.engine2d;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.view.MotionEvent;

public class Switch extends Ui
{
	boolean isOn=false;
	RectF rec=new RectF();
	Canvas c;
	public Switch(int x,int y,int w,int h,boolean iso){
		super(null,x,y,w,h);
		p.setStyle(Paint.Style.FILL);
		isOn=iso;
		c=new Canvas(b);
	}

	@Override
	public void onClick(MotionEvent e)
	{
		// TODO: Implement this method
		super.onClick(e);
		isOn=!isOn;
	}




	@Override
	public void onDraw(Canvas uc)
	{
		// TODO: Implement this method
		if(!visible)return;
		c.drawColor(0xfffffff,PorterDuff.Mode.CLEAR);
		float pa=h/2;
		rec.set(pa/4,pa/4,w-pa/4,h-pa/4);
		p.setColor(isOn?0xfff1dea8:0xffdddddd);
		c.drawRoundRect(rec,pa,pa,p);
		p.setColor(0xfff0c720);
		c.drawCircle(isOn?pa+w/2:pa,pa,pa,p);
		super.onDraw(uc);
	}
}
