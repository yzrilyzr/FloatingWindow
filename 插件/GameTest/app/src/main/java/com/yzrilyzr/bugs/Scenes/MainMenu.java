package com.yzrilyzr.bugs.Scenes;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import com.yzrilyzr.game.*;
import java.io.*;
import java.util.*;

import com.yzrilyzr.game.Timer;

public class MainMenu extends Scene
{
	float tip=-1;
	String ud[],uh[];
	Ui tipui;
	public MainMenu(String id)
	{
		super(id);
		loadGUI(Utils.readTxt((Utils.mainDir+"GUI/mainmenu.txt")));
		if(id.equals("mainmenul")){
			removeGUI("back");
			setBackgroundColor(0xff40bbff);
		}
		else{
			findUi("yzr").anim.clear();
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
		if(findUi("uiaboutok")==null)loadGUI(Utils.readTxt((Utils.mainDir+"GUI/info.txt")));
		else
		{
			int p=uis.indexOf(findUi("uiaboutok"));
			for(int i=p;i>=p-2;i--)
				uis.get(i).reverseAnim();
			
		}
	}
	public void uiaboutok(Ui s)
	{
		int p=uis.indexOf(s);
		for(int i=p;i>=p-2;i--)
		uis.get(i).reverseAnim();
	}
	public void yzr(Ui s)
	{
		if(findUi("tip")==null)
		{
			ud=uh[new Random().nextInt(uh.length)].split("\\\\n");
			loadGUI(Utils.readTxt((Utils.mainDir+"GUI/mainmenutip.txt")));
			tipui=findUi("tip");
			tip=0;
		}
	}
	public void play(Ui s){
		Utils.loadScene(new LevelSelect("levelselect"));
		exitAnim();
	}
	public void settings(Ui s){
		
	}
	@Override
	public void onDraw(Canvas c)
	{
		// TODO: Implement this method
		super.onDraw(c);
		if(tip!=-1)tip+=Utils.dt/1000000f;
		if(tip>300&&tip<2000){
			p.setColor(0xff000000);
			p.setStyle(Paint.Style.FILL);
			p.setTextSize(Utils.px(11));
			for(int i=0;i<ud.length;i++)
				c.drawText(ud[i],tipui.rectf.left+Utils.px(10),tipui.rectf.top+(i+2)*Utils.px(12),p);
		}
		else if(tip>2300)
		{
			tip=-1;
			removeGUI("tip");
		}
	}

}
