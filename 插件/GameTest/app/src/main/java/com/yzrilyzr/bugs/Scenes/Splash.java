package com.yzrilyzr.bugs.Scenes;
import android.graphics.*;
import com.yzrilyzr.game.*;
import com.yzrilyzr.icondesigner.*;
import java.util.concurrent.*;
import com.yzrilyzr.bugs.Game.*;

public class Splash extends Scene
{
	//public CopyOnWriteArrayList<PointF> l=new CopyOnWriteArrayList<PointF>();
	boolean b=true,b2=true,b3=true,b4=true,b5=true;
	public Splash(String id)
	{
		super(id);
		loadGUIPath("GUI/splash/splash.txt");
		setBackgroundColor(0xff000000);
	}

	@Override
	public void onDraw(Canvas c)
	{
		// TODO: Implement this method
		super.onDraw(c);
		if(time>2000&&b)
		{
			b=!b;
			clearGUI();
			CopyOnWriteArrayList<VECfile.TypefaceMap> l=new CopyOnWriteArrayList<VECfile.TypefaceMap>();
			l.addAll(VECfile.typefaceMap);
			VECfile.typefaceMap.clear();
			loadGUIPath("GUI/splash/intro.txt");
			VECfile.typefaceMap.addAll(l);
			setBackgroundColor(0xff333333);
		}
		if(time>3600&&b2)
		{
			b2=!b2;
			for(int i=0;i<20;i++)loadGUIPath("GUI/splash/intro_bugs.txt");
		}
		if(time>4500&&b3)
		{
			b3=!b3;
			for(Ui u:uis)
			{
				AlphaAnim a=new AlphaAnim(u.p);
				a.fromto=new float[]{100,0};
				a.duration=500;
				u.anim.add(a);
			}
			loadGUIPath("GUI/splash/intro_yzrilyzr.txt");
		}
		if(time>5000&&b5)
		{
			b5=!b5;
			for(Ui u:uis)
			{
				if(u.id.contains("bug"))uis.remove(u);
			}
		}
		if(time>5800&&b4)
		{
			b4=!b4;
			Utils.unloadScene("splash");
			Utils.loadScene(new MainMenu("mainmenu"));
		}
	}
}
