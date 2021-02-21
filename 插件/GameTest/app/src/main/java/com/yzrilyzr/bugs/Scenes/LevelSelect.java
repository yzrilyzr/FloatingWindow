package com.yzrilyzr.bugs.Scenes;
import com.yzrilyzr.game.*;
import com.yzrilyzr.bugs.Game.*;
import com.yzrilyzr.icondesigner.*;
import android.graphics.*;

public class LevelSelect extends Scene
{
	public LevelSelect(String id)
	{
		super(id);
		setBackgroundColor(0xff40bbff);
		loadGUI(Utils.readTxt(Utils.mainDir+"GUI/levelselect.txt"),Data.unlockmap>15?"":"#");
		for(int i=0;i<20;i++)
		{
			loadGUI(Utils.readTxt(Utils.mainDir+"GUI/levelselectitem.txt"),
				Integer.toString(i),
				Integer.toString(130*(i%5)),
				Integer.toString(130*(i/5)),
				Integer.toString(Utils.random(300,800))
			);
			Ui s=findUi(String.format("item%d",i));
			VECfile v=s.getVec();
			int it=((i/5)*5)+Math.abs(i%5-5);
			if(it-1>Data.unlockmap)
			{
				v.getShapes().get(0).par[0]=0xff333333;
				v.getShapes().get(1).par[0]=0xff666666;
				v.getShapes().get(2).par[0]=0xff666666;
			}
			v.getShapes().get(1).txt=Integer.toString(it);
			v.getShapes().get(2).txt=String.format("分数:%s",Data.scores[i]);
			s.reDrawVecBmp();
		}
	}
	public void level(Ui s)
	{
		if(Integer.parseInt(s.getVec().getShapes().get(1).txt)-1<=Data.unlockmap){
			Utils.loadScene(new Load("load",s));
		exitAnim();
		}
		
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
	public void mainmenu(Ui s)
	{
		Utils.loadScene(new MainMenu("mainmenul"));
		exitAnim();
	}
	public void edit(Ui s)
	{

	}
	public void help(Ui s)
	{
		Utils.loadScene(new MainMenu("help"));
		exitAnim();
	}
	public void achi(Ui s)
	{
		Utils.loadScene(new Achievements("achievements"));
		exitAnim();
	}
	public void bugs(Ui s)
	{
		//Utils.loadScene(new GameMain("gamemain",Utils.readTxt("maps/map2")));
		exitAnim();
	}
	//载入界面
	private class Load extends Scene
	{
		//CopyOnWriteArrayList<Shape> hs,hs1,hs2;
		float dt;
		GameMain g;
		public Load(String id,final Ui s)
		{
			super(id);
			loadGUI(Utils.readTxt(Utils.mainDir+"GUI/gamemainload.txt"));
			new Thread(){
				public void run()
				{
					try
					{
						
						int it=Integer.parseInt(s.getVec().getShapes().get(1).txt)-1;
						g=new GameMain("gamemain",it,Utils.readTxt("maps/map"+it));
					}
					catch(Throwable e)
					{
						Utils.alert(e);
					}
				}
			}.start();
		}

		@Override
		public void onDraw(Canvas c)
		{
			// TODO: Implement this method
			super.onDraw(c);
			if(dt!=-1)dt+=Utils.getDtMs();
			if(dt>500)
			{
				Utils.unloadAll();
				Utils.loadScene(g);
				Utils.loadScene(this);
				//Utils.ctx.scenes.add(Utils.ctx.scenes.size()-2,g);
				dt=-1;
				exitAnim();
			}
		}

	}

}
