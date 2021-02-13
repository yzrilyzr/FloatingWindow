package com.yzrilyzr.bugs.Scenes;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import com.yzrilyzr.game.*;
import java.io.*;
import java.util.*;

import com.yzrilyzr.game.Timer;
import com.yzrilyzr.icondesigner.*;
import java.util.concurrent.*;

public class MainMenu extends Scene
{
	float tip=-1000,info=-1000;
	String ud[],uh[];
	public MainMenu(String id)
	{
		super(id);
		loadGUI(Utils.readTxt((Utils.mainDir+"GUI/mainmenu.txt")));
		if(id.equals("mainmenul"))
		{
			removeGUI("back");
			setBackgroundColor(0xff40bbff);
		}
		else
		{
			Ui y=findUi("yzr");
			y.anim.clear();
			BaseAnim b=new BaseAnim();
			b.duration=500;
			y.anim.add(b);
			setBackgroundColor(0xff333333);
		}
		StringBuilder sb=new StringBuilder();
		try
		{
			String g=null;
			BufferedReader br=new BufferedReader(new InputStreamReader(Utils.ctx.getAssets().open("tips.txt")));
			while((g=br.readLine())!=null)sb.append(g).append("\n");
			br.close();
		}
		catch(Throwable pe)
		{
		}
		uh=sb.toString().split("\n");	
	}
	public void info(Ui s)
	{
		Utils.loadScene(new Info("info"));
	}
	
	public void title(Ui s)
	{
		float[] p=new float[]{100,110,90,105,95,100f/1.10f/0.90f/1.05f/0.95f};
		for(int i=0;i<5;i++)
		{
			ScaleAnim sc=new ScaleAnim(s.matrix);
			sc.cx=s.width/2;
			sc.cy=s.height/2;
			sc.duration=100;
			sc.delay=i*100;
			sc.fromto=new float[]{p[i],p[i],p[i+1],p[i+1]};
			s.anim.add(sc);
		}
	}
	public void yzr(Ui s)
	{
		if(findUi("tip")==null)
		{
			try
			{
				ud=uh[new Random().nextInt(uh.length)].split("\\\\n");
				loadGUI(Utils.readTxt((Utils.mainDir+"GUI/mainmenutip.txt")));
				Ui tipui=findUi("tip");
				VECfile v=VECfile.readFileFromIs(Utils.ctx.getAssets().open("vec/mainmenu/tip.vec"));
				CopyOnWriteArrayList<Shape> hs=v.getShapes();
				for(int i=2;i<6;i++)
				{
					if(i-2<ud.length)hs.get(i).txt=ud[i-2];
					else hs.get(i).txt="";
				}
				tipui.vec=v;
				tipui.bmp=VECfile.createBitmap(v,(int)tipui.width,(int)tipui.height);
				tip=2500;
			}
			catch(Throwable e)
			{
				Utils.alert(e);
			}
		}
	}
	public void play(Ui s)
	{
		Utils.loadScene(new LevelSelect("levelselect"));
		exitAnim();
	}
	public void setting(Ui s)
	{
	Utils.loadScene(new Settings("settingsmainmenu"));
	}
	@Override
	public void onDraw(Canvas c)
	{
		// TODO: Implement this method
		super.onDraw(c);
		if(tip>0)tip-=Utils.getDtMs();
		else if(tip!=-1000)
		{
			tip=-1000;
			removeGUI("tip");
		}
		if(info>0)info-=Utils.getDtMs();
		else if(info!=-1000)
		{
			info=-1000;
			removeGUI("backinfo");
			removeGUI("uiabout");
			removeGUI("uiaboutok");
		}
	}

}
