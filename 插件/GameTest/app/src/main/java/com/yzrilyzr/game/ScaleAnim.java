package com.yzrilyzr.game;

import android.graphics.*;

public class ScaleAnim extends BaseAnim
{
	public Matrix ma;
	public ScaleAnim(Matrix m){
		ma=m;
	}
	@Override
	public void doAnim()
	{
		// TODO: Implement this method
		super.doAnim();
		ma.preScale(((fromto[1]-fromto[0])*antime+fromto[0])/100f,((fromto[3]-fromto[2])*antime+fromto[2])/100f,cx,cy);
	}
}
