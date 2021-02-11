package com.yzrilyzr.game;
import android.graphics.*;

public class TransAnim extends BaseAnim
{
	public Matrix ma;
	public TransAnim(Matrix m){
		ma=m;
	}
	@Override
	public void doAnim()
	{
		// TODO: Implement this method
		super.doAnim();
		ma.postTranslate((fromto[2]-fromto[0])*antime+fromto[0],(fromto[3]-fromto[1])*antime+fromto[1]);
	}
}
