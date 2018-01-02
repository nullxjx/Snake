package xjx;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainWindow extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1877974685325498861L;
	private Font f = new Font("微软雅黑",Font.PLAIN,15);
	private Font f2 = new Font("微软雅黑",Font.PLAIN,12);
	private JRadioButtonMenuItem speedItems[];
	private ButtonGroup speedGroup;
	private JRadioButtonMenuItem headItems[]; 
	private ButtonGroup headGroup;
	private JRadioButtonMenuItem bodyItems[];
	private ButtonGroup bodyGroup;
	private ImageIcon backgroundImage;
	private JLabel label;
	JPanel imagePanel;
	
	public MainWindow(){
		Image img = Toolkit.getDefaultToolkit().getImage("title.png");//窗口图标
		setIconImage(img);
	    setTitle("Snake By XJX");
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	    setSize(602, 507);
	    setSize(1000,540);
	    setResizable(false);
	    setLocationRelativeTo(null);
	    
	    //添加背景图片
	    backgroundImage = new ImageIcon("background//sky2.jpg");
	    backgroundImage.setImage(backgroundImage.getImage().getScaledInstance(1000,540,Image.SCALE_SMOOTH));
        label = new JLabel(backgroundImage);  
        label.setBounds(0,0, this.getWidth(), this.getHeight());   
        imagePanel = (JPanel) this.getContentPane();  
        imagePanel.setOpaque(false);  
        this.getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));
        
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
		
		JMenuItem help = new JMenuItem("游戏指导");
		help.setFont(f2);
		Help.add(help);
		
		JMenuItem about = new JMenuItem("关于此游戏");
		about.setFont(f2);
		About.add(about);
		
		
	    SnakeDemo snake = new SnakeDemo();
		snake.Thread();
		snake.Thread2();
		snake.setOpaque(false);
		imagePanel.add(snake, BorderLayout.CENTER);
		
		//添加监听器
		remove_net.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		if(!snake.If_remove)
        		{
        			snake.If_remove = true;
        			remove_net.setText("显示网格");
        		}
        		else
        		{
        			snake.If_remove = false;
        			remove_net.setText("移除网格");
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
										snake.normal_speed = 600;
									}
									else if(i == 1)
									{
										snake.normal_speed = 400;
									}
									else if(i == 2)
									{
										snake.normal_speed = 300;
									}
									else if(i == 3)
									{
										snake.normal_speed = 150;
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
		headIcon[0] = new ImageIcon("head//head.png");
		headIcon[1] = new ImageIcon("head//head2.png");
		headIcon[2] = new ImageIcon("head//head3.png");
		headIcon[3] = new ImageIcon("head//head4.png");
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
									snake.remove(snake.head_label);
									snake.snakehead = new ImageIcon(headIcon[i].toString());
									snake.snakehead.setImage(snake.snakehead.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
									snake.head_label = new JLabel(snake.snakehead);
									snake.add(snake.head_label);
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
		bodyIcon[0] = new ImageIcon("body//body.png");
		bodyIcon[1] = new ImageIcon("body//body2.png");
		bodyIcon[2] = new ImageIcon("body//body3.png");
		bodyIcon[3] = new ImageIcon("body//body4.png");
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
									for(int j = 0;j < snake.body_length;j++)
									{
										snake.remove(snake.body_label[j]);
									}
									snake.snakebody = new ImageIcon(bodyIcon[i].toString());
									snake.snakebody.setImage(snake.snakebody.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
									for(int k = 0; k < snake.MAX_SIZE;k++)
									{
										snake.body_label[k] = new JLabel(snake.snakebody); 
										snake.body_label[k].setOpaque(false);
									}
									
									for(int l = 0;l < snake.body_length;l++)
									{
										snake.add(snake.body_label[l]);
									}
								}
							}
						}
					}
			);
		}
		bodyItems[0].setSelected(true);
		
		set_background.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		new Alter_Bacground();
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
		
	    setVisible(true);
	}
	
	public static void main(String[] args) {
		new MainWindow();
	}
	
	class Alter_Bacground extends JDialog{
		/**
		 * 
		 */
		private static final long serialVersionUID = -990903376750998765L;
		private final int back_kind = 6;
		private Font f = new Font("微软雅黑",Font.PLAIN,15);
		private JPanel p = new JPanel();
		
		public Alter_Bacground(){
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
		        			ImageIcon temp = new ImageIcon(background[0].toString());
		        			backgroundImage.setImage(temp.getImage().getScaledInstance(1000,540,Image.SCALE_SMOOTH));
		        			repaint();
		        			JOptionPane.showMessageDialog(null,"更改成功！回到游戏界面生效");
		        		}
		        		else if(e.getSource() == choose[1])
		        		{
		        			ImageIcon temp = new ImageIcon(background[1].toString());
		        			backgroundImage.setImage(temp.getImage().getScaledInstance(1000,540,Image.SCALE_SMOOTH));
		        			repaint();
		        			JOptionPane.showMessageDialog(null,"更改成功！回到游戏界面生效");
		        		}
		        		else if(e.getSource() == choose[2])
		        		{
		        			ImageIcon temp = new ImageIcon(background[2].toString());
		        			backgroundImage.setImage(temp.getImage().getScaledInstance(1000,540,Image.SCALE_SMOOTH));
		        			repaint();
		        			JOptionPane.showMessageDialog(null,"更改成功！回到游戏界面生效");
		        		}
		        		else if(e.getSource() == choose[3])
		        		{
		        			ImageIcon temp = new ImageIcon(background[3].toString());
		        			backgroundImage.setImage(temp.getImage().getScaledInstance(1000,540,Image.SCALE_SMOOTH));
		        			repaint();
		        			JOptionPane.showMessageDialog(null,"更改成功！回到游戏界面生效");
		        		}
		        		else if(e.getSource() == choose[4])
		        		{
		        			ImageIcon temp = new ImageIcon(background[4].toString());
		        			backgroundImage.setImage(temp.getImage().getScaledInstance(1000,540,Image.SCALE_SMOOTH));
		        			repaint();
		        			JOptionPane.showMessageDialog(null,"更改成功！回到游戏界面生效");
		        		}
		        		else if(e.getSource() == choose[5])
		        		{
		        			ImageIcon temp = new ImageIcon(background[5].toString());
		        			backgroundImage.setImage(temp.getImage().getScaledInstance(1000,540,Image.SCALE_SMOOTH));
		        			repaint();
		        			JOptionPane.showMessageDialog(null,"更改成功！回到游戏界面生效");
		        		}
		        	}
		        });
		     }
		     setVisible(true);
		}
	}
}
