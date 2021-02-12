package com.yzrilyzr.bugs.Scenes;
import com.yzrilyzr.game.*;
import android.graphics.*;

public class test extends Scene
{
	boolean a=true,b=true;
	public test(String id){
		super(id);
		loadGUI(Utils.readTxt((Utils.mainDir+"GUI/修炼.txt")));
	}

	@Override
	public void onDraw(Canvas c)
	{
		// TODO: Implement this method
		super.onDraw(c);
		if(time>3500&&a){
			a=!a;
			uis.clear();
			loadGUI(Utils.readTxt((Utils.mainDir+"GUI/修炼2.txt")));
		}
		if(time>4500&&b){
			uis.clear();
		}
	}
	
} 
