package com.yzrilyzr.bugs;
import android.graphics.*;
import android.view.*;
import com.yzrilyzr.bugs.*;
import com.yzrilyzr.engine2d.*;
import com.yzrilyzr.icondesigner.*;
import java.util.*;

import com.yzrilyzr.bugs.MainActivity;
import com.yzrilyzr.engine2d.Shape;
import com.yzrilyzr.engine2d.Timer;

public class Background extends Scene
{
	Random r=new Random();
	@Override
	public void render(Canvas c, float dt)
	{
		while(uis.size()<20)
			uis.add(new mBug(r.nextInt(20),
					r.nextBoolean()?Eg.p(-20):Eg.getAbsWidth(),
					r.nextBoolean()?Eg.p(-20):Eg.getAbsHeight(),
					10+r.nextInt(10)));
		for(Ui pp:uis)
		{
			mBug b=(mBug)pp;
			b.compute(dt);
			if(b.t==null){
				Eg.p.setAlpha(255);
				b.ma.reset();
				b.ma.postRotate((float)(180f*Eg.getArc(b.vx,b.vy,b.vel)/Math.PI)+90,b.r,b.r);
				b.ma.postTranslate(b.x,b.y);
				c.drawBitmap(b.bug,b.ma,Eg.p);
			}else
				switch(b.t.render(dt))
				{
					case 0:
						b.ma.reset();
						b.ma.postScale(b.t.getNLF(),b.t.getNLF(),b.r,b.r);
						Eg.p.setAlpha((int)(255*b.t.getNLF()));
						b.ma.postTranslate(b.x,b.y);
						c.drawBitmap(b.dbug,b.ma,Eg.p);
						break;
					case 200:
						Eg.p.setAlpha(255);
						c.drawBitmap(b.dbug,b.x,b.y,Eg.p);
						break;
					case 1500:
						Eg.p.setAlpha((int)(255-255*b.t.getNLF()));
						c.drawBitmap(b.dbug,b.x,b.y,Eg.p);
						break;
					case 2000:
						uis.remove(b);
						break;
				}
			//Eg.drawVec(c,b.vec,b.gravity,b.si,b.dx,b.dy,b.rect,0,0,0,0,1,0,0,(float)(Eg.getArc(b.vx,b.vy,Math.sqrt(b.vx*b.vx+b.vy*b.vy))/Math.PI),50,50,1);
		}
	}

	@Override
	public void start()
	{
		// TODO: Implement this method
	}

	@Override
	public void stop()
	{
		// TODO: Implement this method
	}
	class mBug extends Ui
	{
		float vx,vy,ax,ay,x,y,vel=Eg.p(0.1f),r;
		int id;
		Matrix ma=new Matrix();
		Timer t;
		Random rd=new Random();
		Bitmap bug,dbug;
		public mBug(int id,float x,float y,float size)
		{
			super(x,y,size,size);
			try
			{
				int s=Eg.pi(size);
				bug=VECfile.createBitmap(Eg.gameact,"bugs/"+id,s,s);
				this.x=x;
				this.y=y;
				this.r=s/2f;
				try
				{
					dbug=VECfile.createBitmap(Eg.gameact,"bugs/d"+id,s,s);
				}
				catch (Exception e)
				{
					try
					{
						dbug=VECfile.createBitmap(Eg.gameact,"bugs/d0",s,s);
					}
					catch(Throwable pe)
					{}
				}

			}
			catch(Throwable e)
			{
			}
			this.x=x;
			this.y=y;
		}

		@Override
		public void onClick(MotionEvent e)
		{
			if(t==null)
			{
				//anim=true;
				t=new Timer(0,200,1500,2000);
			}
		}

		public void compute(float dt)
		{
			if(t==null)
			{
				vx=Eg.limit(vx+ax*dt,-vel,vel);
				vy=Eg.limit(vy+ay*dt,-vel,vel);
				x+=vx*dt;
				y+=vy*dt;
				float ro=this.r*2.5f;
				if(x<-ro)x=Eg.getAbsWidth()+ro;
				if(y<-ro)y=Eg.getAbsHeight()+ro;
				if(x>Eg.getAbsWidth()+ro)x=-ro;
				if(y>Eg.getAbsHeight()+ro)y=-ro;
				ax=(rd.nextBoolean()?1:-1)*Eg.p(0.00005f);
				ay=(rd.nextBoolean()?1:-1)*Eg.p(0.00005f);
				//dx=x*100/Eg.getAbsWidth();
				//dy=y*100/Eg.getAbsHeight();
				rect.set(x,y,x+2*r,y+2*r);
			}
			
		}
	}
}
