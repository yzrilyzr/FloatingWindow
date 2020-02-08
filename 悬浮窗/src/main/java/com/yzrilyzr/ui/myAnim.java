package com.yzrilyzr.ui;

import android.view.View;
import android.view.animation.TranslateAnimation;
import com.yzrilyzr.myclass.util;

public class myAnim
{
	
	public myAnim(View v,String patt)
	{
		String[] ps=patt.split(";");
		for(String p:ps){
			String[] u=p.split(":");
			switch(u[0]){
				case "tt":
					break;
				case "tf":
					break;
				case "t":
					//TranslateAnimation tra=new TranslateAnimation();
					break;
			}
		}
	}
	public static float getNLinearValueByTime(float time,float starttime,float endtime){
		float sec=endtime-starttime;
		if(sec<=0)sec=1;
		float x=util.limit((time-starttime)/sec,0f,1f);
		return NonLinearFunc(x);
	}
	public static float NonLinearFunc(float x){
		float y=x<0.5?(float)Math.pow(x,2)*2f:-(float)Math.pow(x-1f,2)*2f+1f;
		return util.limit(y,0,1);
	}
}
