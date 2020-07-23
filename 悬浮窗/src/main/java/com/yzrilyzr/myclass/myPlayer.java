package com.yzrilyzr.myclass;
import java.io.*;
import jmp123.decoder.*;
import android.media.*;

public class myPlayer extends AbstractDecoder implements IAudio
{
	InputStream is=null;
	AudioTrack track;
	public myPlayer(String path){
		super(null);
		this.audio=this;
		File file=new File(path);
        if(!file.exists() || !path.toLowerCase().endsWith(".mp3")) {
            throw new RuntimeException("文件不存在");
        }
		try
		{
			is=new FileInputStream(file);
			System.out.println(openDecoder());
			run();
		}
		catch (FileNotFoundException e)
		{}
	}

	@Override
	protected int fillBuffer(byte[] b, int off, int len)
	{
		// TODO: Implement this method
		try
		{
			return is.read(b,off,len);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	protected void done()
	{
		// TODO: Implement this method
		try
		{
			is.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean open(int rate, int channels, int bufferSize)
	{
		track=new AudioTrack(
		AudioManager.STREAM_MUSIC,
		rate,
		channels==1?AudioFormat.CHANNEL_CONFIGURATION_MONO:AudioFormat.CHANNEL_CONFIGURATION_STEREO,
		AudioFormat.ENCODING_PCM_16BIT,
		bufferSize*2,
		AudioTrack.MODE_STREAM);
		track.play();
		return true;
	}

	@Override
	public int write(byte[] b, int off, int size)
	{
		//System.out.println(off);
		return track.write(b,off,size);
	}

	@Override
	public void start(boolean started)
	{
		if(track==null)return;
		if(started)track.play();
		else track.stop();
	}

	@Override
	public void drain()
	{
		track.release();
	}

	@Override
	public void close()
	{
		track.release();
	}
}
