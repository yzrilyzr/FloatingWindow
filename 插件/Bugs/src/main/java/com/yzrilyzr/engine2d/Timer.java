package com.yzrilyzr.engine2d;
import java.util.*;

public class Timer
{
	ArrayList<Integer> ts=new ArrayList<Integer>();
	public float timer=0;
	public Timer(int... offsets){
		for(int g:offsets)ts.add(g);
	}
	public int render(float dt){
		timer+=dt;
		timer=Eg.limit(timer,0,3600000);
		int size=ts.size();
		if(size==0)return 0;
		for(int i=size-1;i>=0;i--){
			if(ts.get(i)<=timer)return ts.get(i);
		}
		return -1;
	}
}
