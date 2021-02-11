package com.yzrilyzr.game;

import android.graphics.*;

public class RotateAnim extends BaseAnim
{
	public Matrix ma;
	public RotateAnim(Matrix m){
		ma=m;
	}
	@Override
	public void doAnim()
	{
		// TODO: Implement this method
		super.doAnim();
		ma.preRotate((fromto[1]-fromto[0])*antime+fromto[0],cx,cy);
	}
	
}
