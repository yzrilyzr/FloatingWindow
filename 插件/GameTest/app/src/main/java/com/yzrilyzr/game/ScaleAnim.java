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
		ma.preScale(((fromto[2]-fromto[0])*antime+fromto[0])/100f,((fromto[3]-fromto[1])*antime+fromto[1])/100f,cx,cy);
	}
}
