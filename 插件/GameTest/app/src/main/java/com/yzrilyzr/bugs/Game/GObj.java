package com.yzrilyzr.bugs.Game;

public class GObj
{
	public float x,y,r;
	public boolean contains(float px,float py,float r){
		if(Math.pow(px-x,2)+Math.pow(py-y,2)<r*r)return true;
		return false;
	}
	
}
