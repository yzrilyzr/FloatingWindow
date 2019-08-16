package com.yzrilyzr.engine2d;
import com.yzrilyzr.icondesigner.VECfile;
import android.graphics.Canvas;

public class Bullet extends Shape
{
	Tower parent;
	Bug target;
	VECfile icon;
	float brtime;//>0启用 -1接触后消失
	float x,y,vx,vy;
	public Bullet(Bug b,Tower p, float r,float v,float br){
		target=b;
		parent=p;
		this.r=r;
		brtime=br;
		x=p.x;
		y=p.y;
		float d=(float)Math.sqrt((b.x-p.x)*(b.x-p.x)+(b.y-p.y)*(b.y-p.y));
		vx=v*(b.x-p.x)/d;
		vy=v*(b.y-p.y)/d;
	}
	public void compute(Bug s,float dt){
		brtime-=dt;
		if(brtime<=0)MainActivity.bullets.remove(this);
	}
	public void attack(Bug b){

	}

	@Override
	public void onDraw(Canvas c)
	{
		// TODO: Implement this method
		super.onDraw(c);
	}
	
}
