package xjx.game;

import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import java.util.Random;

import javax.swing.*;
import xjx.snake.*;

public class MainWindow extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1877974685325498861L;
	private Font f = new Font("微软雅黑",Font.PLAIN,15);
	private Font f2 = new Font("微软雅黑",Font.PLAIN,12);
	private JRadioButtonMenuItem speedItems[];
	private JRadioButtonMenuItem headItems[]; 
	private JRadioButtonMenuItem bodyItems[];
	private ButtonGroup speedGroup;
	private ButtonGroup headGroup;
	private ButtonGroup bodyGroup;
	private ImageIcon backgroundImage;
	private JLabel background_label;
	private JPanel imagePanel;
	private JPanel paintPanel;//画板，画线条用的
	private JLabel label  = new JLabel("当前长度：");
	private JLabel label2 = new JLabel("所花时间：");
	private JLabel label3 = new JLabel("当前得分：");
	private JLabel label4 = new JLabel("食物个数：");
	private JLabel label5 = new JLabel("剩余子弹：");
	private JLabel label6 = new JLabel("AI长度：");
	private JLabel label7 = new JLabel("食物坐标：");
	private JLabel label8 = new JLabel("下一步：");
	private JLabel FoodCoor = new JLabel("");
	private JLabel NextStepCoor = new JLabel("");
	private JLabel AILength = new JLabel("1");
	private JLabel Length = new JLabel("1");
	private JLabel Score = new JLabel("0");
	private JLabel Time = new JLabel("");
	private JLabel Amount = new JLabel("0");
	private JLabel Weapon = new JLabel("0");
	private JPanel p = new JPanel();
	private Timer timer;
	private boolean pause = false;
	private boolean isrun = true;
	private boolean IfSpeedUp = false;//标记是否加速,true表示当前在加速
	private boolean IfRemove = false;//标记是否移除界面上的网格线，true表示移除，false表示不移除，默认不移除
	
	private PlayerSnake snake;
	private Foodset food;
	private Obstacle obstacle;
	
	private static final int HEIGHT = 21;			//游戏区域高，高21代表纵向有21行
	private static final int WIDTH = 40;			//游戏区域宽，宽40代表横向有40列
	private int[][] map = new int[HEIGHT][WIDTH];	//map数组标记当前地图的使用情况
													/*0表示空闲
													 *1表示蛇身体节点
													 *2表示食物
													 *3表示障碍物 
													 */

	public synchronized int[][] getMap(){
		return map;
	}
	
	public synchronized void setMap(int i,int j,int e){
		map[i][j] = e;
	}
	
	public JLabel getFoodCoorLabel(){
		return FoodCoor;
	}
	
	public JLabel getNextStepCoorLabel(){
		return NextStepCoor;
	}
	
	public JLabel getAILengthLabel(){
		return AILength;
	}
	
	public JLabel getLengthLabel(){
		return Length;
	}
	
	public JLabel getScoreLabel(){
		return Score;
	}
	
	public JLabel getTimeLabel(){
		return Time;
	}
	
	public JLabel getAmountLabel(){
		return Amount;
	}
	
	public JLabel getWeaponLabel(){
		return Weapon;
	}
	
	public boolean getIsrun(){
		return isrun;
	}
	
	public void setIsrun(boolean tag){
		isrun = tag;
	}
	
	public boolean getPause(){
		return pause;
	}
	
	public void setPause(boolean tag){
		pause = tag;
	}
	
	public int getAreaHeight(){
		return HEIGHT;
	}
	
	public int getAreaWidth(){
		return WIDTH;
	}
	
	public void resetTimer(){
		timer.reset();
	}

	public void resetLabel(){
		FoodCoor.setText("");
		NextStepCoor.setText("");
		AILength.setText("1");
		Length.setText("1");
		Score.setText("0");
		Time.setText("");
		Amount.setText("0");
		Weapon.setText("20");
	}
	
	public void restart(){//重新开始游戏
		isrun = false;
		snake.quit();
		food.quit();
		obstacle.quit();
		
		System.out.println();
		System.out.println();
		System.out.println("Game Restarting......");
		System.out.println();
		System.out.println();
		
		resetLabel();
		
		//初始化map数组全为0
		for(int i = 0;i < HEIGHT;i++)
			for(int j = 0;j < WIDTH;j++)
				setMap(i,j,0);		
		
		speedItems[2].setSelected(true);
		headItems[0].setSelected(true);
		bodyItems[0].setSelected(true);
		
		food.removeAll();
		food = new Foodset(this);
		
		obstacle.removeAll();
		obstacle = new Obstacle(this);
		
		snake.goDie();
		snake = new PlayerSnake(this,food,obstacle);

		resetTimer();//重置计时器
		
		isrun = true;
		pause = false;
		IfSpeedUp = false;
	}
	
	public Coordinate produceRandomCoordinate(){
		Random rand = new Random();
		Coordinate res;
		int x = rand.nextInt(HEIGHT);//[0,20],共21行，序号从0开始，此处x为选中的行号
		int y = rand.nextInt(WIDTH); //[0,39],共40列，序号从0开始，此处y为选中的列号
		
		while(true)
		{
			//P();
			//保证身体节点，食物，障碍物都不能和该坐标重合
			if(getMap()[x][y] != 0 || x <= 5 || y <= 10)
			{
				x = rand.nextInt(HEIGHT);//[0,20],共21行，序号从0开始
				y = rand.nextInt(WIDTH);//[0,39],共40列，序号从0开始
				
				//V();
				continue;
			}
			else
				break;
		}
		res = new Coordinate(x,y);
		return res;
	}
	
	public void run(){
		//初始化map数组全为0
		for(int i = 0;i < HEIGHT;i++)
			for(int j = 0;j < WIDTH;j++)
				map[i][j] = 0;
		
		System.out.println();
		System.out.println();
		System.out.println("Game starting......");
		System.out.println();
		System.out.println();
		
		food = new Foodset(this);
		obstacle = new Obstacle(this);
        snake = new PlayerSnake(this,food,obstacle);
		
		Write2file.PrintMap(getMap(),"map.txt");
        timer = new Timer();
	}
	
	@SuppressWarnings("serial")
	public void InitialUI(){
		//--------------------------------界面部分-----------------------------------
		String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		Image img = Toolkit.getDefaultToolkit().getImage("image//title.png");//窗口图标
		setIconImage(img);
	    setTitle("Snake_5th_version_beta");
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setSize(1000,540);
	    setResizable(false);
	    setLayout(null);
	    setLocationRelativeTo(null);
	    
	    //添加背景图片
	    backgroundImage = new ImageIcon("background//sky2.jpg");
	    backgroundImage.setImage(backgroundImage.getImage().getScaledInstance(1000,540,Image.SCALE_SMOOTH));
	    background_label = new JLabel(backgroundImage);  
	    background_label.setBounds(0,0, this.getWidth(), this.getHeight());   
        this.getLayeredPane().add(background_label, new Integer(Integer.MIN_VALUE));
        
        imagePanel = (JPanel) this.getContentPane();  
        imagePanel.setOpaque(false);
        
        paintPanel = new JPanel(){
        	//绘制界面的函数
        	public void paint(Graphics g1){
        		super.paint(g1);
        		Graphics2D g = (Graphics2D) g1;
        		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_NORMALIZE);
        		
        		//墙
        		g.setPaint(new GradientPaint(115,135,Color.CYAN,230,135,Color.MAGENTA,true));
        		g.setStroke( new BasicStroke(4,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL));
        		g.drawRect(3, 6, 887, 468);//+400
        		
        		if(!IfRemove)
        		{
        			//网格线
        			for(int i = 1;i < 40;i++)
        			{
        				g.setStroke( new BasicStroke(1f, BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_ROUND, 3.5f, new float[] { 15, 10, },
                                0f));//虚线
        				g.setColor(Color.black);
        				g.drawLine(5+i*22,9,5+i*22,472);//画竖线，共39根竖线，40列
        				if(i <= 20)
        				{
        					g.drawLine(4,10+i*22,887,10+i*22);//画横线，共20根横线，21行
        				}
        			}
        		}
        	}
        };
        paintPanel.setOpaque(false);
        paintPanel.setBounds(0, 0, 900, 480);
        add(paintPanel);
        
        //------------------------------------------------------------------
        //布局
        add(label);
 		label.setBounds(900, 10, 80, 20);
	    label.setFont(f);
	    add(Length);
	    Length.setBounds(900, 35, 80, 20);
	    Length.setFont(f);
	    add(label2);
	    label2.setBounds(900, 70, 80, 20);
	    label2.setFont(f);
	    add(Time);
	    Time.setBounds(900, 95, 80, 20);
	    Time.setFont(f);    
	    add(label3);
	    label3.setBounds(900, 130, 80, 20);
	    label3.setFont(f);
	    add(Score);
	    Score.setBounds(900, 155, 80, 20);
	    Score.setFont(f);
	    add(label4);
	    label4.setBounds(900, 190, 80, 20);
	    label4.setFont(f);
	    add(Amount);
	    Amount.setBounds(900, 215, 80, 20);
	    Amount.setFont(f);
	    add(label5);
	    label5.setBounds(900, 250, 80, 20);
	    label5.setFont(f);
	    add(Weapon);
	    Weapon.setBounds(900, 275, 80, 20);
	    Weapon.setFont(f);
	    
	    add(p);
	    p.setBounds(898, 300, 93, 1);
	    p.setBorder(BorderFactory.createLineBorder(Color.white));
	    
	    add(label6);
	    label6.setBounds(900, 315, 80, 20);
	    label6.setFont(f);
	    add(AILength);
	    AILength.setBounds(900, 340, 80, 20);
	    AILength.setFont(f);
	    
	    add(label7);
	    label7.setBounds(900, 365, 80, 20);
	    label7.setFont(f);
	    add(FoodCoor);
	    FoodCoor.setBounds(900, 390, 80, 20);
	    FoodCoor.setFont(f);
	    
	    add(label8);
	    label8.setBounds(900, 415, 80, 20);
	    label8.setFont(f);
	    add(NextStepCoor);
	    NextStepCoor.setBounds(900, 440, 80, 20);
	    NextStepCoor.setFont(f);
	    
	    //字体颜色，为了便于分辨，设为白色
	    label.setForeground(Color.white);
	    label2.setForeground(Color.white);
	    label3.setForeground(Color.white);
	    label4.setForeground(Color.white);
	    label5.setForeground(Color.white);
	    label6.setForeground(Color.white);
	    label7.setForeground(Color.white);
	    label8.setForeground(Color.white);
	    FoodCoor.setForeground(Color.white);
	    NextStepCoor.setForeground(Color.white);
	    AILength.setForeground(Color.white);
	    Length.setForeground(Color.white);
		Score.setForeground(Color.white);
		Time.setForeground(Color.white);
		Amount.setForeground(Color.white);    
		Weapon.setForeground(Color.white);
		
		//菜单栏
        JMenuBar bar = new JMenuBar();
        bar.setBackground(Color.white);
  		setJMenuBar(bar);
  		JMenu Settings = new JMenu("设置");
  		Settings.setFont(f);
  		JMenu Help = new JMenu("帮助");
  		Help.setFont(f);
  		JMenu About = new JMenu("关于");
  		About.setFont(f);
  		bar.add(Settings);
  		bar.add(Help);
  		bar.add(About);	
      		
  		JMenuItem set_background = new JMenuItem("更换背景");
  		set_background.setFont(f2);
		JMenu set_head = new JMenu("更换蛇头");
		set_head.setFont(f2);
		JMenu set_body = new JMenu("更换蛇身");
		set_body.setFont(f2);
		JMenu set_speed= new JMenu("设置速度");
		set_speed.setFont(f2);
		JMenuItem remove_net= new JMenuItem("移除网格");
		remove_net.setFont(f2);
		Settings.add(set_background);
		Settings.add(set_head);
		Settings.add(set_body);
		Settings.add(set_speed);
		Settings.add(remove_net);
		
		JMenuItem help = new JMenuItem("Guide...");
		help.setFont(f2);
		Help.add(help);
		
		JMenuItem about = new JMenuItem("About...");
		about.setFont(f2);
		About.add(about);
		
		
		setFocusable(true);
		setVisible(true);
       
		//---------------------------------------------------------------------
		
		//添加监听器
		//监听键盘事件
		this.addKeyListener(new MyKeyListener());
		
		remove_net.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		if(!IfRemove)
        		{
        			IfRemove = true;
        			remove_net.setText("显示网格");
        			paintPanel.repaint();
        		}
        		else
        		{
        			IfRemove = false;
        			remove_net.setText("移除网格");
        			paintPanel.repaint();
        		}
        	}
        });
		
		String speed[] = {"龟速","行走","奔跑","疯狂"};
		speedItems = new JRadioButtonMenuItem[speed.length];
		speedGroup = new ButtonGroup();
		for(int i = 0;i < speed.length;i++)
		{
			speedItems[i] = new JRadioButtonMenuItem(speed[i]);
			speedItems[i].setFont(f2);
			set_speed.add(speedItems[i]);
			speedGroup.add(speedItems[i]);
			speedItems[i].addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent e){
							for(int i = 0;i < speedItems.length;i++)
							{
								if(speedItems[i].isSelected())
								{
									if(i == 0)
									{
										snake.setDefaultSpeed(600);
										snake.resetSpeed();
									}
									else if(i == 1)
									{
										snake.setDefaultSpeed(400);
										snake.resetSpeed();
									}
									else if(i == 2)
									{
										snake.setDefaultSpeed(300);
										snake.resetSpeed();
									}
									else if(i == 3)
									{
										snake.setDefaultSpeed(150);
										snake.resetSpeed();
									}
								}
							}
						}
					}
			);
		}
		speedItems[2].setSelected(true);
		
		String head[] = {"doge","二哈","经典","憧憬"};
		headItems = new JRadioButtonMenuItem[head.length];
		headGroup = new ButtonGroup();
		ImageIcon headIcon[] = new ImageIcon[head.length];
		headIcon[0] = new ImageIcon("head//head0.png");
		headIcon[1] = new ImageIcon("head//head1.png");
		headIcon[2] = new ImageIcon("head//head2.png");
		headIcon[3] = new ImageIcon("head//head3.png");
		headIcon[0].setImage(headIcon[0].getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));//保持图片的清晰
		headIcon[1].setImage(headIcon[1].getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));//保持图片的清晰
		headIcon[2].setImage(headIcon[2].getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));//保持图片的清晰
		headIcon[3].setImage(headIcon[3].getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));//保持图片的清晰
		for(int i = 0;i < head.length;i++)
		{
			headItems[i] = new JRadioButtonMenuItem(head[i]);
			headItems[i].setFont(f2);
			headItems[i].setIcon(headIcon[i]);
			set_head.add(headItems[i]);
			headGroup.add(headItems[i]);
			headItems[i].addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent e){
							for(int i = 0;i < headItems.length;i++)
							{
								if(headItems[i].isSelected())
								{
									snake.setHeadIcon(i);
								}
							}
						}
					}
			);
		}
		headItems[0].setSelected(true);
		
		String body[] = {"乖巧","笑眼","滑稽","阴险"};
		bodyItems = new JRadioButtonMenuItem[body.length];
		bodyGroup = new ButtonGroup();
		ImageIcon bodyIcon[] = new ImageIcon[body.length];
		bodyIcon[0] = new ImageIcon("body//body0.png");
		bodyIcon[1] = new ImageIcon("body//body1.png");
		bodyIcon[2] = new ImageIcon("body//body2.png");
		bodyIcon[3] = new ImageIcon("body//body3.png");
		bodyIcon[0].setImage(bodyIcon[0].getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));//保持图片的清晰
		bodyIcon[1].setImage(bodyIcon[1].getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));//保持图片的清晰
		bodyIcon[2].setImage(bodyIcon[2].getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));//保持图片的清晰
		bodyIcon[3].setImage(bodyIcon[3].getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));//保持图片的清晰
		for(int i = 0;i < body.length;i++)
		{
			bodyItems[i] = new JRadioButtonMenuItem(body[i]);
			bodyItems[i].setFont(f2);
			bodyItems[i].setIcon(bodyIcon[i]);
			set_body.add(bodyItems[i]);
			bodyGroup.add(bodyItems[i]);
			bodyItems[i].addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent e){
							for(int i = 0;i < bodyItems.length;i++)
							{
								if(bodyItems[i].isSelected())
								{
									snake.setBodyIcon(i);
								}
							}
						}
					}
			);
		}
		bodyItems[0].setSelected(true);
		
		set_background.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		new AlterBacground();
        	}
        });
		
		about.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		new About();
        	}
        });
		
		help.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		new Help();
        	}
        });
	}
	
	//主函数入口
	public static void main(String[] args) {
		MainWindow game = new MainWindow();
		game.InitialUI();//初始化界面
		game.run();
	}
	
	
	/*
	 * 计时器类,负责计时
	 * 调用方法，直接new一个此类，然后主界面就开始显示计时
	 * new Timer();
	 */
	private class Timer extends Thread{
		
		private int hour = 0;
		private int min = 0;
		private int sec = 0;
		
		public Timer(){
			this.start();
	    }
		
		public void reset()
		{
			hour = 0;
			min = 0;
			sec = 0;
		}
		
		@Override
	    public void run() {
	        while(true){
	            if(isrun){
	                sec +=1 ;
	                if(sec >= 60){
	                    sec = 0;
	                    min +=1 ;
	                }
	                if(min>=60){
	                    min=0;
	                    hour+=1;
	                }
	                showTime();
	            }
	 
	            try {
	                Thread.sleep(1000);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	             
	        }
	    }

		private void showTime(){
	        String strTime ="" ;
	        if(hour < 10)
	            strTime = "0"+hour+":";
	        else
	            strTime = ""+hour+":";
	         
	        if(min < 10)
	            strTime = strTime+"0"+min+":";
	        else
	            strTime =strTime+ ""+min+":";
	         
	        if(sec < 10)
	            strTime = strTime+"0"+sec;
	        else
	            strTime = strTime+""+sec;
	         
	        //在窗体上设置显示时间
	        Time.setText(strTime);
	    }
	}

	private class MyKeyListener implements KeyListener{
		private Calendar Cld;
		private int MI,MI3;
		private int SS,SS3;

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			Direction direction = snake.getDirection();
    		if(key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D)//向右
    		{
    			if(isrun && direction != Direction.LEFT)
    			{
    				snake.setDirection(Direction.RIGHT);
    			}
    		}
    		else if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A)//向左
    		{
    			if(isrun && direction != Direction.RIGHT)
    			{
    				snake.setDirection(Direction.LEFT);
    			}
    		}
    		else if(key == KeyEvent.VK_UP || key == KeyEvent.VK_W)//向上
    		{
    			if(isrun && direction != Direction.DOWN)
    			{
    				snake.setDirection(Direction.UP);
    			}
    		}
    		else if(key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S)//向下
    		{
    			if(isrun && direction != Direction.UP)
    			{
    				snake.setDirection(Direction.DOWN);
    			}
    		}
    		else if(key == KeyEvent.VK_ESCAPE)//重新开始
    		{
    			restart();
    		}
    		else if(key == KeyEvent.VK_SPACE)
    		{
    			if(!pause)//暂停
    			{
    				pause = true;
    				isrun = false;
    				System.out.println("暂停...");
    			}
    			else//开始
    			{
    				pause = false;
    				isrun = true;
    				System.out.println("开始...");
    			}
    		}
    		
//    		//发射子弹
    		if(e.isShiftDown())
    		{
    			if(snake.getBulletNum() > 0)
    			{
    				System.out.println("Fire a bullet");
    				Direction d = snake.getDirection();
    				Coordinate coor = snake.getHeadCoor();
    				Coordinate target = obstacle.searchTarget(coor, d);//找到火焰的目标
    				
    				System.out.println("Target is:" + target.x + "," +target.y);
    				
    				snake.fire(coor,target,d);
    			}
    		}
			
    		if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN ||
    				key == KeyEvent.VK_A || key == KeyEvent.VK_D || key == KeyEvent.VK_W || key == KeyEvent.VK_S)
    		{
    			if(!IfSpeedUp)    	        
	    	        IfSpeedUp = true;
    	
    			Cld = Calendar.getInstance();
    			SS3 = Cld.get(Calendar.SECOND);
    	        MI3 = Cld.get(Calendar.MILLISECOND); 
    	        int x = SS3 * 1000 + MI3 - ( SS * 1000 + MI);
    	        if(x > 500)//按一个按钮的时长大于500毫秒识别为长按
    	        {
    	        	snake.setSpeed(100);//加速时每隔100毫秒刷新一次
    	        	//System.out.println("Long Pressed");
    	        }
    		}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			int key = e.getKeyCode();
    		if( key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN ||
    			key == KeyEvent.VK_A || key == KeyEvent.VK_D || key == KeyEvent.VK_W || key == KeyEvent.VK_S)
    		{
    	        IfSpeedUp = false;
    	        snake.resetSpeed();
    		}
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
		}
	}
	
	private class AlterBacground extends JDialog{
		/**
		 * 
		 */
		private static final long serialVersionUID = -990903376750998765L;
		private final int back_kind = 6;
		private Font f = new Font("微软雅黑",Font.PLAIN,15);
		private JPanel p = new JPanel();
		
		public AlterBacground(){
			 setTitle("更换游戏背景");//设置窗体标题
			 Image img=Toolkit.getDefaultToolkit().getImage("title.png");//窗口图标
			 setIconImage(img);
		     setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		     setModal(true);//设置为模态窗口
		     setSize(650,390);
		     setResizable(false);
		     setLocationRelativeTo(null);
		     
		     //添加背景图片
		     ImageIcon background[] = new ImageIcon[back_kind];
		     background[0] = new ImageIcon("background//desert.jpg");
		     background[1] = new ImageIcon("background//grass.jpg");
		     background[2] = new ImageIcon("background//ocean.jpg");
		     background[3] = new ImageIcon("background//ocean2.jpg");
		     background[4] = new ImageIcon("background//sky.jpg");
		     background[5] = new ImageIcon("background//sky2.jpg");

		     background[0].setImage(background[0].getImage().getScaledInstance(200,110,Image.SCALE_FAST));//快速
		     background[1].setImage(background[1].getImage().getScaledInstance(200,110,Image.SCALE_FAST));
		     background[2].setImage(background[2].getImage().getScaledInstance(200,110,Image.SCALE_FAST));
		     background[3].setImage(background[3].getImage().getScaledInstance(200,110,Image.SCALE_FAST));
		     background[4].setImage(background[4].getImage().getScaledInstance(200,110,Image.SCALE_FAST));
		     background[5].setImage(background[5].getImage().getScaledInstance(200,110,Image.SCALE_FAST));
		     
		     JLabel Back_label[] = new JLabel[back_kind];
		     JButton choose[] = new JButton[back_kind];
		     for(int i = 0;i < back_kind;i++)
		     {
		    	 Back_label[i] = new JLabel(background[i],SwingConstants.LEFT);
		    	 Back_label[i].setFont(f);
		    	 Back_label[i].setHorizontalTextPosition(SwingConstants.CENTER);
		    	 Back_label[i].setVerticalTextPosition(SwingConstants.BOTTOM);
		    	 
		    	 choose[i] = new JButton("选择");
		    	 choose[i].setFont(f);
		    	 p.add(choose[i]);
		    	 p.add(Back_label[i]);
		     }
		     
		     add(p,BorderLayout.CENTER);
		     p.setBackground(Color.white);
		     p.setLayout(null);
		     
		     
		     
		     Back_label[0].setBounds(10, 0, 200, 120);
		     choose[0].setBounds(70, 140, 80, 25);
		     Back_label[1].setBounds(220, 0, 200, 120);
		     choose[1].setBounds(280, 140, 80, 25);
		     Back_label[2].setBounds(430, 0, 200, 120);
		     choose[2].setBounds(490, 140, 80, 25);
		     Back_label[3].setBounds(10, 180, 200, 120);
		     choose[3].setBounds(70, 320, 80, 25);
		     Back_label[4].setBounds(220, 180, 200, 120);
		     choose[4].setBounds(280, 320, 80, 25);
		     Back_label[5].setBounds(430, 180, 200, 120);
		     choose[5].setBounds(490, 320, 80, 25);
		     
		     for(int i = 0;i < back_kind;i++)
		     {
		    	 choose[i].addActionListener(new ActionListener(){
		         	public void actionPerformed(ActionEvent e){
		        		if(e.getSource() == choose[0])
		        		{
		        			background[0] = new ImageIcon("background//desert.jpg");
		        			background[0].setImage(background[0].getImage().getScaledInstance(1000,540,Image.SCALE_SMOOTH));
		        			background_label.setIcon(background[0]);
		        			//JOptionPane.showMessageDialog(null,"更改成功!\n");
		        		}
		        		else if(e.getSource() == choose[1])
		        		{
		       		     	background[1] = new ImageIcon("background//grass.jpg");
		        			background[1].setImage(background[1].getImage().getScaledInstance(1000,540,Image.SCALE_SMOOTH));
		        			background_label.setIcon(background[1]);
		        			//JOptionPane.showMessageDialog(null,"更改成功!\n");
		        		}
		        		else if(e.getSource() == choose[2])
		        		{
		        			background[2] = new ImageIcon("background//ocean.jpg");
		        			background[2].setImage(background[2].getImage().getScaledInstance(1000,540,Image.SCALE_SMOOTH));
		        			background_label.setIcon(background[2]);
		        			//JOptionPane.showMessageDialog(null,"更改成功!\n");
		        		}
		        		else if(e.getSource() == choose[3])
		        		{
		        			background[3] = new ImageIcon("background//ocean2.jpg");
		        			background[3].setImage(background[3].getImage().getScaledInstance(1000,540,Image.SCALE_SMOOTH));
		        			background_label.setIcon(background[3]);
		        			//JOptionPane.showMessageDialog(null,"更改成功!\n");
		        		}
		        		else if(e.getSource() == choose[4])
		        		{
		        			background[4] = new ImageIcon("background//sky.jpg");
		        			background[4].setImage(background[4].getImage().getScaledInstance(1000,540,Image.SCALE_SMOOTH));
		        			background_label.setIcon(background[4]);
		        			//JOptionPane.showMessageDialog(null,"更改成功!\n");
		        		}
		        		else if(e.getSource() == choose[5])
		        		{
		        			background[5] = new ImageIcon("background//sky2.jpg");
		        			background[5].setImage(background[5].getImage().getScaledInstance(1000,540,Image.SCALE_SMOOTH));
		        			background_label.setIcon(background[5]);
		        			//JOptionPane.showMessageDialog(null,"更改成功!\n");
		        		}
		        	}
		        });
		     }
		     
		     setVisible(true);
		}
	}
}


