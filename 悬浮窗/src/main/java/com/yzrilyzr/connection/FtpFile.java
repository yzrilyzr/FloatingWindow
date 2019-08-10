package com.yzrilyzr.connection;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FtpFile extends File
{
	String path,parent,name;
	long leng,time;
	boolean canr,canw,isf,isd;
	public FtpFile(String path)
	{
		super(path);
		this.path=path;
		path();
		//System.out.println("n:"+name+","+parent);
	}
	public void path(){
		path=path.replace("//","/").replace("ftp:/","ftp://");
		Matcher m=Pattern.compile("ftp://.*?:[0-9].*?/").matcher(path);
		String aa="";
		while(m.find())aa=m.group();
		String p="/"+path.substring(aa.length());
		int c=p.lastIndexOf("/");
		name=p.substring(c+1);
		parent=aa+p.substring(0,c);
	}
	public FtpFile(String path, String parent, String name, long leng, long time, boolean canr, boolean canw, boolean isf, boolean isd)
	{
		super(path);
		this.path = path;
		this.parent = parent;
		this.name = name;
		this.leng = leng;
		this.time = time;
		this.canr = canr;
		this.canw = canw;
		this.isf = isf;
		this.isd = isd;
	}

	@Override
	public File getAbsoluteFile()
	{
		// TODO: Implement this method
		return new FtpFile(path,parent,name,leng,time,canr,canw,isf,isd);
	}
	@Override
	public String getAbsolutePath()
	{
		// TODO: Implement this method
		return path;
	}

	@Override
	public String getPath()
	{
		// TODO: Implement this method
		return path;
	}

	@Override
	public File getParentFile()
	{
		// TODO: Implement this method
		return new FtpFile(parent);
	}

	@Override
	public String getParent()
	{
		// TODO: Implement this method
		return parent;
	}

	@Override
	public String getName()
	{
		// TODO: Implement this method
		return name;
	}

	@Override
	public boolean isFile()
	{
		// TODO: Implement this method
		return isf;
	}

	@Override
	public boolean isDirectory()
	{
		// TODO: Implement this method
		return isd;
	}

	@Override
	public boolean canRead()
	{
		// TODO: Implement this method
		return canr;
	}

	@Override
	public boolean canWrite()
	{
		// TODO: Implement this method
		return canw;
	}

	@Override
	public long lastModified()
	{
		// TODO: Implement this method
		return time;
	}

	@Override
	public long length()
	{
		// TODO: Implement this method
		return leng;
	}

}
