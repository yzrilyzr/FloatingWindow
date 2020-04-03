package com.yzrilyzr.bugs;
import android.graphics.*;
import android.view.*;
import com.yzrilyzr.engine2d.*;
import java.io.*;
import java.util.*;

import com.yzrilyzr.engine2d.Timer;

public class MainMenu extends Scene
{
	Timer in,out,tip;
	String[] uh,ud;
	RectF tipm;
	Button yzr;
	Button play,info,settings;
	boolean from;
	public MainMenu(boolean fromAnim)
	{
		this.from=fromAnim;
	}
	@Override
	public void render(Canvas c, float dt)
	{
		if(in!=null)
			switch(in.render(dt))
			{
				case 0:
					anim(true);
					Eg.drawVec(c,"vec/mainmenu/mainmenutitle",Eg.Gravity.CENTER|Eg.Gravity.TOP,35,0,0,0,0,1.3f-in.getNLF()*0.3f,50,50,0,0,0,in.getNLF());
					if(from)yzr.draw(c);
					else yzr.draw(c,0,0,0.7f+in.getNLF()*0.3f,50,50,0,0,0,in.getNLF());
					play.draw(c,0,0,0.5f*(1f+in.getNLF()),50,50,0,0,0,in.getNLF());
					settings.draw(c,18*(1-in.getNLF()),0,0.5f*(1f+in.getNLF()),50,50,0,0,0,in.getNLF());
					info.draw(c,18*(1-in.getNLF()),0,0.5f*(1f+in.getNLF()),50,50,0,0,0,in.getNLF());
					break;
				case 500:
					anim(false);
					Eg.drawVec(c,"vec/mainmenu/mainmenutitle",Eg.Gravity.CENTER|Eg.Gravity.TOP,35);
					yzr.draw(c);
					play.draw(c);
					settings.draw(c);
					info.draw(c);
					break;
			}
		else if(out!=null)
			switch(out.render(dt))
			{
				case 0:
					anim(true);
					Eg.drawVec(c,"vec/mainmenu/mainmenutitle",Eg.Gravity.CENTER|Eg.Gravity.TOP,35,0,0,0,-40*out.getNLF(),1f-out.getNLF()*0.5f,50,50,0,0,0,1-out.getNLF());
					play.draw(c,0,0,1-out.getNLF()+out.getSF()*0.3f,50,50,0,0,0,1-out.getNLF());
					yzr.draw(c,0,0,1-0.3f*out.getNLF(),50,50,0,0,0,1-out.getNLF());
					settings.draw(c,18*out.getNLF(),0,0.5f*(2f-out.getNLF()),50,50,0,0,0,out.getNLF());
					info.draw(c,18*out.getNLF(),0,0.5f*(2f-out.getNLF()),50,50,0,0,0,1-out.getNLF());
					break;
				case 500:
					out=null;
					removeSelf();
					return;
			}
		//提示语
		if(tip!=null)
			switch(tip.render(dt))
			{
				case 0:
					Eg.drawVec(c,"vec/mainmenu/mainmenutip",Eg.Gravity.LEFT|Eg.Gravity.BOTTOM,30,5,-60,0,0,tip.getNLF(),75,95,0,0,0,1);
					break;
				case 300:
					Eg.drawVec(c,"vec/mainmenu/mainmenutip",Eg.Gravity.LEFT|Eg.Gravity.BOTTOM,30,5,-60,tipm,null,0,0,0,0,1,0,0,0,0,0,1);
					Eg.p.setColor(0xff000000);
					Eg.p.setStyle(Paint.Style.FILL);
					Eg.p.setTextSize(Eg.p(3));
					for(int i=0;i<ud.length;i++)
						c.drawText(ud[i],tipm.left+Eg.p(3),tipm.top+(i+1)*Eg.p(5),Eg.p);
					break;
				case 1700:
					Eg.drawVec(c,"vec/mainmenu/mainmenutip",Eg.Gravity.LEFT|Eg.Gravity.BOTTOM,30,5,-60,0,0,1-tip.getNLF(),75,95,0,0,0,1);
					break;
				case 2000:
					tip=null;
					break;
			}
	}

	@Override
	public void start()
	{
		//提示语
		StringBuilder sb=new StringBuilder();
		try
		{
			String g=null;
			BufferedReader br=new BufferedReader(new InputStreamReader(Eg.gameact.getAssets().open("tips.txt")));
			while((g=br.readLine())!=null)sb.append(g).append("\n");
			br.close();
		}
		catch(Throwable pe)
		{
		}
		tipm=new RectF();
		uh=sb.toString().split("\n");	
		//界面布局
		yzr=new Button("vec/mainmenu/mainmenuyzr",Eg.Gravity.BOTTOM|Eg.Gravity.LEFT,70,6,-2)
		{
			public void onClick(MotionEvent e)
			{
				if(tip==null)
				{
					ud=uh[new Random().nextInt(uh.length)].split("\\\\n");
					tip=new Timer(0,300,1700,2000);
				}
			}
		};
		play=new Button("vec/mainmenu/mainmenuplay",Eg.Gravity.RIGHT|Eg.Gravity.BOTTOM,45,-20,-15,true){
			public void onClick(MotionEvent e)
			{
				in=null;
				if(tip!=null)tip.timer=Eg.limit(tip.timer,1500,2000);
				out=new Timer(0,500);
				Eg.startScene(new LevelSelect());
			}
		};
		settings=new Button("vec/mainmenu/settings",Eg.Gravity.RIGHT|Eg.Gravity.BOTTOM,15,-3,-3,true){
			public void onClick(MotionEvent e)
			{
				Eg.startScene(new Settings());
			}
		};
		info=new Button("vec/mainmenu/info",Eg.Gravity.RIGHT|Eg.Gravity.BOTTOM,15,-3,-21,true){
			public void onClick(MotionEvent e)
			{
				Eg.startScene(new About());
			}
		};
				add(yzr,play,settings,info);
		//-15,-10
		//播放动画
		in=new Timer(0,500);
	}

	@Override
	public void stop()
	{
		// TODO: Implement this method
	}
}
