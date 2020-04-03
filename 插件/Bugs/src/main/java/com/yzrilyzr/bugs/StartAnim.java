package com.yzrilyzr.bugs;
import android.graphics.*;
import com.yzrilyzr.engine2d.*;

public class StartAnim extends Scene
{
	Timer t;
	int[] rd=new int[80];
	@Override
	public void render(Canvas c, float dt)
	{
		switch(t.render(dt))
		{
			case 0:
				Eg.drawVec(c,"vec/startanim/loadcode",Eg.Gravity.LEFT,100);
				float t1=Eg.getNLinearValueByTime(t.timer,500,1000);
				Eg.drawVec(c,"bugs/0",0,30,60,55,0,0,t1,50,50,0,0,0,t1);
				float t3=Eg.getNLinearValueByTime(t.timer,1150,1300);
				Eg.drawVec(c,"vec/startanim/loadpp",0,30,60,55,0,0,t3,50,50,0,0,0,t3);
				float t2=Eg.getNLinearValueByTime(t.timer,1000,1200);
				Eg.drawVec(c,"vec/startanim/loadp",0,60,60,55,1-t2,1-t2,t2,100,100,0,0,0,1);
				for(int i=0;i<20;i++)
				{
					float t35=Eg.getNLinearValueByTime(t.timer,1400+i*50,1450+i*50);
					Eg.drawVec(c,"bugs/"+i,Eg.Gravity.CENTER,rd[i],rd[20+i],rd[40+i],0,0,t35,50,50,rd[60+i]/100f,50,50,t35);
				}
				break;
			case 2500:
				float t11=1-Eg.getNLinearValueByTime(t.timer,2500,2900);
				Eg.drawVec(c,"vec/startanim/loadcode",Eg.Gravity.LEFT,100,0,0,0,0,1,0,0,0,0,0,t11);
				Eg.drawVec(c,"vec/startanim/loadpp",0,30,60,55,0,0,1,0,0,0,0,0,t11);
				Eg.drawVec(c,"vec/startanim/loadp",0,60,60,55,0,0,1,0,0,0,0,0,t11);
				float t4=Eg.getNLinearValueByTime(t.timer,2700,3500);
				for(int i=0;i<20;i++)
				{
					Eg.drawVec(c,"bugs/"+i,Eg.Gravity.CENTER,rd[i],rd[20+i],rd[40+i],0,0,1,0,0,rd[60+i]/100f,50,50,t11);
				}
				Eg.drawVec(c,"vec/mainmenu/mainmenuyzr",Eg.Gravity.CENTER,100,0,0,0,0,1,0,0,0,0,0,t4);
				break;
			case 3500:
				float t43=Eg.getNLinearValueByTime(t.timer,3500,4200);
				Eg.drawVec(c,"vec/mainmenu/mainmenuyzr",Eg.Gravity.CENTER,100,0,0,null,null,6,-2,t43,Eg.Gravity.LEFT|Eg.Gravity.BOTTOM,1f-t43*0.3f,0,100,0,0,0,1);
				break;
			case 4200:
				Eg.drawVec(c,"vec/mainmenu/mainmenuyzr",Eg.Gravity.CENTER,100,0,0,null,null,6,-2,1,Eg.Gravity.LEFT|Eg.Gravity.BOTTOM,0.7f,0,100,0,0,0,1);
				Eg.startScene(new Background());
				Eg.startScene(new MainMenu(true));
				removeSelf();
				break;
		}
	}
	@Override
	public void start()
	{
		t=new Timer(0,2500,3500,4200);
		Eg.setBackground(0xff666666);
		for(int i=0;i<20;i++)rd[i]=Eg.random(15,30);
		for(int i=20;i<40;i++)rd[i]=Eg.random(-50,50);
		for(int i=40;i<60;i++)rd[i]=Eg.random(-50,50);
		for(int i=60;i<80;i++)rd[i]=Eg.random(0,100);
	}

	@Override
	public void stop()
	{
		Eg.cache.clear();
	}

}
