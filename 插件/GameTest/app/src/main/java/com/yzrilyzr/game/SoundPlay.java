package com.yzrilyzr.game;
import android.media.*;
import java.io.*;
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
	
}
