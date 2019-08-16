package com.yzrilyzr.engine2d;
import com.yzrilyzr.icondesigner.VECfile;
import android.graphics.Canvas;
import android.view.MotionEvent;

public class Tower extends Shape
{
	float dmg,dtime,cdtime;
	float rdmg,rtime,rdtime,brtime;
	float x,y,money,slow;
	VECfile ico;
	int level;
	Bug target;
	public void compute(Bug s,float dt){
		if(target==null)target=s;
		if(!inRange(target)||target.hp<=0){
			target=null;
			return;
		}
		if(target==s)//单一目标
		{
			if(cdtime<=0){
				cdtime=dtime*(float)Math.pow(1.25,-level);
				attack(s);
			}
			else cdtime-=dt;
		}
	}
	public void attack(Bug b){
		float tilew=MainActivity.map.tilew;
		//直接或范围
		b.hp-=dmg*Math.pow(1.25,level);
		//定向范围
		//~
		MainActivity.bullets.add(new Bullet(b,this,tilew*0.7f,tilew*0.5f,brtime));
		//子弹
		MainActivity.bullets.add(new Bullet(b,this,tilew*0.1f,tilew*2,-1));
		//远定
		MainActivity.bullets.add(new Bullet(b,this,tilew*1.5f,tilew*0.5f,-1));
	}
	public boolean inRange(Bug b){
		return contains(b.x,b.y);
	}
	@Override
	public void onDraw(Canvas c)
	{
		// TODO: Implement this method
		super.onDraw(c);
	}

	@Override
	public void onTouch(MotionEvent e)
	{
		// TODO: Implement this method
		super.onTouch(e);
	}
	
}
