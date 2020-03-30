package com.yzrilyzr.engine2d;

import com.yzrilyzr.engine2d.*;
import java.util.*;

public abstract class Scene implements Eg.GameCBK
{
	public ArrayList<Ui> uis=new ArrayList<Ui>();
	public void removeSelf(){
		stop();
		Eg.gameact.mSceneList.remove(this);
		
	}
	public void add(Ui... u){
		for(Ui c:u)uis.add(c);
	}
	public void anim(boolean b){
		for(Ui c:uis)c.anim=b;
	}
	public void anim(boolean b,Ui... u){
		for(Ui c:u)c.anim=b;
	}
}
