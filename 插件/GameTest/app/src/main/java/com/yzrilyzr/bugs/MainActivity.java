package com.yzrilyzr.bugs;

import android.content.pm.*;
import android.os.*;
import android.view.*;
import com.yzrilyzr.bugs.Scenes.*;
import com.yzrilyzr.game.*;
import java.io.*;

public class MainActivity extends com.yzrilyzr.game.MainActivity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		Utils.setContext(this);
        Utils.setMainDir(Environment.getExternalStorageDirectory().getAbsolutePath()+"/yzr的app/Bugs/");
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					try
					{
						Thread.sleep(500);
					}
					catch (InterruptedException e)
					{}
					Utils.loadScene(new LevelSelect("splash"));
				}
			}).start();

    }
	/*Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	 getMenuInflater().inflate(R.menu.menu,menu);
	 return true;
	 }
	 */
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
			File[] f=new File(Utils.mainDir+"/GUI").listFiles();
			Utils.ctx.scenes.get(scenes.size()-1).uis.clear();
			Utils.ctx.scenes.get(scenes.size()-1).loadGUI(Utils.readTxt(f[item.getOrder()-2].getPath()));
        }
		return super.onOptionsItemSelected(item);
    }
}
