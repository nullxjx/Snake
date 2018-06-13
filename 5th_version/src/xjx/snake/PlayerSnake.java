package xjx.snake;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.util.LinkedList;
import java.awt.Image;
import java.util.Deque;
import java.util.Iterator;

import xjx.game.*;

public class PlayerSnake {
	private MainWindow GameUI;//母窗体,即游戏主界面
	private Foodset food;
	private Obstacle obstacle;
	/*
	 * 这里说明一下，由于游戏的主界面类时MainWindow，而PlayerSnake类需要和MainWindow交换一些信息，即两个类有通信需求，
	 * 故需要把MainWindow类作为PlayerSnake构造函数的参数以达到能访问其中变量和函数的目的
	 */
	private Thread run;
	private Direction direction = Direction.RIGHT;//蛇当前前进的方向,初始化默认向右移动
	private int speed = 300;//用于描述蛇移动速度的变量，其实是作为蛇刷新线程的时间用的
	private int defaultSpeed = 300;//默认速度
	private Deque<Body> body = new LinkedList<Body>();//用于描述蛇身体节点的数组，保存蛇身体各个节点的坐标
	private int point = 0;//当前蛇得了多少分
	private int bulletNumber = 20;//蛇的子弹数目
	
	private ImageIcon[] headIcon = new ImageIcon[4];//表示蛇头的四张图片
	private int headIconTag = 0;//头部默认加载第0张图片
	private ImageIcon[] bodyIcon = new ImageIcon[4];//表示蛇头的四张图片
	private int bodyIconTag = 0;//身体默认加载第0张图片
	private boolean quit = false;
	
	public PlayerSnake(MainWindow GameUI,Foodset food,Obstacle obstacle){
		this.GameUI = GameUI;
		this.food = food;
		this.obstacle = obstacle;
		//加载4张蛇头和4张蛇身体图片
		for(int i = 0;i < 4;i++)
		{
			headIcon[i] = new ImageIcon("head//head" + i + ".png");
			headIcon[i].setImage(headIcon[i].getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));//保持图片的清晰
			
			bodyIcon[i] = new ImageIcon("body//body" + i + ".png");
			bodyIcon[i].setImage(bodyIcon[i].getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));//保持图片的清晰
		}
		
		Body head = new Body(0,0,headIcon[headIconTag]);//初始化头部在(0,0)位置
		body.addFirst(head);
		GameUI.add(head.label);
		head.label.setBounds(IndexToPixel.getXPixel(head.coor.x), IndexToPixel.getYPixel(head.coor.y), 20, 20);

		GameUI.setMap(0, 0, 1);
		MoveThread();
	}
	
	//蛇身体移动
	public void move(){
		Coordinate head,next_coor = new Coordinate(0,0);
		if(direction == Direction.UP){
			head = body.getFirst().coor;//获取头部
			next_coor = new Coordinate(head.x,head.y - 1);//头部向上移动一个单位后的坐标
		}
		else if(direction == Direction.DOWN){
			head = body.getFirst().coor;//获取头部
			next_coor = new Coordinate(head.x,head.y + 1);//头部向下移动一个单位后的坐标
		}
		else if(direction == Direction.LEFT){
			head = body.getFirst().coor;//获取头部
			next_coor = new Coordinate(head.x - 1,head.y);//头部向左移动一个单位后的坐标
		}
		else if(direction == Direction.RIGHT){
			head = body.getFirst().coor;//获取头部
			next_coor = new Coordinate(head.x + 1,head.y);//头部向右移动一个单位后的坐标
		}
		
		if(checkDeath(next_coor))//判断下一步是否死亡
		{
			new Music("music//over.wav").start();
			GameUI.setIsrun(false);
			quit();
			
			
			int result=JOptionPane.showConfirmDialog(null, "Game over! Try again?", 
					"Information", JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.YES_NO_OPTION)
			{
				GameUI.restart();
			}
			else
			{
				GameUI.setPause(true);
			}
		}
		else
		{
			Body next_node = new Body(next_coor,headIcon[headIconTag]);
			body.addFirst(next_node);//添头
			//GameUI.map[next_node.coor.y][next_node.coor.x] = 1;//标记为蛇身体节点
			GameUI.setMap(next_node.coor.y, next_node.coor.x, 1);
			next_node.label.setVisible(true);
			GameUI.add(next_node.label);
			
			if(!checkEat(next_coor))//没吃到食物就去尾，否则不用去掉，因为添加的头刚好是吃到一个食物后增长的一节
			{
				Body tail = body.pollLast();//去尾
				//GameUI.map[tail.coor.y][tail.coor.x] = 0;//标记为空闲
				GameUI.setMap(tail.coor.y, tail.coor.x, 0);
				tail.label.setVisible(false);
				GameUI.remove(tail.label);
				//添头去尾实现移动
			}
		}
	}
	
	//判断一个坐标位置是否是蛇死亡的位置
	public boolean checkDeath(Coordinate coor){
		/*注意，coor.x是map数组中的列号，从左到右依次为[0,WIDTH-1]
		 *     coor.y是map数组中的行号，从上到下依次为[0,HEIGHT-1]
		 */
		
		if( coor.x < 0 || coor.x >= GameUI.getAreaWidth()||
			coor.y < 0 || coor.y >= GameUI.getAreaHeight()||
			GameUI.getMap()[coor.y][coor.x] == 3)
			//GameUI.map[coor.y][coor.x] == 3)
			return true;
		else
			return false;
	}
	
	public boolean checkEat(Coordinate coor){
		int _point = food.getFoodPoint(coor);
		if(_point == -1)//没吃到食物
			return false;
		else//吃到了食物
		{
			new Music("music//eat.wav").start();
			point += _point;
			if(_point == 0)//食物是子弹
			{
				bulletNumber ++;
				GameUI.getWeaponLabel().setText("" + bulletNumber);
			}
			GameUI.getScoreLabel().setText("" + point);//在界面上刷新得分
			GameUI.getLengthLabel().setText("" + body.size());//刷新身体长度
			//GameUI.map[coor.y][coor.x] = 1;//把地图上的该点标记为1，代表蛇身体
			GameUI.setMap(coor.y, coor.x, 1);
			return true;
		}
	}
	
	public void quit(){
		quit = true;
	}
	
	public void setDirection(Direction direction){
		this.direction = direction;
	}
	
	public Direction getDirection(){
		return direction;
	}
	
	public void setSpeed(int speed){
		this.speed = speed;
	}
	
	public void resetSpeed(){
		this.speed = defaultSpeed;
	}
	
	public void setDefaultSpeed(int speed){
		this.defaultSpeed = speed;
	}
	
	public void setHeadIcon(int tag){
		headIconTag = tag;
	}
	
	public void setBodyIcon(int tag){
		bodyIconTag = tag;
	}
	
	public int getBulletNum(){
		return bulletNumber;
	}
	
	public Coordinate getHeadCoor(){
		return body.getFirst().coor;
	}
	
	public synchronized void goDie(){
		quit();
		for (Iterator<Body> iter = body.iterator(); iter.hasNext();) {
			Body node = iter.next();
			node.label.setVisible(false);
			GameUI.remove(node.label);
		}
	}
	
	public void fire(Coordinate snakehead,Coordinate target,Direction d){
		new Fire(snakehead,target,d);
		bulletNumber--;
		GameUI.getWeaponLabel().setText("" + bulletNumber);//刷新界面上显示的子弹数目
	}
	
	public synchronized void show(){
		for (Iterator<Body> iter = body.iterator(); iter.hasNext();) {
			Body node = iter.next();
			node.label.setBounds(IndexToPixel.getXPixel(node.coor.x), 
					IndexToPixel.getYPixel(node.coor.y), 20, 20);
			node.label.setIcon(bodyIcon[bodyIconTag]);
		}
		Body node = body.getFirst();
		node.label.setIcon(headIcon[headIconTag]);
	}
	
	public void MoveThread(){
		run = new Thread() {
			public void run() {
				while (!quit) 
				{
					try {
						Thread.sleep(speed);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
					
					if(!GameUI.getPause() && GameUI.getIsrun())
					{	
						move();
						Write2file.PrintMap(GameUI.getMap(),"map.txt");
						if(quit)
							break;
						show();
					}
				}
				System.out.println("Player thread exit...");
			}
		};
		run.start();
	}
	
	public class Fire extends Thread{
		private Coordinate fireCoor;
		private ImageIcon fireIcon;
		private JLabel fireLabel;
		private Coordinate target;
		private boolean quit = false;
		private Direction moveDirection;
		private Coordinate snakehead;
		
		public Fire(Coordinate snakehead,Coordinate target,Direction d){
			fireIcon = new ImageIcon("image//fire.png");//射击子弹时产生的火焰图标
		    fireIcon.setImage(fireIcon.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));//保持图片的清晰
		    fireLabel = new JLabel(fireIcon);
		    
		    this.target = target;
		    this.moveDirection = d;
		    this.snakehead = snakehead;
		    //初始化火焰起始坐标
		    if(moveDirection == Direction.UP)
			{
				fireCoor = new Coordinate(snakehead.x,snakehead.y-1);
				/* 这里插一句
				 * 草他妈的Java，引用传递害死人，这里的snakehead从最开始传过来的都是引用，
				 * 也就是说各个形参和实参都和最初的那个对象绑定在一起了，
				 * 任何一处修改，都会直接影响到最原来那个对象的值，也就是body数组的值
				 * 所以这里千万不能有任何修改原值的行为，比如此处只能用snakehead.y-1，不能用snakehead.y--!!!
				 * 忘后人记住这一点，他娘的，浪费我这么久时间
				 * 详情请参考：https://www.zhihu.com/question/20628016/answer/28970414
				 */
				
			}
			else if(moveDirection == Direction.DOWN)
			{
				fireCoor = new Coordinate(snakehead.x,snakehead.y+1);
			}
			else if(moveDirection == Direction.LEFT)
			{
				fireCoor = new Coordinate(snakehead.x-1,snakehead.y);
			}
			else if(moveDirection == Direction.RIGHT)
			{
				fireCoor = new Coordinate(snakehead.x+1,snakehead.y);
			}
		    
		    GameUI.add(fireLabel);
		    show();
		    
		    this.start();
		}
		
		public void show(){
			if(fireCoor.x == target.x && fireCoor.y == target.y)
			{
				if(target.x < 0 || target.x > GameUI.getAreaWidth() || target.y < 0 || target.y > GameUI.getAreaHeight()){}//运动到屏幕外不播放音效
				else new Music("music//explode.wav").start();//击中障碍物播放音效
				
				fireLabel.setVisible(false);
				obstacle.removeOne(target);
				
				GameUI.remove(fireLabel);
				quit = true;
			}
			fireLabel.setVisible(false);
			fireLabel.setBounds(IndexToPixel.getXPixel(fireCoor.x), 
					IndexToPixel.getYPixel(fireCoor.y), 20, 20);
			fireLabel.setVisible(true);
		}
		
		public void move(){
			if(moveDirection == Direction.UP)
			{
				fireCoor.y--;
			}
			else if(moveDirection == Direction.DOWN)
			{
				fireCoor.y++;
			}
			else if(moveDirection == Direction.LEFT)
			{
				fireCoor.x--;
			}
			else if(moveDirection == Direction.RIGHT)
			{
				fireCoor.x++;
			}
		}
		
		public void run(){
			while(!quit)
			{
				try {
					Thread.sleep(50);//每隔50毫秒刷新一次
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				move();
				if(quit)
					return;
				show();
			}
		}
	}
}

