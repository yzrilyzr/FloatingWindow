package com.yzrilyzr.engine2d;

import android.graphics.Canvas;

public class PlayerBullet extends Shape
{
	public float v=0,d=0;
	public PlayerBullet(float x,float y,float r,float v,float d){
		this.x=x;
		this.y=y;
		this.r=r;
		this.v=v;
		this.d=d;
		p.setColor(0xff6666ff);
	}
	@Override
	public void onDraw(Canvas c)
	{
		x+=v*Math.cos(d*Math.PI/180.0);
		y+=v*Math.sin(d*Math.PI/180.0);
		c.drawCircle(x,y,r,p);
	}
}
