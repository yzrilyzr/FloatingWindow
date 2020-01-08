package com.yzrilyzr.engine2d;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import com.yzrilyzr.icondesigner.VECfile;
import java.util.Random;

public class DropItem extends Shape
{
	int type=0;
	Bitmap b;
	public float d=90,v=p(2);
	final String[] vec=new String[]{"ibomb","ilife","ipoint","ipower"};
	public DropItem(Enemy e,int type){
		x=e.x+r(100);
		y=e.y+r(100);
		r=p(200);
		this.type=type;
		try
		{
			b=VECfile.createBitmap(MainActivity.ctx,vec[type],p(40),p(40));
		}
		catch (Exception pe)
		{}
	}
	@Override
	public void onDraw(Canvas c)
	{
		c.drawBitmap(b,x-p(20),y-p(20),p);
		x+=v*Math.cos(d*Math.PI/180.0);
		y+=v*Math.sin(d*Math.PI/180.0);
	}
}
