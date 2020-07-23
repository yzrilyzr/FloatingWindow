package com.yzrilyzr.myclass;
import java.io.*;
import java.util.*;
public class MP3File
{
	public int length=0;
	public String path;
	public static final String[] p=new String[]{
		"APIC",
		"TEXT",//: 歌词作者 
		"TENC",//: 编码 
		"WXXX",//: URL链接(URL) 
		"TCOP",//: 版权(Copyright) 
		"TOPE",//: 原艺术家 
		"TCOM",//: 作曲家 
		"TDAT",//: 日期 
		"TPE3",//: 指挥者 
		"TPE2",//: 乐队 
		"TPE1",//: 艺术家相当于ID3v1的Artist 
		"TPE4",//: 翻译（记录员、修改员） 
		"TYER",//: 年代相当于ID3v1的Year 
		"USLT",//: 歌词 
		"TALB",//: 专辑相当于ID3v1的Album 
		"TIT1",//: 内容组描述 
		"TIT2",//: 标题相当于ID3v1的Title 
		"TIT3",//: 副标题 
		"TCON",//: 流派（风格）相当于ID3v1的Genre见下表 
		"TBPM",//: 每分钟节拍数 
		"COMM",//: 注释相当于ID3v1的Comment 
		"TDLY",//: 播放列表返录 
		"TRCK",//: 音轨（曲号）相当于ID3v1的Track 
		"TFLT",//: 文件类型 
		"TIME",//: 时间　 
		"TKEY",//: 最初关键字 
		"TLAN",//: 语言 
		"TLEN",//: 长度 
		"TMED",//: 媒体类型 
		"TOAL",//: 原唱片集 
		"TOFN",//: 原文件名 
		"TOLY",//: 原歌词作者 
		"TORY",//: 最初发行年份 
		"TOWM",//: 文件所有者（许可证者） 
		"TPOS",//: 作品集部分 
		"TPUB",//: 发行人 
		"TRDA",//: 录制日期 
		"TRSN",//: Intenet电台名称 
		"TRSO",//: Intenet电台所有者 
		"TSIZ",//: 大小 　 
		"TSRC",//: ISRC（国际的标准记录代码） 
		"TSSE",//: 编码使用的软件（硬件设置） 
		"UFID"//: 唯一的文件标识符 ;
	};
	public HashMap<String,Object> id3v2=new HashMap<String,Object>();
	public HashMap<String,Object> id3v1=new HashMap<String,Object>();
	public MP3File(String mp3path)
	{
		path=mp3path;
	}
	public void loadInfo()
	{
		try
		{
			RandomAccessFile is=new RandomAccessFile(path,"r");
			byte[] id3=new byte[3];
			is.read(id3);
			if(new String(id3).equals("ID3"))
			{
				//System.out.println("ID3");
				int ver=is.read();
				//System.out.println(ver);
				int revision=is.read();
				int flag=is.read();
				/*1.标志字节
				 标志字节一般为0，定义如下：
				 abc00000
				 a -- 表示是否使用不同步(一般不设置)
				 b -- 表示是否有扩展头部，一般没有(至少Winamp没有记录)，所以一般也不设置
				 c -- 表示是否为测试标签(99.99%的标签都不是测试用的啦，所以一般也不设置)*/

				byte[] size = new byte[4];
				is.read(size);
				int l =
					(size[0]&0x7F)<<21 |
					(size[1]&0x7F)<<14 |
					(size[2]&0x7F)<<7|
					(size[3]&0x7F);
				int index=10;
				while (index<l)
				{
					byte[] FrameIdB=new byte[4];
					byte[] SizeB=new byte[4];
					byte[] FlagsB=new byte[2];

					is.read(FrameIdB);
					String FrameID=new String(FrameIdB);
					boolean br=false;
					for(byte c:FrameIdB)
						if(c<0x21||c>0x7e)
						{
							br=true;
							break;
						}
					if(br)break;
					is.read(SizeB);
					int FrameSize=(SizeB[0]&0xff)<<24|(SizeB[1]&0xff)<<16|(SizeB[2]&0xff)<<8|(SizeB[3]&0xff);
					if(FrameSize<1||FrameSize>is.length())break;
					//System.out.println("FrameId:"+FrameID+" Size:"+(FrameSize+10));

					is.read(FlagsB);

					String enc="utf-8";
					//if(!FrameID.equals("PRIV"))
					switch(is.read())
					{
						case 0:enc="ISO-8859-1";break;
						case 1:enc="UTF-16";break;
						case 2:enc="UTF-16BE";break;
						case 3:enc="UTF-8";break;
					}
					if(FrameID.equals("APIC"))
					{
						byte[] mimeB=new byte[12];
						is.read(mimeB);
						String mime=new String(mimeB,enc);
						int sz=FrameSize-13;
						if(mime.indexOf("jpeg")!=-1){
						is.read();
						sz--;
						}
						byte[] pic=new byte[sz];
						is.read(pic);
						this.id3v2.put(FrameID,pic);
					}
					else
					{
						byte[] dt=new byte[FrameSize-1];
						is.read(dt);
						this.id3v2.put(FrameID,new String(dt,enc));
					}

					index+=FrameSize+10;
				}
				is.seek(l);
			}
			else is.seek(0);
			//mp3解码
			//while(is.read()!=0xff)continue;
			/*String cc=
			 Integer.toBinaryString(is.read())+
			 Integer.toBinaryString(is.read())+
			 Integer.toBinaryString(is.read())
			 ;
			 System.out.println(cc);*/
			/*{
				int a=is.read(),b=is.read(),c=is.read();
				String mpeg=new String[]{"MPEG2.5","保留","MPEG2","MPEG1"}[(a&24)>>3];
				System.out.println(mpeg);

				String layer=new String[]{"保留","Layer3","Layer2","Layer1"}[(a&6)>>1];
				System.out.println(layer);

				boolean crc=(a&1)==0;
				System.out.println(crc);

				int[][] brT=new int[][]{
					new int[]{0,32,64,96,128,160,192,224,256,288,320,352,384,416,448,-1},
					new int[]{0,32,48,56,64,80,96,112,128,160,192,224,256,320,384,-1},
					new int[]{0,32,40,48,56,64,80,96,112,128,160,192,224,256,320,-1},
					new int[]{0,32,48,56,64,80,96,112,128,144,160,176,192,224,256,-1},
					new int[]{0,8,16,24,32,40,48,56,64,80,96,112,128,174,160,-1}
				};
				int bitrate=new int[][][]{
					new int[][]{null,brT[4],brT[4],brT[3]},
					null,
					new int[][]{null,brT[4],brT[4],brT[3]},
					new int[][]{null,brT[2],brT[1],brT[0]}
				}[(a&24)>>3][(a&6)>>1][(b&240)>>4];
				System.out.println(bitrate);

				int samplerate=new int[][]{
					new int[]{11025,12000,8000,-1},
					null,
					new int[]{22050,24000,16000,-1},
					new int[]{44100,48000,32000,-1},
				}[(a&24)>>3][(b&12)>>2];
				System.out.println(samplerate);

				int fill=(b&2);
				boolean pri=(b&1)==1;

				String channel=new String[]{"立体声","联合立体声","双声道","单声道"}[(c&192)>>6];
				System.out.println(channel);

				boolean copyright=(c&8)>>3==1;
				boolean origin=(c&4)>>2==1;
				String stress=new String[]{"无","50/15ms","保留","CCIT J.17"}[c&3];
				System.out.println(copyright);
				System.out.println(origin);
				System.out.println(stress);

				int flength=new int[][]{
					new int[]{0,576,1152,384},
					null,
					new int[]{0,576,1152,384},
					new int[]{0,1152,1152,384}
				}[(a&24)>>3][(a&6)>>1];
				if((a&6)>>1==3)flength= ((1000* flength / 8 * bitrate ) / samplerate ) + fill * 4;
				if((a&6)>>1==1||(a&6)>>1==2)flength= ((1000*flength / 8 * bitrate ) / samplerate ) + fill;
				System.out.println(flength);
			}*/
			if(is.length()>128)
			{
				is.seek(is.length()-128);
				id3=new byte[3];
				is.read(id3);
				if(new String(id3).equals("TAG"))
				{
					id3=new byte[30];
					is.read(id3);
					id3v1.put("title",new String(id3));
					is.read(id3);
					id3v1.put("artist",new String(id3));
					is.read(id3);
					id3v1.put("album",new String(id3));
					byte[] uu=new byte[4];
					is.read(uu);
					id3v1.put("year",new String(uu));
					is.read(id3);
					id3v1.put("comment",new String(id3));
					id3v1.put("genre",is.read());

				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	/*public boolean saveInfo()
	 {
	 try
	 {
	 BufferedOutputStream o=new BufferedOutputStream(new FileOutputStream(path.replace(".mp3","_.mp3")));
	 InputStream is=new FileInputStream(path);
	 byte[] id3=new byte[3];
	 is.read(id3,0,3);
	 if(new String(id3).equalsIgnoreCase("ID3"))
	 {
	 is.skip(3);
	 byte[] osize = new byte[4];
	 is.read(osize);
	 is.skip(toSize(osize)-10);
	 byte[] left=new byte[is.available()];
	 is.read(left);
	 is.close();
	 ByteArrayOutputStream os=new ByteArrayOutputStream();
	 for(String x:p)
	 {
	 Object s=MP3File.class.getField(x).get(this);
	 if(s!=null)
	 {
	 os.write(x.getBytes());
	 if(x.equals("APIC"))
	 {
	 byte[] d=APIC;
	 byte[] d2=new byte[d.length+13];
	 System.arraycopy(new byte[]{3,'i','m','a','g','e','/','j','p','g','a','a','a'},0,d2,0,13);
	 System.arraycopy(d,0,d2,13,d.length);
	 int si=d2.length;
	 os.write(si/0x1000000);
	 os.write(si%0x1000000/0x10000);
	 os.write(si%0x10000/0x100);
	 os.write(si%0x100);
	 os.write(0);
	 os.write(0);
	 os.write(d2);
	 }
	 else
	 {
	 byte[] d=((String)s).getBytes();
	 int si=d.length+1;
	 os.write(si/0x1000000);
	 os.write(si%0x1000000/0x10000);
	 os.write(si%0x10000/0x100);
	 os.write(si%0x100);
	 os.write(0);
	 os.write(0);
	 os.write(3);
	 os.write(d);
	 }
	 }
	 }
	 os.flush();
	 os.close();
	 int s=os.toByteArray().length+10;
	 byte[] hd=new byte[]{'I','D','3',3,0,0,
	 (byte)((s>>21)&0x7f),
	 (byte)((s>>14)&0x7f),
	 (byte)((s>>7)&0x7f),
	 (byte)(s&0x7f)
	 };
	 o.write(hd);
	 o.write(os.toByteArray());
	 o.write(left);
	 o.flush();
	 o.close();
	 }

	 return true;
	 }
	 catch(Throwable e)
	 {
	 return false;
	 }
	 }*/
}
