package com.yzrilyzr.bugs.Scenes;
import com.yzrilyzr.game.*;

public class Settings extends Scene
{
	public Settings(String id){
		super(id);
		loadGUI(Utils.readTxt(Utils.mainDir+"GUI/settings.txt"));
		if(id.equals("settingsmainmenu"))removeGUI("smainmenu");
	}
	public void close(Ui s){
		exitAnim();
	}
	public void mainmenu(Ui s){
		Utils.unloadAll();
		Utils.loadScene(new MainMenu("mainmenul"));
	}
}
