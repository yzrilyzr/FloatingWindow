package com.yzrilyzr.engine2d;
import android.graphics.Bitmap;
import com.yzrilyzr.icondesigner.VECfile;
import android.graphics.Canvas;

public class Bug extends Shape
{
	Bitmap bugicon;
	float mscale=1;
	float x,y;
	float vx,vy;
	float ax,ay;
	float vel;
	
	public Bug(int id,float x,float y,int size)
	{
		try
		{
			int s=Shape.p(900*mscale)/size;
			bugicon=VECfile.createBitmap(MainActivity.ctx,"bugs/bug",s,s);
			this.x=x;
			this.y=y;
			vel=5;
		}
		catch(Throwable e)
		{
			MainActivity.toast(e);
		}
	}

	@Override
	public void onDraw(Canvas c)
	{
		// TODO: Implement this method
		super.onDraw(c);
		p.setColor(0xff000000);
		c.drawBitmap(bugicon,(int)(x-bugicon.getWidth()/2),(int)(y-bugicon.getHeight()/2),p);
	}

}
