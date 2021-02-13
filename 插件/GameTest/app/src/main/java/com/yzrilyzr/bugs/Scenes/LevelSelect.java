package com.yzrilyzr.bugs.Scenes;
import com.yzrilyzr.game.*;

public class LevelSelect extends Scene
{
	public LevelSelect(String id){
		super(id);
		setBackgroundColor(0xff40bbff);
		loadGUI(Utils.readTxt(Utils.mainDir+"GUI/levelselect.txt"));
	}
	public void mainmenu(Ui s){
		Utils.loadScene(new MainMenu("mainmenul"));
		exitAnim();
	}
}
