package com.yzrilyzr.bugs.Scenes;
import com.yzrilyzr.game.*;

public class Info extends Scene
{
	public Info(String id){
		super(id);
		loadGUIPath("GUI/info.txt");
	}
	public void uiaboutok(Ui s)
	{
		exitAnim();
	}
	
}
