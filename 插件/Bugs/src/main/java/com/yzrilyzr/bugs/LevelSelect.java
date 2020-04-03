package com.yzrilyzr.bugs;
import com.yzrilyzr.engine2d.*;
import android.graphics.*;
import android.view.*;

public class LevelSelect extends Scene
{
	Button mainmenu,bugs,help,achi;
	Timer in,out;
	@Override
	public void render(Canvas c, float dt)
	{
		if(in!=null)
			switch(in.render(dt)){
				case 0:
					anim(true);
					mainmenu.draw(c,0,13-13*in.getNLF(),0.7f+0.3f*in.getNLF(),50,50,0,0,0,in.getNLF());
					bugs.draw(c,0,18*(1-in.getNLF()),0.5f*(1f+in.getNLF()),50,50,0,0,0,in.getNLF());
					help.draw(c,0,18*(1-in.getNLF()),0.5f*(1f+in.getNLF()),50,50,0,0,0,in.getNLF());
					achi.draw(c,0,18*(1-in.getNLF()),0.5f*(1f+in.getNLF()),50,50,0,0,0,in.getNLF());
					
					break;
				case 500:
					anim(false);
					bugs.draw(c);
					help.draw(c);
					achi.draw(c);
					mainmenu.draw(c);
			}
		c.drawText("cc",500,500,Eg.p);
		if(out!=null)
			switch(out.render(dt)){
				case 0:
					anim(true);
					mainmenu.draw(c,0,13*out.getNLF(),1f-0.3f*out.getNLF(),50,50,0,0,0,1-out.getNLF());
					bugs.draw(c,0,18*out.getNLF(),0.5f*(2f-out.getNLF()),50,50,0,0,0,1-out.getNLF());
					help.draw(c,0,18*out.getNLF(),0.5f*(2f-out.getNLF()),50,50,0,0,0,1-out.getNLF());
					achi.draw(c,0,18*out.getNLF(),0.5f*(2f-out.getNLF()),50,50,0,0,0,1-out.getNLF());
					break;
				case 500:
					out=null;
					removeSelf();
			}
	}

	@Override
	public void start()
	{
		in=new Timer(0,500);
		mainmenu=new Button("buttonmainmenu",Eg.Gravity.RIGHT|Eg.Gravity.BOTTOM,10,-3,-3){
			public void onClick(MotionEvent e){
				in=null;
				out=new Timer(0,500);
				Eg.startScene(new MainMenu(false));
			}
		};
		bugs=new Button("vec/levelselect/bugs",Eg.Gravity.LEFT|Eg.Gravity.BOTTOM,15,3,-3,true){
			public void onClick(MotionEvent e)
			{

			}
		};
		help=new Button("vec/levelselect/help",Eg.Gravity.LEFT|Eg.Gravity.BOTTOM,15,21,-3,true){
			public void onClick(MotionEvent e)
			{

			}
		};
		achi=new Button("vec/levelselect/achi",Eg.Gravity.LEFT|Eg.Gravity.BOTTOM,15,39,-3,true){
			public void onClick(MotionEvent e)
			{

			}
		};
		add(mainmenu,bugs,help,achi);
	}

	@Override
	public void stop()
	{
		// TODO: Implement this method
	}
	
}
