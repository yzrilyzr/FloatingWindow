package com.yzrilyzr.ui;

import android.view.View;
import android.view.animation.TranslateAnimation;
import com.yzrilyzr.myclass.util;
import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;

public class myAnim implements Animation.AnimationListener
{
	public TranslateAnimation tr;
	public AlphaAnimation al;
	public ScaleAnimation sc;
	public RotateAnimation ro;
	final static Interpolator in=new Interpolator(){
		@Override
		public float getInterpolation(float p1)
		{
			// TODO: Implement this method
			return NonLinearFunc(p1);
		}
	};
	public myAnim(View v,String patt)
	{
		//TranslateAnimation
		//AlphaAnimation
		//ScaleAnimation
		//RotateAnimation
		//t:20,40,60,80,0延迟,500持续时间
		AnimationSet set=new AnimationSet(true);
		set.setInterpolator(in);
		String[] ps=patt.split(";");
		for(String p:ps){
			String[] u=p.split(":");
			String[] pps=u[1].split(",");
			int[] n=new int[pps.length];
			for(int i=0;i<n.length;i++)n[i]=util.px(Float.parseFloat(pps[i]));
			switch(u[0]){
				case "t":
					tr=new TranslateAnimation(n[0],n[1],n[2],n[3]);
					tr.setStartOffset(n[4]);
					tr.setDuration(n[5]);
					set.addAnimation(tr);
					break;
				case "a":
					al=new AlphaAnimation(n[0],n[1]);
					al.setStartOffset(n[2]);
					al.setDuration(n[3]);
					set.addAnimation(al);
					break;
				case "s":
					sc=new ScaleAnimation(n[0],n[1],n[2],n[3]);
					sc.setStartOffset(n[4]);
					sc.setDuration(n[5]);
					set.addAnimation(sc);
					break;
				case "r":
					ro=new RotateAnimation(n[0],n[1]);
					ro.setStartOffset(n[2]);
					ro.setDuration(n[3]);
					set.addAnimation(ro);
					break;
			}
		}
		v.setAnimation(set);
		set.setAnimationListener(this);
		set.start();
		
	}
	@Override
	public void onAnimationEnd(Animation p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onAnimationRepeat(Animation p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onAnimationStart(Animation p1)
	{
		// TODO: Implement this method
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
