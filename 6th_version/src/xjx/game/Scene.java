package xjx.game;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

import xjx.snake.*;

public class Scene extends JFrame{
    private final Font f = new Font("微软雅黑",Font.PLAIN,15);
    private final Font f2 = new Font("微软雅黑",Font.PLAIN,12);
    private JRadioButtonMenuItem[] modeItems;
    private JRadioButtonMenuItem[] speedItems;
    private JRadioButtonMenuItem[] headItems;
    private JRadioButtonMenuItem[] bodyItems;
    private JLabel background_label;
    private JPanel paintPanel;//画板，画线条用的
    private final JLabel label  = new JLabel("当前长度：");
    private final JLabel label2 = new JLabel("所花时间：");
    private final JLabel label3 = new JLabel("当前得分：");
    private final JLabel label4 = new JLabel("食物个数：");
    private final JLabel label5 = new JLabel("剩余子弹：");
    private final JLabel label6 = new JLabel("AI长度：");
    private final JLabel label7 = new JLabel("食物坐标：");

    private final JLabel FoodCoor = new JLabel("");
    private final JLabel AILength = new JLabel("1");
    private final JLabel Length = new JLabel("1");
    private final JLabel Score = new JLabel("0");
    private final JLabel Time = new JLabel("");
    private final JLabel Amount = new JLabel("0");
    private final JLabel Weapon = new JLabel("5");
    private final HashMap<String, JLabel> infos = new HashMap<>();
    private final HashMap<Integer, JLabel> walls = new HashMap<>();
    private final JPanel p = new JPanel();
    private BGPanel backGroundPanel;
    private Timer timer;

    public boolean pause = true;
    public boolean quit = false;
    public boolean die = false;
    public boolean ai_die = false;
    private boolean show_grid = true;       //标记是否显示界面上的网格线，默认显示
    private boolean show_padding = true;    //标记是否显示界面上的边框线，默认显示

    private PlayerSnake snake;
    private AISnake ai;
    private Foodset food;
    public final int pixel_per_unit = 22;       //每个网格的像素数目
    public final int pixel_rightBar = 110;      //右边信息栏的宽度(像素)
    public final int padding = 5;               //内边框宽度

    private int[][] gameMap;                    //map数组标记当前地图的使用情况
    /*0表示空闲（道路）
     *1表示玩家蛇身体节点
     *2表示食物
     *3表示障碍物
     *4表示AI蛇身体结点
     */

    public int gameMode = 0;                   //游戏模式
    /*0表示只有player snake
    * 1表示只有ai snake
    * 2表示player snake 和 ai snake都存在
    * */

    public synchronized int[][] getMap(){
        return gameMap;
    }

    public synchronized void setMap(int i, int j, int e){
        gameMap[i][j] = e;
    }

    public void resetLabel(){
        FoodCoor.setText("");
        AILength.setText("1");
        Length.setText("1");
        Score.setText("0");
        Time.setText("");
        Amount.setText("0");
        Weapon.setText("5");
    }

    public void restart(){//重新开始游戏
        quit = true;
        resetLabel();
        speedItems[2].setSelected(true);
        headItems[0].setSelected(true);
        bodyItems[0].setSelected(true);

        food.removeAll();
        food = null;
        food = new Foodset(this);

        removeAllBrick();

        loadGameMap("map//map.txt");//加载游戏地图
        PrintMap(gameMap,"debug//map.txt");

        initWalls();

        /*0表示只有player snake
         * 1表示只有ai snake
         * 2表示player snake 和 ai snake都存在
         * */
        if(gameMode == 0){
            snake.removeAll();
            snake.quit();
            snake = null;
            snake = new PlayerSnake(this);
        }else if(gameMode == 1){
            ai.removeAll();
            ai.removeAllPath();
            ai.quit();
            ai = null;
            ai = new AISnake(this);
        }else if(gameMode == 2){
            snake.removeAll();
            snake.quit();
            snake = null;
            snake = new PlayerSnake(this);

            ai.removeAll();
            ai.removeAllPath();
            ai.quit();
            ai = null;
            ai = new AISnake(this);
        }

        timer.reset();

        die = false;
        ai_die = false;
        quit = false;
        pause = false;

        System.out.println("\nGame restart...\t" + getSysTime());
    }

    public void changeGameMode(int current_mode, int new_mode){
        quit = true;
        resetLabel();
        speedItems[2].setSelected(true);
        headItems[0].setSelected(true);
        bodyItems[0].setSelected(true);

        food.removeAll();
        food = null;
        food = new Foodset(this);

        removeAllBrick();

        loadGameMap("map//map.txt");//加载游戏地图
        PrintMap(gameMap,"debug//map.txt");

        initWalls();

        /*0表示只有player snake
         * 1表示只有ai snake
         * 2表示player snake 和 ai snake都存在
         * */
        if(current_mode == 0){
            snake.removeAll();
            snake.quit();
            snake = null;
        }else if(current_mode == 1){
            ai.removeAll();
            ai.removeAllPath();
            ai.quit();
            ai = null;
        }else if(current_mode == 2){
            snake.removeAll();
            snake.quit();
            snake = null;
            ai.removeAll();
            ai.removeAllPath();
            ai.quit();
            ai = null;
        }

        if(new_mode == 0){
            snake = new PlayerSnake(this);
        }else if(new_mode == 1){
            ai = new AISnake(this);
        }else if(new_mode == 2){
            snake = new PlayerSnake(this);
            ai = new AISnake(this);
        }

        gameMode = new_mode;
        initRightBar();

        timer.reset();

        die = false;
        ai_die = false;
        quit = false;
        pause = false;

        System.out.println("\nGame restart...\t" + getSysTime());
    }

    public Coordinate randomCoor(){
        int rows = gameMap.length, cols = gameMap[0].length;
        Random rand = new Random();
        Coordinate res;
        int x = rand.nextInt(rows-1);
        int y = rand.nextInt(cols-1);

        while(true) {
            /*
            * 保证身体节点，食物，障碍物都不能和该坐标重合
            * 产生的点尽可能远离原点(0, 0)
            * */
            if(gameMap[x][y] != 0 || y == cols-1) {
                x = rand.nextInt(rows-1);
                y = rand.nextInt(cols-1);
            } else {
                break;
            }
        }
        res = new Coordinate(x,y);
        return res;
    }

    public void loadGameMap(String file){
        Vector<String> v = new Vector<>();
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader reader = null;
        if (fin != null) {
            reader = new InputStreamReader(fin);
        }
        BufferedReader buffReader = null;
        if (reader != null) {
            buffReader = new BufferedReader(reader);
        }

        String line = "";
        while(true){
            try {
                if (buffReader != null && (line = buffReader.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
//            System.out.println(line);
//            System.out.println(line.length());
            v.add(line);
        }

        try {
            buffReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int rows = v.size(), cols = v.get(0).length()/2;
//        System.out.println("rows: " + rows + " cols: " + cols);

        gameMap = new int[rows][cols];
        for(int i = 0; i < rows; i++) {
            line = v.get(i);
            for (int j = 0; j < cols; j++) {
                gameMap[i][j] = line.charAt(2*j)-'0';
            }
        }
    }

    public void initWalls(){
        int rows = gameMap.length, cols = gameMap[0].length;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                if(gameMap[i][j] == 3){
                    //加载砖块图片
                    ImageIcon brickIcon = new ImageIcon("image//brick.png");
                    brickIcon.setImage(brickIcon.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));//保持图片的清晰
                    Coordinate coor = new Coordinate(j, i);
                    JLabel label = new JLabel(brickIcon);
                    this.add(label);
                    label.setBounds(getPixel(coor.x,padding,pixel_per_unit),
                            getPixel(coor.y,padding,pixel_per_unit), 20, 20);
                    walls.put(coor_trans(new Coordinate(i, j)), label);
                }
            }
        }
    }

    //注意，序号从0开始
    public int getPixel(int i, int paddind, int pixels_per_unit) {
        //通过方格序号返回其横坐标
        return 1+paddind+i*pixels_per_unit;
    }

    public static String getSysTime(){
        String Time = "";
        Calendar Cld = Calendar.getInstance();
        int YY = Cld.get(Calendar.YEAR) ;
        int MM = Cld.get(Calendar.MONTH)+1;
        int DD = Cld.get(Calendar.DATE);
        int HH = Cld.get(Calendar.HOUR_OF_DAY);
        int mm = Cld.get(Calendar.MINUTE);
        int SS = Cld.get(Calendar.SECOND);
        int MI = Cld.get(Calendar.MILLISECOND);
        Time += YY + "/" + MM + "/" + DD + "-" + HH + ":" + mm + ":" + SS + ":" + MI;

        return Time;
    }

    //字符串写出到文本
    public static void  Write2Txt(String str,String filepath) {
        FileWriter fw;
        File f = new File(filepath);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            fw = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(fw);
            // FileOutputStream fos = new FileOutputStream(f);
            // OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
            out.write(str);
            out.close();
            //System.out.println("===========写入文本成功========");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void PrintMap(int[][] map,String filepath){
        String temp = "";
        temp += "\t";
        for(int i = 0;i < map[0].length;i++)
            temp += i + "\t";
        temp += "\n";

        for(int i = 0;i < map.length;i++)
        {
            temp += i + "\t";
            for(int j = 0;j <map[0].length;j++)
                temp += map[i][j] + "\t";
            temp += "\n";
        }
        try {
            Write2Txt(temp,filepath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void removeBrick(Coordinate coor){
        JLabel label = walls.get(coor_trans(coor));
        label.setVisible(false);
        this.remove(label);
        walls.remove(coor_trans(coor));
    }

    public void removeAllBrick(){
        for(int key : walls.keySet()){
            JLabel label = walls.get(key);
            label.setVisible(false);
            this.remove(label);
        }

        walls.clear();
    }

    public void initMenuBar(){
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

        JMenu change_mode = new JMenu("切换游戏模式");
        change_mode.setFont(f2);
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
        JMenuItem remove_padding= new JMenuItem("移除边框");
        remove_padding.setFont(f2);

        Settings.add(change_mode);
        Settings.add(set_background);
        Settings.add(set_head);
        Settings.add(set_body);
        Settings.add(set_speed);
        Settings.add(remove_net);
        Settings.add(remove_padding);

        JMenuItem help = new JMenuItem("Guide...");
        help.setFont(f2);
        Help.add(help);

        JMenuItem about = new JMenuItem("About...");
        about.setFont(f2);
        About.add(about);

        initWalls();

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

        String[] modes = {"仅玩家蛇", "仅AI蛇", "玩家蛇和AI蛇"};
        modeItems = new JRadioButtonMenuItem[modes.length];
        ButtonGroup modeGroup = new ButtonGroup();
        for(int i = 0; i < modes.length; i ++){
            modeItems[i] = new JRadioButtonMenuItem(modes[i]);
            modeItems[i].setFont(f2);
            change_mode.add(modeItems[i]);
            modeGroup.add(modeItems[i]);
            modeItems[i].addActionListener(e -> {
                for(int j = 0; j < modeItems.length; j++){
                    if(modeItems[j].isSelected()){
                        if(j == gameMode){
                            return;
                        }else{
                            changeGameMode(gameMode, j);
                        }
                    }
                }
            });
        }
        modeItems[gameMode].setSelected(true);
//        modeItems[2].setEnabled(false);

        //设置速度菜单
        String[] speed = {"龟速","行走","奔跑","疯狂"};
        speedItems = new JRadioButtonMenuItem[speed.length];
        ButtonGroup speedGroup = new ButtonGroup();
        for(int i = 0;i < speed.length;i++) {
            speedItems[i] = new JRadioButtonMenuItem(speed[i]);
            speedItems[i].setFont(f2);
            set_speed.add(speedItems[i]);
            speedGroup.add(speedItems[i]);
            speedItems[i].addActionListener(e -> {
                for(int i1 = 0; i1 < speedItems.length; i1++) {
                    if(speedItems[i1].isSelected()) {
                        if(i1 == 0) {
                            snake.setDefaultSpeed(600);
                            snake.resetSpeed();
                        } else if(i1 == 1) {
                            snake.setDefaultSpeed(500);
                            snake.resetSpeed();
                        } else if(i1 == 2) {
                            snake.setDefaultSpeed(200);
                            snake.resetSpeed();
                        } else if(i1 == 3) {
                            snake.setDefaultSpeed(100);
                            snake.resetSpeed();
                        }
                    }
                }
            });
        }
        speedItems[1].setSelected(true);

        //设置头部图片
        String[] head = {"doge","二哈","经典","憧憬"};
        headItems = new JRadioButtonMenuItem[head.length];
        ButtonGroup headGroup = new ButtonGroup();
        ImageIcon[] headIcon = new ImageIcon[head.length];
        for(int i = 0; i < head.length; i++) {
            headIcon[i] = new ImageIcon("head//head" + i + ".png");
            headIcon[i].setImage(headIcon[i].getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));
        }
        for(int i = 0;i < head.length;i++) {
            headItems[i] = new JRadioButtonMenuItem(head[i]);
            headItems[i].setFont(f2);
            headItems[i].setIcon(headIcon[i]);
            set_head.add(headItems[i]);
            headGroup.add(headItems[i]);
            headItems[i].addActionListener(e -> {
                for(int i12 = 0; i12 < headItems.length; i12++) {
                    if(headItems[i12].isSelected()) snake.setHeadIcon(i12);
                }
            });
        }
        headItems[0].setSelected(true);

        //设置身体图片
        String[] body = {"乖巧","笑眼","滑稽","阴险"};
        bodyItems = new JRadioButtonMenuItem[body.length];
        ButtonGroup bodyGroup = new ButtonGroup();
        ImageIcon[] bodyIcon = new ImageIcon[body.length];
        for(int i = 0; i < body.length; i++) {
            bodyIcon[i] = new ImageIcon("body//body" + i + ".png");
            bodyIcon[i].setImage(bodyIcon[i].getImage().getScaledInstance(16,16,Image.SCALE_SMOOTH));
        }
        for(int i = 0;i < body.length;i++) {
            bodyItems[i] = new JRadioButtonMenuItem(body[i]);
            bodyItems[i].setFont(f2);
            bodyItems[i].setIcon(bodyIcon[i]);
            set_body.add(bodyItems[i]);
            bodyGroup.add(bodyItems[i]);
            bodyItems[i].addActionListener(e -> {
                for(int i13 = 0; i13 < bodyItems.length; i13++) {
                    if(bodyItems[i13].isSelected()) snake.setBodyIcon(i13);
                }
            });
        }
        bodyItems[0].setSelected(true);


        backGroundPanel = new BGPanel();
        set_background.addActionListener(e -> backGroundPanel.setVisible(true));
        about.addActionListener(e -> new About());
        help.addActionListener(e -> new Help());
    }

    public void initRightBar(){
        remove(label);remove(label2);remove(label3);remove(label4);
        remove(label5);remove(label6);remove(label7);
        remove(FoodCoor);remove(AILength);remove(Length);
        remove(Score);remove(Time);remove(Amount);remove(Weapon);remove(p);

        //布局
        int info_x = padding*3 + gameMap[0].length*pixel_per_unit;
        if(gameMode == 0){//player snake
            add(label);label.setBounds(info_x, 10, 80, 20);label.setFont(f);
            add(Length);Length.setBounds(info_x, 35, 80, 20);Length.setFont(f);
            add(label2);label2.setBounds(info_x, 70, 80, 20);label2.setFont(f);
            add(Time);Time.setBounds(info_x, 95, 80, 20);Time.setFont(f);
            add(label3);label3.setBounds(info_x, 130, 80, 20);label3.setFont(f);
            add(Score);Score.setBounds(info_x, 155, 80, 20);Score.setFont(f);
            add(label4);label4.setBounds(info_x, 190, 80, 20);label4.setFont(f);
            add(Amount);Amount.setBounds(info_x, 215, 80, 20);Amount.setFont(f);
            add(label5);label5.setBounds(info_x, 250, 80, 20);label5.setFont(f);
            add(Weapon);Weapon.setBounds(info_x, 275, 80, 20);Weapon.setFont(f);
        }else if(gameMode == 1){//ai snake
            add(label6);label6.setBounds(info_x, 10, 80, 20);label6.setFont(f);
            add(AILength);AILength.setBounds(info_x, 35, 80, 20);AILength.setFont(f);
            add(label2);label2.setBounds(info_x, 70, 80, 20);label2.setFont(f);
            add(Time);Time.setBounds(info_x, 95, 80, 20);Time.setFont(f);
            add(label7);label7.setBounds(info_x, 130, 80, 20);label7.setFont(f);
            add(FoodCoor);FoodCoor.setBounds(info_x, 155, 80, 20);FoodCoor.setFont(f);
        }else if(gameMode == 2){
            add(label);label.setBounds(info_x, 10, 80, 20);label.setFont(f);
            add(Length);Length.setBounds(info_x, 35, 80, 20);Length.setFont(f);
            add(label2);label2.setBounds(info_x, 70, 80, 20);label2.setFont(f);
            add(Time);Time.setBounds(info_x, 95, 80, 20);Time.setFont(f);
            add(label3);label3.setBounds(info_x, 130, 80, 20);label3.setFont(f);
            add(Score);Score.setBounds(info_x, 155, 80, 20);Score.setFont(f);
            add(label4);label4.setBounds(info_x, 190, 80, 20);label4.setFont(f);
            add(Amount);Amount.setBounds(info_x, 215, 80, 20);Amount.setFont(f);
            add(label5);label5.setBounds(info_x, 250, 80, 20);label5.setFont(f);
            add(Weapon);Weapon.setBounds(info_x, 275, 80, 20);Weapon.setFont(f);
            add(p);p.setBounds(info_x, 300, 70, 1);p.setBorder(BorderFactory.createLineBorder(Color.white));
            add(label6);label6.setBounds(info_x, 315, 80, 20);label6.setFont(f);
            add(AILength);AILength.setBounds(info_x, 340, 80, 20);AILength.setFont(f);
            add(label7);label7.setBounds(info_x, 365, 80, 20);label7.setFont(f);
            add(FoodCoor);FoodCoor.setBounds(info_x, 390, 80, 20);FoodCoor.setFont(f);
        }
        //初始化这些Label组成的Hashmap
        infos.put("FoodCoor", FoodCoor);            //食物坐标
        infos.put("AILength", AILength);            //AI长度
        infos.put("Length", Length);                //当前长度
        infos.put("Score", Score);                  //当前得分
        infos.put("Time", Time);                    //所花时间
        infos.put("Amount", Amount);                //食物个数
        infos.put("Weapon", Weapon);                //剩余子弹

        //字体颜色，为了便于分辨，设为白色
        label.setForeground(Color.white);
        label2.setForeground(Color.white);
        label3.setForeground(Color.white);
        label4.setForeground(Color.white);
        label5.setForeground(Color.white);
        label6.setForeground(Color.white);
        label7.setForeground(Color.white);
        FoodCoor.setForeground(Color.white);
        AILength.setForeground(Color.white);
        Length.setForeground(Color.white);
        Score.setForeground(Color.white);
        Time.setForeground(Color.white);
        Amount.setForeground(Color.white);
        Weapon.setForeground(Color.white);
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

        int rows = gameMap.length, cols = gameMap[0].length;
        setSize(cols*pixel_per_unit+pixel_rightBar, rows * pixel_per_unit + 75);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);

        //添加背景图片
        ImageIcon backgroundImage = new ImageIcon("background//background1.png");
        backgroundImage.setImage(backgroundImage.getImage().getScaledInstance(cols*pixel_per_unit+pixel_rightBar,rows * pixel_per_unit + 75,Image.SCALE_SMOOTH));
        background_label = new JLabel(backgroundImage);
        background_label.setBounds(0,0, this.getWidth(), this.getHeight());
        this.getLayeredPane().add(background_label, Integer.valueOf(Integer.MIN_VALUE));

        JPanel imagePanel = (JPanel) this.getContentPane();
        imagePanel.setOpaque(false);

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
                    g.drawRect(2, 2, padding*2-4+cols*pixel_per_unit, rows*pixel_per_unit+6);
                }

                //网格线
                if(show_grid) {
                    for(int i = 0; i <= cols; i++) {
                        g.setStroke( new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 3.5f, new float[] { 15, 10, }, 0f));//虚线
                        g.setColor(Color.black);
                        g.drawLine(padding+i*pixel_per_unit,padding,padding+i*pixel_per_unit,padding+rows*pixel_per_unit);//画竖线
                    }

                    for(int i = 0;i <= rows; i++){
                        g.drawLine(padding,padding+i*pixel_per_unit,padding+cols*pixel_per_unit,padding+i*22);//画横线
                    }
                }

                //显示死亡信息
                if(die || ai_die) {
                    g.setFont(new Font("微软雅黑",Font.BOLD | Font.ITALIC,30));
                    g.setColor(Color.white);
                    g.setStroke( new BasicStroke(10,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL));

                    int x = this.getWidth()/2, y = this.getHeight()/2;
                    //文字的位置没有根据界面大小进行适配，有可能显示出来看不到
                    if(die){
                        g.drawString("Sorry, you die", x-130, y-30);
                        g.drawString("Press Esc to restart", x-180, y+30);
                    }
                    if(ai_die){
                        g.drawString("oops, the stupid AI can not find a way out", x-250, y-30);
                        g.drawString("Press Esc to restart", x-180, y+30);
                    }
                }
            }
        };
        paintPanel.setOpaque(false);
        paintPanel.setBounds(0, 0, 900, 480);
        add(paintPanel);

        initRightBar();
        initMenuBar();
    }

    public void updateInfos(String key, String value){
        infos.get(key).setText(value);
    }

    public Vector<Coordinate> getFoodCoor(){
        return food.getFoodCoors();
    }

    public void removeFood(Coordinate coor){
        food.removeFoodCoor(coor);
    }

    public int getFoodPoint(Coordinate coor){
        return food.getFoodPoint(coor);
    }

    public Coordinate getAITarget(){
        return ai.getTarget();
    }

    public void FindNewPath(){
        ai.FindNewPath();
    }

    public int coor_trans(Coordinate coor){
        //把gamemap里面的二维坐标(x,y)转化成一维坐标
        int x = coor.x, y = coor.y;
        int cols = gameMap[0].length;
        return x * cols + y;
    }

    public void run(){
        food = new Foodset(this);
        if(gameMode == 0) snake = new PlayerSnake(this);
        else if(gameMode == 1) ai = new AISnake(this);
        else if(gameMode == 2) {
            snake = new PlayerSnake(this);
            ai = new AISnake(this);
        }

        setFocusable(true);
        setVisible(true);
        timer = new Timer();
        pause = false;
    }

    //主函数入口
    public static void main(String[] args) {
        System.out.println("Application starting...\t" + getSysTime());
        Scene game = new Scene();
        game.gameMode = 2;
        game.loadGameMap("map//trap.txt");//加载游戏地图
        PrintMap(game.getMap(),"debug//map.txt");
        game.initUI();//初始化游戏界面
        game.run();//开始游戏
        System.out.println("\nGame start...\t" + getSysTime());
    }

    /*
     * 计时器类,负责计时
     * 调用方法，直接new一个此类，然后主界面就开始显示计时
     * new Timer();
     */
    private class Timer{
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
            Direction direction = null;
            if(gameMode != 1) direction = snake.getDirection();

            if(key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {          //向右
                if(!quit && direction != Direction.LEFT && gameMode != 1) {
                    snake.setDirection(Direction.RIGHT);
                }
            } else if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {    //向左
                if(!quit && direction != Direction.RIGHT && gameMode != 1) {
                    snake.setDirection(Direction.LEFT);
                }
            } else if(key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {      //向上
                if(!quit && direction != Direction.DOWN && gameMode != 1) {
                    snake.setDirection(Direction.UP);
                }
            } else if(key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {    //向下
                if(!quit && direction != Direction.UP && gameMode != 1) {
                    snake.setDirection(Direction.DOWN);
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

    		//发射子弹
            if (gameMode != 1 && e.isShiftDown() && !pause) {//gameMode = 1表示界面上只有AI蛇
                snake.fire();
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

    private class BGPanel extends JDialog{
        public BGPanel(){
            setTitle("更换游戏背景");//设置窗体标题
            Image img=Toolkit.getDefaultToolkit().getImage("title.png");//窗口图标
            setIconImage(img);
//            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            setModal(true);//设置为模态窗口
            setSize(650,390);
            setResizable(false);
            setLocationRelativeTo(null);
            setVisible(false);

            //添加背景图片
            int back_kind = 6;
            ImageIcon[] background = new ImageIcon[back_kind];
            for(int i = 0; i < back_kind; i++) {
                background[i] = new ImageIcon("background//background" + i + ".png");
                background[i].setImage(background[i].getImage().getScaledInstance(200,110,Image.SCALE_FAST));
            }

            JLabel[] Back_label = new JLabel[back_kind];
            JButton[] choose = new JButton[back_kind];
            JPanel p = new JPanel();
            for(int i = 0; i < back_kind; i++) {
                Back_label[i] = new JLabel(background[i],SwingConstants.LEFT);
                Font f = new Font("微软雅黑", Font.PLAIN, 15);
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

            for(int i = 0; i < back_kind; i++) {
                choose[i].addActionListener(e -> {
                    int rows = gameMap.length, cols = gameMap[0].length;
                    for(int j = 0; j < back_kind; j++) {
                        if(e.getSource() == choose[j]) {
                            background[j] = new ImageIcon("background//background" + j + ".png");
                            background[j].setImage(background[j].getImage().getScaledInstance(cols*pixel_per_unit+pixel_rightBar,rows * pixel_per_unit + 75,Image.SCALE_SMOOTH));
                            background_label.setIcon(background[j]);
                        }
                    }
                });
            }
        }
    }
}