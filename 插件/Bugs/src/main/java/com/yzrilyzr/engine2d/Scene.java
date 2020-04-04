package com.yzrilyzr.engine2d;

import com.yzrilyzr.engine2d.*;
import java.util.concurrent.*;
import android.graphics.*;

public abstract class Scene implements Eg.GameCBK
{
	public CopyOnWriteArrayList<Ui> uis=new CopyOnWriteArrayList<Ui>();
	public void removeSelf(){
		Eg.gameact.mSceneList.remove(this);
	}
	public void add(Ui... u){
		for(Ui c:u)uis.add(c);
	}

	@Override
	public void render(Canvas c, float dt)
	{
		for(Ui u:uis)u.draw(c,dt);
	}
}
