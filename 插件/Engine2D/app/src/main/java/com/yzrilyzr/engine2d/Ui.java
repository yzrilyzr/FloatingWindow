package com.yzrilyzr.engine2d;
import android.graphics.Bitmap;
import com.yzrilyzr.icondesigner.VECfile;
import android.graphics.Canvas;
import android.view.MotionEvent;

public class Ui extends Shape
{
	Bitmap b;
	boolean visible=true;
	public Ui(String v,float x,float y,int w,int h){
		this.x=x;
		this.y=y;
		this.w=w;
		this.h=h;
		r=0;
		try
		{
			b=VECfile.createBitmap(MainActivity.ctx,v,w,h);
		}
		catch (Exception e)
		{}
		MainActivity.ui.add(this);
	}
	@Override
	public void onDraw(Canvas c)
	{
		if(visible)c.drawBitmap(b,x,y,p);
	}

	@Override
	public void onTouch(MotionEvent e)
	{
		
		super.onTouch(e);
	}
	
}
