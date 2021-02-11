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
	
}
