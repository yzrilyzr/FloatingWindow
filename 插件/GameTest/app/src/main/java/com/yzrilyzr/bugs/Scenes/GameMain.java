package com.yzrilyzr.bugs.Scenes;
import android.graphics.*;
import com.yzrilyzr.bugs.Game.*;
import com.yzrilyzr.game.*;
import java.util.concurrent.*;

public class GameMain extends Scene
{
	Map map;
	public CopyOnWriteArrayList<Bug> bugs=new CopyOnWriteArrayList<Bug>();
	public CopyOnWriteArrayList<Tower> towers=new CopyOnWriteArrayList<Tower>();
	public CopyOnWriteArrayList<Bullet> bullets=new CopyOnWriteArrayList<Bullet>();
	
	float sendnowcool=-1000;
	public GameMain(String id){
		super(id);
		try
		{
			map=Map.loadMap(id);
			map.loadTiles(0.5f);
			map.findWayPoint();
			loadGUI(Utils.readTxt(Utils.mainDir+"GUI/gamemain.txt"));
		}
		catch (Exception e)
		{
			Utils.alert(e);
			Utils.loadScene(new LevelSelect("levelselect"));
			return;
		}
	}
	public void setting(Ui s){
		Utils.loadScene(new Settings("settings"));
	}
	public void sendnow(Ui s){
		loadGUI(Utils.readTxt(Utils.mainDir+"GUI/gamemain_sendnow.txt"));
		sendnowcool=5300;
	}
	@Override
	public void onDraw(Canvas c)
	{
		// TODO: Implement this method
		super.onDraw(c);
		if(sendnowcool>0)sendnowcool-=Utils.getDtMs();
		else if(sendnowcool!=-1000){
				removeGUI("sendnowmask");
				sendnowcool=-1000;
			}
		c.drawBitmap(map.background,0,0,p);
		for(int i=0;i<map.map.length;i++){
			for(int j=0;j<map.map[i].length;j++){
				if(map.map[i][j]!=0)c.drawBitmap(map.tiles[map.map[i][j]],i*map.tilew,j*map.tilew,p);
			}
		}
		for(Map.AstarPoint pp:map.wpwaypoint.get(0))
			c.drawBitmap(map.tiles[5],pp.x*map.tilew,pp.y*map.tilew,p);
	}
	
}
