package com.yzrilyzr.bugs;
import android.graphics.*;
import com.yzrilyzr.engine2d.*;

public class StartAnim extends Scene
{
	Timer t;
	@Override
	public void render(Canvas c, float dt)
	{
		super.render(c,dt);
		if(t.render(dt)==4300){
			Eg.startScene(new Background());
			Eg.startScene(new MainMenu(true));
			removeSelf();
		}
	}
	@Override
	public void start()
	{
		t=new Timer(4300);
		Eg.setBackground(0xff666666);
		add(new Ui("vec/startanim/loadcode",100,Ui.G.L,0,0,null)
			.alpha(2300,500,100,0)
			.delayDismiss(2800));
		Ui bug=new Ui("bugs/0",30,Ui.G.L|Ui.G.T,60,55,null)
			.alpha(500,300,0,100)
			.scale(500,300,50,100)
			.rotate(500,0,0,Eg.random(0,360))
			.delayDismiss(0)
			.delayShow(500)
			.delayDismiss(1200);
		add(bug);
		add(
			new Ui("vec/startanim/loadpp",40,Ui.G.C,0,0,bug)
			.alpha(1150,100,80,100)
			.scale(1150,100,20,100)
			.delayDismiss(0)
			.delayShow(1150)
			.alpha(2300,500,100,0)
			.delayDismiss(2800),
			new Ui("vec/startanim/loadp",60,Ui.G.L|Ui.G.T,0,0,bug)
			.translate(1000,200,100,100,0,0)
			.scale(1000,200,0,100)
			.delayDismiss(0)
			.delayShow(1000)
			.alpha(2300,500,100,0)
			.delayDismiss(2800)

		);
		for(int i=0;i<20;i++)
		{
			add(new Ui("bugs/"+i,Eg.random(15,30),Ui.G.C,Eg.random(-50,50),Eg.random(-50,50),null)
				.alpha(1300+i*50,50,0,100)
				.scale(1300+i*50,50,50,100)
				.rotate(1300+i*50,0,0,Eg.random(0,360))
				.delayDismiss(0)
				.delayShow(1300+i*50)
				.alpha(2300,500,100,0)
				.delayDismiss(2800));
		}
		add(new Ui("vec/mainmenu/yzr",100,Ui.G.C,0,0,null)
			.alpha(2300,500,0,100)
			.scale(3300,1000,100,100,70,70,0,0,null,null)
			.translate(3300,1000,Ui.G.C,0,0,100,100,null,Ui.G.L|Ui.G.B,6,-2,70,70,null)
			.delayDismiss(0)
			.delayShow(2300)
			.delayDismiss(4300),
			new Ui("vec/mainmenu/yzr",70,Ui.G.L|Ui.G.B,6,-2,null)
			.delayDismiss(0)
			.delayShow(4300)
			.delayDismiss(4500)
		);
		
	}

	@Override
	public void stop()
	{
		
	}

}
