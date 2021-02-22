package com.yzrilyzr.bugs.Scenes;
import com.yzrilyzr.game.*;
import android.graphics.*;

public class Settings extends Scene
{
	float dt=-1;
	public Settings(String id){
		super(id);
		loadGUIPath("GUI/settings.txt");
		if(id.equals("settingsmainmenu"))removeGUI("smainmenu");
	}
	public void close(Ui s){
		exitAnim();
	}
	public void mainmenu(Ui s){
		//Utils.unloadAll();
		dt=0;
		loadGUIPath("GUI/mainmenu/mainmenuload.txt");
	}

	@Override
	public void onDraw(Canvas c)
	{
		// TODO: Implement this method
		super.onDraw(c);
		if(dt!=-1)dt+=Utils.getDtMs();
		if(dt>500){
			dt=-1;
			MainMenu s=new MainMenu("mainmenul");
			Utils.unloadAll();
			Utils.loadScene(s);
			Utils.loadScene(this);
			for(int i=0;i<4;i++)uis.remove(0);
			
			//Utils.ctx.scenes.add(Utils.ctx.scenes.size()-2,s);
			exitAnim();
			
		}

	}
	
}
