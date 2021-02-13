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
	public void help(Ui s){
		Utils.loadScene(new GameMain(Utils.readTxt("maps/map0")));
		exitAnim();
	}
	public void achi(Ui s){
		Utils.loadScene(new GameMain(Utils.readTxt("maps/map2")));
		exitAnim();
	}
	public void bugs(Ui s){
		Utils.loadScene(new GameMain(Utils.readTxt("maps/map1")));
		exitAnim();
	}
}
