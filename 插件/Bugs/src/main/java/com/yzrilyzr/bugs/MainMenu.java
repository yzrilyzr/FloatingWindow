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
	Ui yzr,play,info,settings;
	Ui title;
	boolean from;
	public MainMenu(boolean fromAnim)
	{
		this.from=fromAnim;
	}
	@Override
	public void render(Canvas c, float dt)
	{
		/*if(in!=null)
	
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
		 }*/
		super.render(c,dt);
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
		add(yzr=new Button("vec/mainmenu/yzr",70,Eg.Gravity.BOTTOM|Eg.Gravity.LEFT,6,-2,null,true)
			{
				public void onClick(MotionEvent e)
				{
					if(tip==null)
					{
						ud=uh[new Random().nextInt(uh.length)].split("\\\\n");
						tip=new Timer(0,300,1700,2000);
					}
				}
			},
			title=new Ui("vec/mainmenu/title",35,Ui.G.C|Ui.G.T,0,0,null)
			.alpha(0,300,0,100)
			.scale(0,300,110,100,100,100,50,50,null,null),
			play=new Button("vec/mainmenu/play",45,Eg.Gravity.RIGHT|Eg.Gravity.BOTTOM,-20,-15,null,true){
				public void onClick(MotionEvent e)
				{
					in=null;
					if(tip!=null)tip.timer=Eg.limit(tip.timer,1500,2000);
					out=new Timer(0,500);
					Eg.startScene(new LevelSelect());
				}
			}
			.alpha(300,600,0,100)
			.delayDismiss(0)
			.delayShow(300)
			.scale(300,600,0,100),
			settings=new Button("vec/mainmenu/settings",15,Eg.Gravity.RIGHT|Eg.Gravity.BOTTOM,-3,-3,null,true){
				public void onClick(MotionEvent e)
				{
					Eg.startScene(new Settings());
				}
			}
			.delayDismiss(0)
			.delayShow(500)
			.alpha(500,800,0,100)
			.scale(500,800,0,100)
			.translate(500,800,100,0,0,0),
			info=new Button("vec/mainmenu/info",15,Eg.Gravity.RIGHT|Eg.Gravity.BOTTOM,-3,-21,null,true){
				public void onClick(MotionEvent e)
				{
					Eg.startScene(new About());
				}
			}
			.delayDismiss(0)
			.delayShow(500)
			.alpha(500,800,0,100)
			.scale(500,800,0,100)
			.translate(500,800,100,0,0,0)

		);
		if(!from)yzr.alpha(0,500,0,100).scale(0,500,80,100);
		//add(yzr,play,settings,info,title);
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
