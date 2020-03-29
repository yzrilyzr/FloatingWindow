package com.yzrilyzr.bugs;
import com.yzrilyzr.engine2d.*;
import android.graphics.*;

public class StartAnim extends Scene
{
	Timer t;
	@Override
	public void render(Canvas c, float dt)
	{
		switch(t.render(dt))
		{
			case 0:
				Eg.drawVec(c,"vec/startanim/loadcode",Eg.Gravity.CENTER,100);
				float t1=Eg.getNLinearValueByTime(t.timer,500,1000);
				Eg.drawVec(c,"bugs/0",Eg.Gravity.CENTER,20,20,20,
					0,0,
					t1,50,50,
					0,0,0,
					t1);
				float t3=Eg.getNLinearValueByTime(t.timer,1150,1300);
				Eg.drawVec(c,"vec/startanim/loadpp",Eg.Gravity.CENTER,30,20,20,
					0,0,
					t3,50,50,
					0,0,0,
					t3);
				float t2=Eg.getNLinearValueByTime(t.timer,1000,1200);
				Eg.drawVec(c,"vec/startanim/loadp",Eg.Gravity.CENTER,40,25,30,
					1-t2,1-t2,
					t2,100,100,
					0,0,0,
					1);
		}
		/*loadcode=new Ui("loadcode",0,0,1600,900);
		 Thread.sleep(500);
		 new Ui("bugs/0",1000,500,250,250).alphaFrom(0,100).tScFrom(1125,625,0,0,100);
		 Thread.sleep(500);
		 Ui u=new Ui("loadpp",900,400,400,400).setVisable(false);
		 new Ui("loadp",900,400,700,700).tScFrom(1600,900,0,0,50);
		 Thread.sleep(50);
		 u.tScFrom(1100,600,0,0,10).alphaFrom(0,10);
		 Thread.sleep(200);
		 Random r=new Random();
		 for(int i=0;i<20;i++)

		 {
		 int x=r.nextInt(1600),y=r.nextInt(900);
		 Ui o=new Ui("bugs/"+i,x,y,250,250,false).alphaFrom(0,20).tScFrom(x+125,y+125,0,0,20);
		 ui.add(3,o);
		 Thread.sleep(50);
		 }
		 Thread.sleep(200);
		 ui.clear();
		 Ui g=new Ui("mainmenuyzr",350,0,900,975).alphaFrom(0,500);
		 Thread.sleep(500);
		 g.tScTo(100,250,600,650,500);
		 Thread.sleep(500);
		 ui.clear();
		 mainmenu();*/
	}
	@Override
	public void start()
	{
		t=new Timer(0,4000,8800);
		Eg.setBackground(0xff000000);
	}

	@Override
	public void stop()
	{
		// TODO: Implement this method
	}

	@Override
	public void pause()
	{
		// TODO: Implement this method
	}

}
