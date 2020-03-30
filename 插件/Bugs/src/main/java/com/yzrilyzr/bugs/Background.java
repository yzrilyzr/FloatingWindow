package com.yzrilyzr.bugs;
import android.graphics.*;
import android.view.*;
import com.yzrilyzr.engine2d.*;
import java.util.*;

import com.yzrilyzr.engine2d.Timer;

public class Background extends Scene
{
	Random r=new Random();
	@Override
	public void render(Canvas c, float dt)
	{
		while(uis.size()<20)
			uis.add(new mBug(r.nextInt(20),
					r.nextBoolean()?0:Eg.getAbsWidth(),
					r.nextBoolean()?0:Eg.getAbsHeight(),
					5+r.nextInt(15)));
		for(Ui pp:uis){
			mBug b=(mBug)pp;
			b.compute(dt);
			Eg.drawVec(c,b.vec,b.gravity,b.si,b.dx,b.dy,b.rect,0,0,0,0,1,0,0,(float)(Eg.getArc(b.vx,b.vy,Math.sqrt(b.vx*b.vx+b.vy*b.vy))/Math.PI),50,50,1);
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
	class mBug extends Button{
		float vx,vy,ax,ay,x,y,vel=Eg.p(0.05f);
		int id;
		Matrix ma=new Matrix();
		Timer t;
		Random rd=new Random();
		public mBug(int id,float x,float y,float size){
			super("bugs/"+id,Eg.Gravity.CENTER,size,0,0,true);
			this.x=x;
			this.y=y;
		}

		@Override
		public void onClick(MotionEvent e)
		{
			anim=true;
			vec="bugs/d"+0;
			t=new Timer(0,300,2000);
		}
		
		public void compute(float dt){
			if(t==null){
				vx=Eg.limit(vx+ax*dt,-vel,vel);
				vy=Eg.limit(vy+ay*dt,-vel,vel);
				x+=vx*dt;
				y+=vy*dt;
				float ro=this.r*2;
				if(x<-ro)x=Eg.getAbsWidth()+ro;
				if(y<-ro)y=Eg.getAbsHeight()+ro;
				if(x>Eg.getAbsWidth()+ro)x=-ro;
				if(y>Eg.getAbsHeight()+ro)y=-ro;
				ax=(rd.nextBoolean()?1:-1)*Eg.p(0.00001f);
				ay=(rd.nextBoolean()?1:-1)*Eg.p(0.00001f);
				dx=x*100/Eg.getAbsWidth();
				dy=y*100/Eg.getAbsHeight();
			}
			
			}
	}
}
