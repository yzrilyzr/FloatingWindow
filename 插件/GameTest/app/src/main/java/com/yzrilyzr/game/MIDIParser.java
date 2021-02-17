package com.yzrilyzr.game;
import android.media.midi.*;
import java.io.*;
import java.util.*;

public class MIDIParser
{
	public int format,blocks,tick;
	public int vel;
	public ArrayList<Event> events=new ArrayList<Event>();
	public MIDIParser(InputStream is) throws Exception
	{
		//MidiManager m=(MidiManager) Utils.ctx.getSystemService(Utils.ctx.MIDI_SERVICE);
		//m.
		DataInputStream d=new DataInputStream(is);
		byte[] MThd=new byte[4];
		d.read(MThd);
		if(!new String(MThd).equals("MThd"))throw new IOException("不是midi文件");
		int l=d.readInt();
		format=d.readShort();
		blocks=d.readShort();
		tick=d.readShort();
		for(int i=0;i<blocks;i++)
		{
			byte[] MTrk=new byte[4];
			d.read(MTrk);
			if(!new String(MTrk).equals("MTrk"))throw new IOException("MTrk错误");
			byte[] b2=new byte[d.readInt()];
			d.read(b2);
			DataInputStream d2=new DataInputStream(new ByteArrayInputStream(b2));
			while(d2.available()>0)
			{
				Event ev=new Event();
				events.add(ev);
				int p=0,e=2;
				//dt
				while(((p=d2.read())&128)==128)
				{
					ev.dt+=Math.pow(128,e--)*(p-128);
				}
				ev.dt+=p;

				//event
				int event=(ev.event=d2.read());
				if(event<0x80){
					ev.event=events.get(events.size()-2).event;
					int av=d2.available();
					d2.reset();
					d2.skip(b2.length-av-1);
				}
				//参数
				
				//松开
				if(event>=0x80&&event<=0x8F)
				{
					ev.noteoff=d2.read();
					ev.noteoff2=d2.read();
				}
				//按下
				if(event>=0x90&&event<=0x9F)
				{
					ev.noteon=d2.read();
					ev.noteon2=d2.read();
				}

				//key after touch 音符
				if(event>=0xA0&&event<=0xAF)
				{
					ev.keyaftertouch=d2.read();
					ev.keyaftertouch2=d2.read();
				}
				//控制器
				if(event>=0xB0&&event<=0xBF)
				{
					ev.controller=d2.read();
					ev.controllerp=d2.read();
				}
				//program changes 乐器
				if(event>=0xC0&&event<=0xCF)
				{
					ev.programchanges=d2.read();
				}
				//Aftertouch
				if(event>=0xD0&&event<=0xDF)
				{
					ev.aftertouch=d2.read();
				}
				//滑音
				if(event>=0xE0&&event<=0xEF)
				{
					d2.read();
					d2.read();
				}
				//系统码
				else if(event==0xF0)
				{
					
				}
				//其他格式
				else if(event==0xff)
				{
					int type=(ev.type=d2.read());
					if(type==0x2f)break;
					int bts=d2.read();
					byte[] b=new byte[bts];
					d2.read(b);

					if(type==0x51)
					{

					}
				}
			}
		}
		/*SoundPlay pl=new SoundPlay();
		for(Event e:events){
			pl.midievent(e);
		}*/
		
	}
	public class Event
	{
		public int dt,event,type;
		public int noteoff,noteon,keyaftertouch,controller,programchanges,aftertouch,pitch,sysex;
		public int noteoff2,noteon2,keyaftertouch2,controllerp;
	}
}
