package com.yzrilyzr.bugs;

import android.content.pm.*;
import android.os.*;
import android.view.*;
import com.yzrilyzr.bugs.Game.*;
import com.yzrilyzr.bugs.Scenes.*;
import com.yzrilyzr.game.*;
import java.io.*;

public class MainActivity extends com.yzrilyzr.game.MainActivity 
{
	SoundPlay pl;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		Utils.setContext(this);
        Utils.setMainDir(Environment.getExternalStorageDirectory().getAbsolutePath()+"/yzr的app/Bugs/");
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		Data.load();
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					try
					{
						Thread.sleep(500);
					Utils.loadScene(new LevelSelect("splash"));
						/*Thread.sleep(500);
						pl=new SoundPlay();
						 int bpm=100;
						 byte[] y=pl.getMusic2(new int[][]{
						 pl.parse(getResources().getText(R.string.bgm).toString(),bpm),
						 pl.parse(getResources().getText(R.string.bgmtrk1).toString(),bpm),
						 pl.parse(getResources().getText(R.string.bgmtrk2).toString(),bpm)
						 },new float[]{
						 0.3f,0.35f,0.35f,
						 0.0f,0.5f,0.5f,
						 0.2f,0.4f,0.4f
						 },new float[]{1,0.5f,1},new int[]{12,0,0});

						 pl.write(y);*/
					}
					catch (Exception e)
					{
						Utils.alert(e);
					}
				}
			}).start();
    }

	@Override
	protected void onPause()
	{
		// TODO: Implement this method
		if(pl!=null)pl.pause();
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		if(pl!=null)pl.play();
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		File[] f=new File(Utils.mainDir+"/GUI").listFiles();
		menu.add(0,0,0,"重载");
		menu.add(0,1,1,"屏幕旋转切换");
		for (int i=2; i<f.length+2; i++)
		{
			menu.add(0, i, i, f[i-2].getName());
		}
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getOrder()==0)
		{
			Utils.unloadAll();
			Utils.loadScene(new Splash("splash"));
		}
		else if(item.getOrder()==1)
		{
			if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		else
		{
			try
			{
				File[] f=new File(Utils.mainDir+"/GUI").listFiles();
				Utils.ctx.scenes.get(scenes.size()-1).uis.clear();
				Utils.ctx.scenes.get(scenes.size()-1).loadGUI(Utils.readTxt(f[item.getOrder()-2].getPath()));
			}
			catch(Throwable e)
			{
				Utils.alert(e);
			}
		}
		return super.onOptionsItemSelected(item);
    }
}
