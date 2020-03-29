package com.yzrilyzr.engine2d;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.graphics.RectF;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;

public class SeekBar extends Ui2
{
	public int pro,max;
	RectF rec=new RectF();
	Canvas c;
	public SeekBar(int x,int y,int w,int h,int pro,int max){
		super(null,x,y,w,h);
		this.pro=pro;
		this.max=max;
		p.setStyle(Paint.Style.FILL);
		c=new Canvas(b);
	}

	@Override
	public void onMove(MotionEvent e)
	{
		// TODO: Implement this method
		super.onMove(e);
		pro=(int)(max*(limit(e.getX(),x+h/2,x+w-h/2)-x-h/2)/(w-h));
	}
	@Override
	public void onClick(MotionEvent e)
	{
		// TODO: Implement this method
		super.onClick(e);
		onMove(e);
	}
	@Override
	public void onDraw(Canvas cd)
	{
		// TODO: Implement this method
		if(!visible)return;
		//p.setXfermode(new PorterDuffXfermode());
		c.drawColor(0xfffffff,PorterDuff.Mode.CLEAR);
		float pa=h/2;
		float po=pa+limit((float)pro/(float)max,0f,1f)*(w-2*pa);
		rec.set(pa,pa/2,po,h-pa/2);
		p.setColor(0xfff1dea8);
		c.drawRoundRect(rec,pa,pa,p);
		p.setColor(0xffdddddd);
		rec.set(po,pa/2,w-pa,h-pa/2);
		c.drawRoundRect(rec,pa,pa,p);
		p.setColor(0xfff0c720);
		c.drawCircle(po,pa,pa,p);
		super.onDraw(cd);
	}
	
}
