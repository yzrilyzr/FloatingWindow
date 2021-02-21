package com.yzrilyzr.game;
import android.media.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class SoundPlay extends OutputStream implements Runnable
{
	AudioTrack tr;
	CopyOnWriteArrayList<byte[]> bs=new CopyOnWriteArrayList<byte[]>();
	CopyOnWriteArrayList<MIDIParser.Event> events=new CopyOnWriteArrayList<MIDIParser.Event>();
	public SoundPlay()
	{
		tr=new AudioTrack(
			AudioManager.STREAM_MUSIC,
			44100,
			AudioFormat.CHANNEL_CONFIGURATION_STEREO,
			AudioFormat.ENCODING_PCM_16BIT,
			AudioTrack.getMinBufferSize(44100,AudioFormat.CHANNEL_CONFIGURATION_STEREO,AudioFormat.ENCODING_PCM_16BIT)*10,
			AudioTrack.MODE_STREAM);
			new Thread(this).start();
		new Thread(new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
				}
			}).start();
	}

	public void pause()
	{
		tr.pause();
	}

	public void play()
	{
		tr.play();
	}
	
	@Override
	public void run()
	{
		tr.play();
		while(tr.getPlayState()==AudioTrack.PLAYSTATE_PLAYING){
			try
			{
				for(MIDIParser .Event e:events){
					
				}
				if(bs.size()>0)
				{
					byte[] b=bs.get(0);
					tr.write(b,0,b.length);
					bs.remove(0);
				}
				else Thread.sleep(1);
			}
			catch (Exception e)
			{
				Utils.alert(e);
				break;
			}
		}
	}

	@Override
	public void write(int p1) throws IOException
	{
		// TODO: Implement this method
	}

	@Override
	public void write(byte[] b) throws IOException
	{
		// TODO: Implement this method
		bs.add(b);
		super.write(b);
	}
	public void midievent(MIDIParser.Event e){
		events.add(e);
	}
	public byte[] getMusic2(int[][] p,float[] ins,float[] gains,int[] pi)
	{
		float[] da=null;
		int bl=0;
		boolean bp=false;
		for(int ii=0;ii<p.length;ii++)
			for(int i=0,o=0;i<p[ii].length;i+=2)
			{
				if(p[ii][i]==-200)bp=!bp;
				if(!bp)
				{
					o+=p[ii][i+1];
					bl=Math.max(bl,o);
				}
			}
		da=new float[bl];
		for(int ii=0;ii<p.length;ii++)
		{
			System.out.println((ii+1)+"/"+p.length);
			boolean pp=false;
			for(int i=0,o=0,ol=0;i<p[ii].length;i+=2)
			{
				float len=p[ii][i+1];
				int id=p[ii][i]+pi[ii];
				float len2=len;
				if(p[ii][i]==-200)
				{
					ol=o;pp=!pp;
					continue;
				}
				if(p[ii][i]==-100)
				{
					o+=len;
					continue;
				}
				float t=1f/16.3515981f/(float)Math.pow(2,id/12f);
				while(len>0)
					da[o++]+=10f*b(len,len2)*pwm((len2-(len)--)/(float)tr.getSampleRate(),t,ins[ii*3],ins[ii*3+1],ins[ii*3+2],0)*gains[ii];
				if(pp)o=ol;
			}
		}
		float max=0;
		for(int k=0;k<da.length;k++)max=Math.max(max,da[k]);
		int[] da2=new int[da.length];
		max=32767f/max;
		for(int k=0;k<da.length;k++)da2[k]=(int)(da[k]*max);
		byte[] b=new byte[da.length*4];
		stereo_PCM_16Bit(b,da2,da2);
		return b;
	}
	static float b(float x,float t){
		return Math.min(2*sin(x/2,t),1);
	}
	static float a(float x,float period,float wi,float ri,float fa,float d){
		//float[] aa=new float[]{0.0066f,0.1894f,0.1407f,0.1355f,0.0309f,0.01f,0.0113f,0.0214f,0.0222f,0.018f,0.0184f,0.0121f};
		float y=0;
		float[] aa=new float[]{0.5f};
		for(int i=0;i<aa.length;i++)y+=aa[i]*sin(x,period/(i+1f));
		return y;
	}
	static float pwm(float x,float period,float width,float rise,float fall,float delay)
	{
		if(width==-1)return (2*(float)Math.random()-1)*(period-x)/period;
		float r=period*rise;
		float w=width*period;
		float f=fall*period;
		x=(x+period*delay+r/2f)%period;
		if(x<r)return x*2f/r-1f;
		else if(x>=r&&x<r+w)return 1;
		else if(x>=r+w&&x<r+w+f)return 1f-(x-r-w)*2f/f;
		else return -1;
	}
	static float vvvf(float x,float p,boolean sync,float syncC){
		float c=0;
		if(sync)c=p/syncC;
		else {
			float f=1f/p;
			f*=10f;
			if(f<500)f=500;
			while(f>600)f-=200;
			c=1f/f;
		}
		return sin(x,p)>2f*pwm(x,c,0,0.5f,0.5f,0)?1:-1;
	}
	static float spwm(float x,float p,float y)
	{
		return y>pwm(x,p,0,0.5f,0.5f,0)?1:-1;
	}
	static float sin(float x,float p){
		return (float)Math.sin(2.0*Math.PI*x/p);
	}
//.左 降8度   右. 升8度
//_ 时长/2   - 时长*2
//# 升调   b 降调
//* 附点   + 连嘤   () 三连嘤   [] 一起嘤
	public int[] parse(String s,int bpm)
	{
		int[] ids=new int[]{-100,48,50,52,53,55,57,59};
		ArrayList<Integer> list=new ArrayList<Integer>();
		s=s.replace("\n","");
		String[] k=s.split(" ");
		int ti=-1;
		int ilen=60*tr.getSampleRate()/bpm;
		for(String a:k)
		{
			if("".equals(a))continue;
			int id=0,li=0;
			int[] len=new int[]{ilen,0,0};
			for(int u=0,ni=-1;u<a.length();u++)
			{
				char c=a.charAt(u);
				if(c>=48&&c<=55)
				{id+=ids[(int)c-48];ni=u;}
				else if(c=='.')
					if(ni==-1)id-=12;
					else id+=12;
				else if(c=='_')len[li]/=2;
				else if(c=='-')len[li]+=ilen;
				else if(c=='#')id+=1;
				else if(c=='b')id-=1;
				else if(c=='*')len[li]=len[li]*3/2;
				else if(c=='(')ti=3;
				else if(c=='x')id=33;
				else if(c=='y')id=37;
				else if(c=='+')len[++li]=ilen;
				else if(c=='['||c==']')
				{
					list.add(-200);
					list.add(0);
				}
				else if(c!=')')throw new UnsupportedOperationException("未知字符:"+c);
			}
			if(ti>0)
			{
				len[li]/=3;ti--;
			}
			list.add(id);
			int gli=0;
			for(int c:len)gli+=c;
			list.add(gli);
		}
		int[] l=new int[list.size()];
		int y=0;
		for(int p:list)l[y++]=p;
		return l;
	}
	public static void stereo_PCM_16Bit(byte[] data,int[] left,int[] right)
    {
        for(int i=0;i<data.length/4;i++)
        {
			if(left[i]>32767)left[i]=32767;
			if(left[i]<-32767)left[i]=-32767;
			if(right[i]>32767)right[i]=32767;
			if(right[i]<-32767)right[i]=-32767;
            byte a=(byte) (left[i]-left[i]/0x100*0x100),b=(byte) (left[i]/0x100);
            data[i*4]=a;
            data[i*4+1]=b;
            a=(byte) (right[i]-right[i]/0x100*0x100);b=(byte) (right[i]/0x100);
            data[i*4+2]=a;
            data[i*4+3]=b;
        }
    }
}
