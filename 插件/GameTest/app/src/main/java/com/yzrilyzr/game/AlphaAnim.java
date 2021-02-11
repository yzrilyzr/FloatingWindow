package com.yzrilyzr.game;

import android.graphics.*;

public class AlphaAnim extends BaseAnim
{
	public Paint p;
	public AlphaAnim(Paint p)
	{
		this.p=p;
	}

	@Override
	public void doAnim()
	{
		// TODO: Implement this method
		super.doAnim();
		p.setAlpha((int)(((fromto[1]-fromto[0])*antime+fromto[0])*255f/100f));
	}

}
