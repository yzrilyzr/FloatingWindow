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
	public void reverse(){
		int y;
		if(fromto.length==2){
			y=fromto[0];
			fromto[0]=fromto[1];
			fromto[1]=y;
		}
		else if(fromto.length==4){
			y=fromto[0];
			fromto[0]=fromto[2];
			fromto[2]=y;
			y=fromto[1];
			fromto[1]=fromto[3];
			fromto[3]=y;
		}
		time=0;
	}
}
