package com.yzrilyzr.engine2d;
import android.graphics.*;

public class Button extends Ui
{
	public Button(String vec,int g,float size,float x,float y){
		super(vec,g,size,x,y);
	}
	public void click(){
		
	}
	public Button(String vec,int g,float d,float x,float y,boolean r){
		super(vec,g,d,x,y);
		this.r=Eg.p(d/2f);
	}
}
