package com.yzrilyzr.engine2d;
import android.graphics.Bitmap;
import com.yzrilyzr.icondesigner.VECfile;
import android.graphics.Canvas;
import android.graphics.PointF;
import java.util.ArrayList;
import com.yzrilyzr.engine2d.Map.AstarPoint;

public class Bug extends Shape
{
	Bitmap bugicon;
	float vx,vy;
	float ax,ay;
	float mscale=1;
	//float x,y;
	float vel;
	float hp,frztime;
	float rdmg,rtime,rdtime,rdcdtime;
	float money,slow;
	VECfile ico,dico;
	AstarPoint wayp;
	int wayIndex;
	public Bug(float x,float y,int size)
	{
		try
		{
			int s=Shape.pi(900*mscale)/size;
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
	public Bug(int id,int way)
	{
		if(id==0)
		{
			hp=20;vel=0.005f;
			wayIndex=way;
			wayp=MainActivity.map.wpwaypoint.get(wayIndex).get(0);
			x=wayp.x;
			y=wayp.y;
			int s=(int)MainActivity.map.tilew;
			try
			{
				bugicon=VECfile.createBitmap(MainActivity.ctx,"bugs/bug",s,s);
			}
			catch (Exception e)
			{}

		}
	}
	public void compute(float dt)
	{
		Map map=MainActivity.map;
		float tilew=map.tilew;
		if(hp<=0)
		{
			map.bugs.remove(this);
			map.money+=money;
			Canvas c=new Canvas(map.background);
			c.drawBitmap(VECfile.createBitmap(dico,(int)tilew,(int)tilew),x,y,p);
		}
		float d=(float)Math.sqrt((wayp.x-x)*(wayp.x-x)+(wayp.y-y)*(wayp.y-y));
		if(frztime>0)frztime-=dt;
		else if(d!=0)
		{
			float v2=vel*limit(slow,0.3f,1);
			x+=(wayp.x-x)*v2/d;
			y+=(wayp.y-y)*v2/d;
		}
		if(d<0.005f*tilew)
		{
			ArrayList<Map.AstarPoint> u=map.wpwaypoint.get(wayIndex);
			int f=u.indexOf(wayp);
			if(f>=u.size()-1)
			{
				map.bugs.remove(this);
				map.lives--;
			}
			else wayp=u.get(f+1);
		}
		if(rtime>0)
		{
			rtime-=dt;
			if(rdcdtime<=0)
			{
				rdcdtime=rdtime;
				hp-=rdmg;
			}
			else rdcdtime-=dt;
		}
	}
	@Override
	public void onDraw(Canvas c)
	{
		// TODO: Implement this method
		super.onDraw(c);
		p.setColor(0xff000000);
		if(MainActivity.map!=null)
		{
			Map map=MainActivity.map;
			float tilew=map.tilew;

			c.drawBitmap(bugicon,(int)(x*tilew),(int)(y*tilew),p);
		}
		else c.drawBitmap(bugicon,(int)(x-bugicon.getWidth()/2),(int)(y-bugicon.getHeight()/2),p);
		
	}

}
