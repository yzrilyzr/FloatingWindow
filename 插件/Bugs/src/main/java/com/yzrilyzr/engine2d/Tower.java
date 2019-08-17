package com.yzrilyzr.engine2d;
import android.graphics.Canvas;
import android.view.MotionEvent;
import com.yzrilyzr.icondesigner.VECfile;

public class Tower extends Shape
{
	float dmg,dtime,cdtime;
	float rdmg,rtime,rdtime,brtime;
	float x,y,money,slow;
	VECfile ico;
	int level;
	Bug target;
	public void compute(float dt)
	{
		if(cdtime<=0)
		{
			cdtime=dtime*(float)Math.pow(1.25,-level);
			attack();
		}
		else cdtime-=dt;
	}
	public void attack()
	{
		Map map=MainActivity.map;
		float tilew=map.tilew;

		for(Bug s:map.bugs)
		{
			if(target==null)target=s;
			if(!inRange(target)||target.hp<=0)
			{
				target=null;
				return;
			}
			if(target==s)
			{//单一目标
				//直接或范围
				s.hp-=dmg*Math.pow(1.25,level);
				//定向范围
				//~
				map.bullets.add(new Bullet(s,this,tilew*0.7f,tilew*0.5f,brtime));
				//子弹
				map.bullets.add(new Bullet(s,this,tilew*0.1f,tilew*2,-1));
				//远定
				map.bullets.add(new Bullet(s,this,tilew*1.5f,tilew*0.5f,-1));
			}
		}
	}
	public boolean inRange(Bug b)
	{
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
