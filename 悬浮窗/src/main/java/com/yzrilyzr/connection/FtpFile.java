package com.yzrilyzr.connection;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FtpFile extends File
{
	String path,parent,name;
	long leng,time;
	boolean canr,canw,isf,isd;
	public FtpFile(String path, String parent, String name)
	{
		super(path);
		this.path = path;
		this.parent = parent;
		this.name = name;
		path=path.replace("//","/").replace("ftp:/","ftp://");
		Matcher m=Pattern.compile("ftp://.*?:[0-9].*?/").matcher(path);
		String aa="";
		while(m.find())aa=m.group();
		String p="/"+path.substring(aa.length());
		int c=p.lastIndexOf("/");
		name=p.substring(c+1);
		parent=aa+p.substring(0,c);
		//System.out.println("n:"+name+","+parent);
		
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
}
