package com.yzrilyzr.engine2d;
import java.util.*;

public class Timer
{
	ArrayList<Integer> ts=new ArrayList<Integer>();
	public float timer=0;
	public int st,ed;
	public Timer(int... offsets){
		for(int g:offsets)ts.add(g);
	}

	public void reset()
	{
		timer=0;
	}
	public float getFuncX(){
		return Eg.getFuncXByTime(timer,st,ed);
	}
	public float getNLF(){
		return Eg.getNLinearValueByTime(timer,st,ed);
	}
	public float getSF(){
		return Eg.getSinValueByTime(timer,st,ed);
	}
	public int render(float dt){
		timer+=dt;
		timer=Eg.limit(timer,0,3600000);
		int size=ts.size();
		if(size==0)return st=0;
		for(int i=size-1;i>=0;i--){
			if(ts.get(i)<=timer){
				st=ts.get(i);
				if(i+1<ts.size())ed=ts.get(i+1);
				else ed=-1;
				return st;
			}
		}
		return st=-1;
	}
}
