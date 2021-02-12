package com.yzrilyzr.bugs.Scenes;
import com.yzrilyzr.game.*;

public class MainMenu extends Scene
{
	public MainMenu(String id)
	{
		super(id);
		loadGUI(Utils.readTxt((Utils.mainDir+"GUI/mainmenu.txt")));
		setBackgroundColor(0xff333333);
	}
	public void info(Ui s){
		loadGUI(Utils.readTxt((Utils.mainDir+"GUI/info.txt")));
	}
	public void uiaboutok(Ui s){
		int p=uis.indexOf(s);
		uis.remove(p-2);
		uis.remove(p-2);
		uis.remove(p-2);
	}
}
