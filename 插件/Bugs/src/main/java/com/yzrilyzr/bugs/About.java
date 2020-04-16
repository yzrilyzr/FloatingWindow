package com.yzrilyzr.bugs;

import com.yzrilyzr.engine2d.*;
import android.graphics.*;
import android.view.*;

public class About extends Scene
{
	Timer in,out;
	Ui ok,abo;
	Ui mask;
	@Override
	public void render(Canvas c, float dt)
	{
		if(in!=null)
			switch(in.render(dt))
			{
				case 0:
					Eg.p.setColor(0xff000000);
					Eg.p.setAlpha((int)(in.getNLF()*128));
					c.drawPaint(Eg.p);
					break;
				case 500:
					Eg.p.setColor(0xff000000);
					Eg.p.setAlpha(128);
					c.drawPaint(Eg.p);
					break;
			}
		else if(out!=null)
			switch(out.render(dt))
			{
				case 0:
					Eg.p.setColor(0xff000000);
					Eg.p.setAlpha((int)(128-out.getNLF()*128));
					c.drawPaint(Eg.p);
					break;
				case 500:
					removeSelf();
					break;
			}
		super.render(c,dt);
	}

	@Override
	public void start()
	{
		in=new Timer(0,500);
		mask=new Ui(0,0,100,100);
		abo=new Ui("vec/mainmenu/uiabout",90,Ui.G.C,0,0,null)
		.alpha(0,500,0,100)
		.scale(0,500,0,100)//,0,100,0,100,0,0,null,null)
		.translate(0,500,Ui.G.R|Ui.G.B,-3,-21,10,15,null,Ui.G.C,0,0,100,100,null);
		ok=new Button("vec/mainmenu/uiaboutok",10,Ui.G.C|Ui.G.B,0,-5,abo){
			public void onClick(MotionEvent e)
			{
				//this.anim=true;
				in=null;
				out=new Timer(0,500);
				stop();
			}
		}
		.scale(0,500,0,100)
		.alpha(0,500,0,100)
		.translate(0,500,0,0,0,0);
		add(mask,abo,ok);
	}

	@Override
	public void stop()
	{
		abo.alpha(0,500,100,0)
			.scale(0,500,100,0)//,0,100,0,100,0,0,null,null)
			.translate(0,500,Ui.G.C,0,0,100,100,null,Ui.G.R|Ui.G.B,-3,-21,10,15,null);
		ok.scale(0,500,100,0)
			.alpha(0,500,100,0)
			.translate(0,500,0,0,0,0);
		
	}


}
