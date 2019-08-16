package com.yzrilyzr.engine2d;
import com.yzrilyzr.icondesigner.VECfile;
import android.graphics.Canvas;

public class Bullet extends Shape
{
	Tower parent;
	Bug target;
	VECfile icon;
	float brtime;
	float x,y,vx,vy;
	public void compute(Bug s){

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
