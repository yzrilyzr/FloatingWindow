package com.yzrilyzr.engine2d;

public abstract class Scene implements Eg.GameCBK
{
	public void removeSelf(){
		stop();
		Eg.gameact.mSceneList.remove(this);
	}
}
