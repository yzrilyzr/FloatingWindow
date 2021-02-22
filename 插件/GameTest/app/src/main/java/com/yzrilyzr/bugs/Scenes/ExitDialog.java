package com.yzrilyzr.bugs.Scenes;
import com.yzrilyzr.game.*;
import com.yzrilyzr.bugs.Game.*;

public class ExitDialog extends Scene
{
	public ExitDialog(String id){
		super(id);
		loadGUIPath("GUI/exitdialog.txt");
	}
	public void cancel(Ui s){
		exitAnim();
	}
	public void ok(Ui s){
		Data.save();
		System.exit(0);
	}
}
