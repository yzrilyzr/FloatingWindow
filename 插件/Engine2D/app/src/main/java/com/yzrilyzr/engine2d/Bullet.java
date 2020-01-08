package com.yzrilyzr.engine2d;
import android.graphics.Canvas;

public class Bullet extends Shape
{
	public float v=0,d=0;
	public Bullet(float x,float y,float r,float v,float d){
		this.x=x;
		this.y=y;
		this.r=p(r);
		this.v=p(v);
		this.d=d;
		p.setColor(0xffff6666);
	}
	@Override
	public void onDraw(Canvas c)
	{
		x+=v*Math.cos(d*Math.PI/180.0);
		y+=v*Math.sin(d*Math.PI/180.0);
		c.drawCircle(x,y,r,p);
	}
}
