package com.yzrilyzr.engine2d;
import android.graphics.*;

public class Button extends Ui
{
	public Button(String vec,float size,int g,float x,float y,Ui parent){
		super(vec,size,g,x,y,parent);
	}
	public Button(String vec,float d,int g,float x,float y,Ui p,boolean r){
		super(vec,d,g,x,y,p);
		this.r=Eg.p(d/2f);
	}
}
