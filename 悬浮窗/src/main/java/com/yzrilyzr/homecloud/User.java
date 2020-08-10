package com.yzrilyzr.homecloud;
import java.io.*;

public class User implements Serializable
{
	public String usr;
	public String pwd;//密文存储
	public byte[] head;
	public String uid;
}
