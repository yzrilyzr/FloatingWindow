package com.yzrilyzr.game;

public class Vector
{
	public float sx,sy;
	public float vx,vy;
	public int color=0xff000000;
	public Vector(float sx, float sy, float vx, float vy)
	{
		this.sx = sx;
		this.sy = sy;
		this.vx = vx;
		this.vy = vy;
	}
	public Vector move(float sx,float sy){
		this.sx=sx;
		this.sy=sy;
		return this;
	}
	public Vector move(float p){
		float m=mod();
		sx+=vx/m*p;
		sy+=vy/m*p;
		return this;
	}
	public Vector setColor(int c){
		color=c;
		return this;
	}
	public Vector setLength(float l){
		float vvx=vx*l/mod();
		float vvy=vy*l/mod();
		vx=vvx;
		vy=vvy;
		return this;
	}
	public float cos(Vector b){
		return product(b)/mod()/b.mod();
	}
	public float mod(){
		return (float)Math.sqrt(vx*vx+vy*vy);
	}
	public float product(Vector b){
		return vx*b.vx+vy*b.vy;
	}
	public Vector normalize(){
		float m=mod();
		vx/=m;
		vy/=m;
		return this;
	}
	public Vector product(float p){
		vx*=p;
		vy*=p;
		return this;
	}
	public Vector(Vector src){
		sx=src.sx;
		sy=src.sy;
		vx=src.vx;
		vy=src.vy;
	}
}
