package com.yzrilyzr.engine2d;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.yzrilyzr.icondesigner.VECfile;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.graphics.Point;

public class Map
{
	public int[][] map;//x y
	public int size=0;
	public String name="";
	public Bitmap background=null;
	public Bitmap[] tiles=new Bitmap[100];
	public Point start=new Point(),finish=new Point();
	public void loadTiles() throws Exception
	{
		tiles[0]=null;
		tiles[1]=tile("wall");
		tiles[2]=tile("tree",1.8f);
		tiles[3]=tile("start");
		tiles[4]=tile("finish");
		tiles[5]=tile("waypoint");
	}
	public static Map loadMap(InputStream i) throws Exception
	{
		BufferedReader br=new BufferedReader(new InputStreamReader(i));
		String l=null;
		Map tmp=new Map();
		int mapi=-1;
		while((l=br.readLine())!=null)
		{
			if(l.startsWith("#"))continue;
			if(l.startsWith("name:"))tmp.setName(l.substring(5));
			else if(l.startsWith("size:"))tmp.setSize(Integer.parseInt(l.substring(5)));
			else if(l.startsWith("background:"))
			{
				String b=l.substring(11);
				if(b.startsWith("@"))
				{
					b=b.substring(1);
					if(b.startsWith("V"))tmp.setBackground(VECfile.createBitmap(MainActivity.ctx,b.substring(1),Shape.p(900),Shape.p(900)));
					else if(b.startsWith("D"))tmp.setBackground(BitmapFactory.decodeStream(MainActivity.ctx.getAssets().open(b.substring(1))));
				}
				else
				{
					if(b.startsWith("V"))tmp.setBackground(VECfile.createBitmap(VECfile.readFile(MainActivity.mainDir+"地图/"+tmp.name+"/"+b.substring(1)),Shape.p(900),Shape.p(900)));
					else if(b.startsWith("D"))tmp.setBackground(BitmapFactory.decodeFile(MainActivity.mainDir+"地图/"+tmp.name+"/"+b.substring(1)));
				}
			}
			else if(l.startsWith("map:"))mapi=0;
			else if(l.startsWith("mapend"))mapi=-1;
			else if(mapi!=-1)tmp.fillData(mapi++,l);
		}
		return tmp;
	}
 /*
	public class AstarPoint
	{
		public int x,y;
		public int g,h;
		public AstarPoint parent;

		public AstarPoint(int x, int y, int g, int h, AstarPoint parent)
		{
			this.x = x;
			this.y = y;
			this.g = g;
			this.h = h;
			this.parent = parent;
		}
		public AstarPoint(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}*/
	public ArrayList<Point> findWayPoint()
	{
		ArrayList<Point> p=new ArrayList<Point>();

		/*ArrayList<AstarPoint> o=new ArrayList<AstarPoint>();
		ArrayList<AstarPoint> c=new ArrayList<AstarPoint>();
		AstarPoint fin=new AstarPoint(finish.x,finish.y);
		//把起点加入 open list 。
		o.add(new AstarPoint(start.x,start.y));
		//重复如下过程
		while(!containPoint(o,fin.x,fin.y))
		{
			AstarPoint np=null;
			int fmin=-1;
			//a. 遍历 open list ，查找 F 值最小的节点，把它作为当前要处理的节点。
			for(AstarPoint d:o)
			{
				int y=d.g+d.h;
				if(fmin==-1)fmin=y;
				else fmin=Math.min(fmin,y);
				if(y==fmin)np=d;
			}
			//b. 把这个节点移到 close list 。
			o.remove(np);
			c.add(np);

			AstarPoint gmin=null;
			//c.对当前方格的 8 个相邻方格的每一个方格？？
			for(int x=np.x-1;x<=np.x+1;x++)
				for(int y=np.y-1;y<=np.y+1;y++)
				{
					//◆如果它是不可抵达的或者它在 close list 中，忽略它
					if(x>=0&&x<size&&y>=0&&y<size&&map[x][y]!=0||containPoint(c,x,y))continue;
					//。否则，做如下操作。
					else
					{
						//◆如果它不在 open list 中，把它加入 open list ，
						//并且把当前方格设置为它的父亲，记录该方格的 F ， G 和 H 值。
						if(!containPoint(o,x,y))o.add(new AstarPoint(x,y,distancePoint(x,y,np),distancePoint(x,y,fin),np));
						//◆如果它已经在 open list 中，检查这条路径 ( 即经由当前方格到达它那里 ) 是否更好，
						//用 G 值作参考。更小的 G 值表示这是更好的路径。
						//如果是这样，把它的父亲设置为当前方格，并重新计算它的 G 和 F 值。
						//如果你的 open list 是按 F 值排序的话，改变后你可能需要重新排序。
						else
						{
							AstarPoint r=getPoint(o,x,y);
							if(gmin==null)gmin=r;
							else if(Math.min(gmin.g,r.g)==r.g)gmin=r;
						}
					}
				}
			gmin.parent=np;
			gmin.g=distancePoint(gmin.x,gmin.y,np);
		}
//
//d.停止，当你
//
//◆把终点加入到了 open list 中，此时路径已经找到了，或者
//
//◆查找终点失败，并且 open list 是空的，此时没有路径。
//
//3.保存路径。从终点开始，每个方格沿着父节点移动直至起点，这就是你的路径。
//
//
		*/
		return p;
	}
	/*
	int distancePoint(int x,int y,AstarPoint w)
	{
		return (int)(1000*Math.sqrt((x-w.x)*(x-w.x)+(y-w.y)*(y-w.y)));
	}
	boolean containPoint(ArrayList<AstarPoint> l,int x,int y)
	{
		boolean isinclose=false;
		for(AstarPoint h:l)
			if(h.x==x&&h.y==y)
			{
				isinclose=true;
				break;
			}
		return isinclose;
	}
	AstarPoint getPoint(ArrayList<AstarPoint> l,int x,int y)
	{
		for(AstarPoint h:l)
			if(h.x==x&&h.y==y)
			{
				return h;
			}
		return null;
	}*/
	private Bitmap tile(String name) throws Exception
	{
		int s=Shape.p(900)/size;
		return VECfile.createBitmap(MainActivity.ctx,"tiles/"+name,s,s);
	}
	private Bitmap tile(String name,float f) throws Exception
	{
		int s=(int)((float)Shape.p(900)*f/(float)size);
		return VECfile.createBitmap(MainActivity.ctx,"tiles/"+name,s,s);
	}
	public void setTileId(int id,Bitmap b)
	{
		tiles[id]=b;
	}
	public void setBackground(Bitmap background)
	{
		this.background = background;
	}

	public Bitmap getBackground()
	{
		return background;
	}
	public void setSize(int size)
	{
		this.size = size;
		map=new int[size][size];
	}

	public int getSize()
	{
		return size;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
	public void fillData(int y,String line)
	{
		if(line.length()!=size)return;
		for(int i=0;i<line.length();i++)
		{
			int id=Integer.parseInt(line.substring(i,i+1));
			map[i][y]=id;
			if(id==3)start=new Point(i,y);
			else if(id==4)finish=new Point(i,y);
		}
	}
	public class Node { 
		private int x; //x坐标 
		private int y; //y坐标 
		private String value;  //表示节点的值 
		private double FValue = 0; //F值 
		private double GValue = 0; //G值 
		private double HValue = 0; //H值 
		private boolean Reachable; //是否可到达（是否为障碍物） 
		private Node PNode;   //父节点 

		public Node(int x, int y, String value, boolean reachable) { 
			super(); 
			this.x = x; 
			this.y = y; 
			this.value = value; 
			Reachable = reachable; 
		} 

		public Node() { 
			super(); 
		} 

		public int getX() { 
			return x; 
		} 
		public void setX(int x) { 
			this.x = x; 
		} 
		public int getY() { 
			return y; 
		} 
		public void setY(int y) { 
			this.y = y; 
		} 
		public String getValue() { 
			return value; 
		} 
		public void setValue(String value) { 
			this.value = value; 
		} 
		public double getFValue() { 
			return FValue; 
		} 
		public void setFValue(double fValue) { 
			FValue = fValue; 
		} 
		public double getGValue() { 
			return GValue; 
		} 
		public void setGValue(double gValue) { 
			GValue = gValue; 
		} 
		public double getHValue() { 
			return HValue; 
		} 
		public void setHValue(double hValue) { 
			HValue = hValue; 
		} 
		public boolean isReachable() { 
			return Reachable; 
		} 
		public void setReachable(boolean reachable) { 
			Reachable = reachable; 
		} 
		public Node getPNode() { 
			return PNode; 
		} 
		public void setPNode(Node pNode) { 
			PNode = pNode; 
		}   
	} 
	public class Map2 {
		private Node[][] map;
		//节点数组 
		private Node startNode;
		//起点 
		private Node endNode;
		//终点 
		public Map2() {
			map = new Node[7][7];
			for (int i = 0;i<7;i++){
				for (int j = 0;j<7;j++){
					map[i][j] = new Node(i,j,"o",true);
				}
			}
			for (int d = 0;d<7;d++){
				map[0][d].setValue("%");
				map[0][d].setReachable(false);
				map[d][0].setValue("%");
				map[d][0].setReachable(false);
				map[6][d].setValue("%");
				map[6][d].setReachable(false);
				map[d][6].setValue("%");
				map[d][6].setReachable(false);
			}
			map[3][1].setValue("A");
			startNode = map[3][1];
			map[3][5].setValue("B");
			endNode = map[3][5];
			for (int k = 1;k<=3;k++){
				map[k+1][3].setValue("#");
				map[k+1][3].setReachable(false);
			}
		}
		//展示地图 
		public void ShowMap(){
			for (int i = 0;i<7;i++){
				for (int j = 0;j<7;j++){
					System.out.print(map[i][j].getValue()+" ");
				}
				System.out.println("");
			}
		}
		public Node[][] getMap() {
			return map;
		}
		public void setMap(Node[][] map) {
			this.map = map;
		}
		public Node getStartNode() {
			return startNode;
		}
		public void setStartNode(Node startNode) {
			this.startNode = startNode;
		}
		public Node getEndNode() {
			return endNode;
		}
		public void setEndNode(Node endNode) {
			this.endNode = endNode;
		}
	}
	public class AStar {
		/** 
		 * 使用ArrayList数组作为“开启列表”和“关闭列表” 
		 */
		ArrayList<Node> open = new ArrayList<Node>();
		ArrayList<Node> close = new ArrayList<Node>();
		/** 
		 * 获取H值 
		 * @param currentNode：当前节点 
		 * @param endNode：终点 
		 * @return 
		 */
		public double getHValue(Node currentNode,Node endNode){
			return (Math.abs(currentNode.getX() - endNode.getX()) + Math.abs(currentNode.getY() - endNode.getY()))*10;
		}
		/** 
		 * 获取G值 
		 * @param currentNode：当前节点 
		 * @return 
		 */
		public double getGValue(Node currentNode){
			if(currentNode.getPNode()!=null){
				if(currentNode.getX()==currentNode.getPNode().getX()||currentNode.getY()==currentNode.getPNode().getY()){
					//判断当前节点与其父节点之间的位置关系（水平？对角线） 
					return currentNode.getGValue()+10;
				}
				return currentNode.getGValue()+14;
			}
			return currentNode.getGValue();
		}
		/** 
		 * 获取F值 ： G + H 
		 * @param currentNode 
		 * @return 
		 */
		public double getFValue(Node currentNode){
			return currentNode.getGValue()+currentNode.getHValue();
		}
		/** 
		 * 将选中节点周围的节点添加进“开启列表” 
		 * @param node 
		 * @param map 
		 */
		public void inOpen(Node node,Map2 map){
			int x = node.getX();
			int y = node.getY();
			for (int i = 0;i<3;i++){
				for (int j = 0;j<3;j++){
					//判断条件为：节点为可到达的（即不是障碍物，不在关闭列表中），开启列表中不包含，不是选中节点 
					if(map.getMap()[x-1+i][y-1+j].isReachable()&&!open.contains(map.getMap()[x-1+i][y-1+j])&&!(x==(x-1+i)&&y==(y-1+j))){
						map.getMap()[x-1+i][y-1+j].setPNode(map.getMap()[x][y]);
						//将选中节点作为父节点 
						map.getMap()[x-1+i][y-1+j].setGValue(getGValue(map.getMap()[x-1+i][y-1+j]));
						map.getMap()[x-1+i][y-1+j].setHValue(getHValue(map.getMap()[x-1+i][y-1+j],map.getEndNode()));
						map.getMap()[x-1+i][y-1+j].setFValue(getFValue(map.getMap()[x-1+i][y-1+j]));
						open.add(map.getMap()[x-1+i][y-1+j]);
					}
				}
			}
		}
		/** 
		 * 使用冒泡排序将开启列表中的节点按F值从小到大排序 
		 * @param arr 
		 */
		public void sort(ArrayList<Node> arr){
			for (int i = 0;i<arr.size()-1;i++){
				for (int j = i+1;j<arr.size();j++){
					if(arr.get(i).getFValue() > arr.get(j).getFValue()){
						Node tmp = new Node();
						tmp = arr.get(i);
						arr.set(i, arr.get(j));
						arr.set(j, tmp);
					}
				}
			}
		}
		/** 
		 * 将节点添加进”关闭列表“ 
		 * @param node 
		 * @param open 
		 */
		public void inClose(Node node,ArrayList<Node> open){
			if(open.contains(node)){
				node.setReachable(false);
				//设置为不可达 
				open.remove(node);
				close.add(node);
			}
		}
		public void search(Map2 map){
			//对起点即起点周围的节点进行操作 
			inOpen(map.getMap()[map.getStartNode().getX()][map.getStartNode().getY()],map);
			close.add(map.getMap()[map.getStartNode().getX()][map.getStartNode().getY()]);
			map.getMap()[map.getStartNode().getX()][map.getStartNode().getY()].setReachable(false);
			map.getMap()[map.getStartNode().getX()][map.getStartNode().getY()].setPNode(map.getMap()[map.getStartNode().getX()][map.getStartNode().getY()]);
			sort(open);
			//重复步骤 
			do{
				inOpen(open.get(0), map);
				inClose(open.get(0), open);
				sort(open);
			}
			while(!open.contains(map.getMap()[map.getEndNode().getX()][map.getEndNode().getY()]));
			//知道开启列表中包含终点时，循环退出 
			inClose(map.getMap()[map.getEndNode().getX()][map.getEndNode().getY()], open);
			showPath(close,map);
		}
		/** 
		 * 将路径标记出来 
		 * @param arr 
		 * @param map 
		 */
		public void showPath(ArrayList<Node> arr,Map2 map) {
			if(arr.size()>0){
				Node node = new Node();
				//node = map.getMap()[map.getEndNode().getX()][map.getEndNode().getY()]; 
				//while(!(node.getX() ==map.getStartNode().getX()&&node.getY() ==map.getStartNode().getY())){ 
				//node.getPNode().setValue("*"); 
				//node = node.getPNode(); 
				//<span style="white-space:pre">  </span>}
			}
			//<span style="white-space:pre">  </span>map.getMap()[map.getStartNode().getX()][map.getStartNode().getY()].setValue("A");
		}
	}
}
