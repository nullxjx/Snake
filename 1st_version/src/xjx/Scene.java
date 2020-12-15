package xjx;

import java.awt.*;
import java.awt.event.*;
import java.util.Deque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

public class Scene extends JFrame{
    private final Font f = new Font("微软雅黑",Font.PLAIN,15);
    private final Font f2 = new Font("微软雅黑",Font.PLAIN,12);
    private JPanel paintPanel;//画板，画线条用的
    private final JLabel label  = new JLabel("当前长度：");
    private final JLabel label2 = new JLabel("所花时间：");
    private final JLabel Length = new JLabel("1");
    private final JLabel Time = new JLabel("");
    private Timer timer;
    public boolean pause = false;
    public boolean quit = false;
    public boolean die = false;
    private boolean show_padding = true;
    private boolean show_grid = true;
    public final int pixel_per_unit = 22;       //每个网格的像素数目
    public final int pixel_rightBar = 110;      //右边信息栏的宽度(像素)
    public final int padding = 5;               //内边框宽度
    public final int width = 20;
    public final int height = 20;
    private Snake snake;

    public void restart(){//重新开始游戏
        quit = true;
        Length.setText("1");
        Time.setText("");

        snake.quit();
        snake = null;
        snake = new Snake(this);

        timer.reset();

        die = false;
        quit = false;
        pause = false;

        System.out.println("\nGame restart...");
    }

    public void updateLength(int length){
        Length.setText(""+length);
    }

    //通过方格序号返回其横坐标
    public int getPixel(int i, int padding, int pixels_per_unit) {
        return 1+padding+i*pixels_per_unit;
    }

    public void initUI(){
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
            e1.printStackTrace();
        }

        Image img = Toolkit.getDefaultToolkit().getImage("image//title.png");//窗口图标
        setIconImage(img);
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width*pixel_per_unit+pixel_rightBar, height * pixel_per_unit + 75);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);

        paintPanel = new JPanel(){
            //绘制界面的函数
            public void paint(Graphics g1){
                super.paint(g1);
                Graphics2D g = (Graphics2D) g1;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_NORMALIZE);

                //边框线
                if(show_padding){
                    g.setPaint(new GradientPaint(115,135,Color.CYAN,230,135,Color.MAGENTA,true));
                    g.setStroke( new BasicStroke(4,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL));
                    g.drawRect(2, 2, padding*2-4+width*pixel_per_unit, height*pixel_per_unit+6);
                }

                //网格线
                if(show_grid) {
                    for(int i = 0; i <= width; i++) {
                        g.setStroke( new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                                3.5f, new float[] { 15, 10, }, 0f));//虚线
                        g.setColor(Color.black);
                        g.drawLine(padding+i*pixel_per_unit, padding,
                                padding+i*pixel_per_unit,padding+height*pixel_per_unit);//画竖线
                    }

                    for(int i = 0;i <= height; i++){
                        g.drawLine(padding,padding+i*pixel_per_unit,
                                padding+width*pixel_per_unit,padding+i*22);//画横线
                    }
                }

                //食物
                Coordinate food = snake.getFoodCoor();
                g.setColor(Color.green);
                g.fillOval(getPixel(food.x, padding, pixel_per_unit),
                        getPixel(food.y, padding, pixel_per_unit), 20, 20);

                //头部
                Deque<Coordinate> body = snake.getBodyCoors();
                Coordinate head = body.getFirst();
                g.setColor(Color.red);
                g.fillRoundRect(getPixel(head.x, padding, pixel_per_unit),
                        getPixel(head.y, padding, pixel_per_unit), 20, 20, 10, 10);

                //身体
                g.setPaint(new GradientPaint(115,135,Color.CYAN,230,135,Color.MAGENTA,true));
                for (Coordinate coor : body){
                    if(head.x == coor.x && head.y == coor.y) continue;
                    g.fillRoundRect(getPixel(coor.x, padding, pixel_per_unit),
                            getPixel(coor.y, padding, pixel_per_unit), 20, 20, 10, 10);
                }

                //显示死亡信息
                if(die) {
                    g.setFont(new Font("微软雅黑",Font.BOLD | Font.ITALIC,30));
                    g.setColor(Color.BLACK);
                    g.setStroke( new BasicStroke(10,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL));

                    int x = this.getWidth()/2, y = this.getHeight()/2;
                    g.drawString("Sorry, you die", x-350, y-50);
                    g.drawString("Press ESC to restart", x-350, y+50);
                }
            }
        };
        paintPanel.setOpaque(false);
        paintPanel.setBounds(0, 0, 900, 480);
        add(paintPanel);

        int info_x = padding*3 + width*pixel_per_unit;
        add(label);label.setBounds(info_x, 10, 80, 20);label.setFont(f);label.setForeground(Color.black);
        add(Length);Length.setBounds(info_x, 35, 80, 20);Length.setFont(f);Length.setForeground(Color.black);
        add(label2);label2.setBounds(info_x, 70, 80, 20);label2.setFont(f);label2.setForeground(Color.black);
        add(Time);Time.setBounds(info_x, 95, 80, 20);Time.setFont(f);Time.setForeground(Color.black);

        //菜单栏
        JMenuBar bar = new JMenuBar();bar.setBackground(Color.white);setJMenuBar(bar);
        JMenu Settings = new JMenu("设置");Settings.setFont(f);bar.add(Settings);
        JMenu Help = new JMenu("帮助");Help.setFont(f);bar.add(Help);
        JMenu About = new JMenu("关于");About.setFont(f);bar.add(About);
        JMenuItem remove_net= new JMenuItem("移除网格");remove_net.setFont(f2);Settings.add(remove_net);
        JMenuItem remove_padding= new JMenuItem("移除边框");remove_padding.setFont(f2);Settings.add(remove_padding);
        JMenuItem help = new JMenuItem("Guide...");help.setFont(f2);Help.add(help);
        JMenuItem about = new JMenuItem("About...");about.setFont(f2);About.add(about);

        //监听器
        this.addKeyListener(new MyKeyListener());
        remove_net.addActionListener(e -> {
            if(!show_grid) {
                show_grid = true;
                remove_net.setText("移除网格");
            } else {
                show_grid = false;
                remove_net.setText("显示网格");
            }
            paintPanel.repaint();
        });
        remove_padding.addActionListener(e -> {
            if(!show_padding) {
                show_padding = true;
                remove_padding.setText("移除边框");
            } else {
                show_padding = false;
                remove_padding.setText("显示边框");
            }
            paintPanel.repaint();
        });
        about.addActionListener(e -> new About());
        help.addActionListener(e -> new Help());
    }

    public void run(){
        snake = new Snake(this);
        setFocusable(true);
        setVisible(true);
        timer = new Timer();
    }

    public static void main(String[] args) {
        System.out.println("Application starting...");
        Scene game = new Scene();   //初始化游戏场景
        game.initUI();              //初始化游戏界面
        game.run();                 //开始游戏
        System.out.println("Game start...");
    }

    private class Timer{
        //计时器类,负责计时，调用方法，new Timer(); 然后主界面就开始显示计时
        private int hour = 0;
        private int min = 0;
        private int sec = 0;

        public Timer(){
            this.run();
        }

        public void run() {
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                if (!quit && !pause) {
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
            }, 0, 1000, TimeUnit.MILLISECONDS);
        }

        public void reset() {
            hour = 0;
            min = 0;
            sec = 0;
        }

        private void showTime(){
            String strTime;
            if(hour < 10) strTime = "0"+hour+":";
            else strTime = ""+hour+":";

            if(min < 10) strTime = strTime+"0"+min+":";
            else strTime =strTime+ ""+min+":";

            if(sec < 10) strTime = strTime+"0"+sec;
            else strTime = strTime+""+sec;

            //在窗体上设置显示时间
            Time.setText(strTime);
        }
    }

    private class MyKeyListener implements KeyListener{
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            Direction direction = snake.direction;

            if(key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {          //向右
                if(!quit && direction != Direction.LEFT) {
                    snake.direction = Direction.RIGHT;
                }
            } else if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {    //向左
                if(!quit && direction != Direction.RIGHT) {
                    snake.direction = Direction.LEFT;
                }
            } else if(key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {      //向上
                if(!quit && direction != Direction.DOWN) {
                    snake.direction = Direction.UP;
                }
            } else if(key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {    //向下
                if(!quit && direction != Direction.UP) {
                    snake.direction = Direction.DOWN;
                }
            } else if(key == KeyEvent.VK_ESCAPE) {  //重新开始
                restart();
            } else if(key == KeyEvent.VK_SPACE) {
                if(!pause) {//暂停
                    pause = true;
                    System.out.println("暂停...");
                } else {//开始
                    pause = false;
                    System.out.println("开始...");
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // TODO Auto-generated method stub
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // TODO Auto-generated method stub
        }
    }
}