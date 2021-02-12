package com.yzrilyzr.game;

public class BaseAnim
{
	public int duration,delay;
	public int[] fromto=new int[0];
	public float time,antime,cx,cy;
	public void doAnim(){
		if(time<delay)antime=0;
		else if(time<=delay+duration){
			antime=Utils.NonLinearFunc((time-delay)/duration);
		}
		else antime=1;
		time+=Utils.dt/1000000f;
	}
}
