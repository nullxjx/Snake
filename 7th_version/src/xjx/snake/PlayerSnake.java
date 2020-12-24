package xjx.snake;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.util.LinkedList;
import java.awt.Image;
import java.util.Deque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import xjx.game.*;

public class PlayerSnake {
    private Scene GameUI;//母窗体,即游戏主界面
    private Direction direction = Direction.RIGHT;//蛇当前前进的方向,初始化默认向右移动
    private int speed = 500;//用于描述蛇移动速度的变量，其实是作为蛇刷新线程的时间用的
    private int defaultSpeed = 500;//默认速度
    private Deque<Body> body = new LinkedList<>();//用于描述蛇身体节点的数组，保存蛇身体各个节点的坐标
    private int point = 0;//当前蛇得了多少分
    private int bulletNumber = 5;//蛇的子弹数目

    private ImageIcon[] headIcon = new ImageIcon[4];//表示蛇头的四张图片
    private int headIconTag = 0;//头部默认加载第0张图片
    private ImageIcon[] bodyIcon = new ImageIcon[4];//表示蛇头的四张图片
    private int bodyIconTag = 0;//身体默认加载第0张图片

    private ScheduledExecutorService executor;//刷新线程

    public PlayerSnake(Scene GameUI){
        this.GameUI = GameUI;

        //加载4张蛇头和4张蛇身体图片
        for(int i = 0;i < 4;i++) {
            headIcon[i] = new ImageIcon("head//head" + i + ".png");
            headIcon[i].setImage(headIcon[i].getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));//保持图片的清晰

            bodyIcon[i] = new ImageIcon("body//body" + i + ".png");
            bodyIcon[i].setImage(bodyIcon[i].getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));//保持图片的清晰
        }

        Body head = new Body(0,0,headIcon[headIconTag]);//初始化头部在(0,0)位置
        body.addFirst(head);
        GameUI.add(head.label);
        head.label.setBounds(Util.getPixel(head.coor.y, GameUI.padding, GameUI.pixel_per_unit),
                Util.getPixel(head.coor.x, GameUI.padding, GameUI.pixel_per_unit), 20, 20);
        GameUI.setMap(0, 0, 1);

        run();
    }

    public boolean checkDeath(Pos coor){
        int[][] map = GameUI.getMap();
        int rows = map.length, cols = map[0].length;
        int x = coor.x, y = coor.y;
        /*
        * 死亡条件：
        * 1. 跑出游戏界面
        * 2. 碰到障碍物
        * 3. 碰到自己
        * 4. 碰到AI
        * */
        return x < 0 || x >= rows || y < 0 || y >= cols || map[x][y] == 1 || map[x][y] == 3 || map[x][y] == 4;
    }

    public boolean checkEat(Pos coor){
        int _point = GameUI.getFoodPoint(coor);
        if(_point == -1){//没吃到食物
            return false;
        } else {//吃到了食物
            new Music("music//eat.wav").start();
            point += _point;
            if(_point == 0) {//食物是子弹
                bulletNumber ++;
                GameUI.updateInfos("Weapon", "" + bulletNumber);
            }

            GameUI.removeFood(new Pos(coor.x, coor.y));
            //刷新界面信息
            GameUI.updateInfos("Score", "" + point);
            GameUI.updateInfos("Length", "" + body.size());
            GameUI.updateInfos("Amount", "" + GameUI.getAllFoodCoor().size());
            GameUI.setMap(coor.x, coor.y, 1);
            return true;
        }
    }

    public void setDirection(Direction direction){
        this.direction = direction;
    }

    public Direction getDirection(){
        return direction;
    }

    public void resetSpeed(){
        this.speed = defaultSpeed;
        executor.shutdownNow();
        run();
    }

    public void setDefaultSpeed(int speed){
        this.defaultSpeed = speed;
    }

    public void setHeadIcon(int tag){
        headIconTag = tag;
        show();
        GameUI.repaint();
    }

    public void setBodyIcon(int tag){
        bodyIconTag = tag;
        show();
        GameUI.repaint();
    }

    public void removeAll(){
        for (Body node : body) {
            node.label.setVisible(false);
            GameUI.remove(node.label);
        }
    }

    public Pos searchTarget(Pos coor, Direction d){
        int row = coor.x, col = coor.y;
        int[][] gameMap = GameUI.getMap();
        int rows = gameMap.length, cols = gameMap[0].length;
        switch (d){
            case UP:
            {
                for(int i = row; i >= 0; i--){
                    if(gameMap[i][col] == 3){
                        return new Pos(i, col);
                    }
                }
            }
            case DOWN:
            {
                for(int i = row; i < rows; i++){
                    if(gameMap[i][col] == 3){
                        return new Pos(i, col);
                    }
                }
            }
            case LEFT:{
                for(int j = col; j >= 0; j--){
                    if(gameMap[row][j] == 3){
                        return new Pos(row, j);
                    }
                }
            }
            case RIGHT:{
                for(int j = col; j < cols; j++){
                    if(gameMap[row][j] == 3){
                        return new Pos(row, j);
                    }
                }
            }
        }

        return new Pos(0, 0);
    }

    public void fire(){
        if(bulletNumber > 0){
            System.out.println("Fire a bullet");
            Direction d = getDirection();
            Pos coor = body.getFirst().coor;
            Pos target = searchTarget(coor, d);//找到火焰的目标
            System.out.println("Target is:" + target.x + "," +target.y);

            new Fire(this.GameUI, coor, target, d);

            bulletNumber--;
            GameUI.updateInfos("Weapon", "" + bulletNumber);//刷新界面上显示的子弹数目
        }
    }

    public void restart(){
        removeAll();
        quit();
    }

    public void quit(){
        executor.shutdownNow();//退出线程
    }

    public void show(){
        for (Body node : body) {
            node.label.setBounds(Util.getPixel(node.coor.y, GameUI.padding, GameUI.pixel_per_unit),
                    Util.getPixel(node.coor.x, GameUI.padding, GameUI.pixel_per_unit), 20, 20);
            node.label.setIcon(bodyIcon[bodyIconTag]);
        }
        Body node = body.getFirst();
        node.label.setIcon(headIcon[headIconTag]);
    }

    public void move(){
        Pos head,next_coor = new Pos(0,0);
        if(direction == Direction.UP){
            head = body.getFirst().coor;//获取头部
            next_coor = new Pos(head.x-1, head.y);//头部向上移动一个单位后的坐标
        } else if(direction == Direction.DOWN){
            head = body.getFirst().coor;//获取头部
            next_coor = new Pos(head.x+1, head.y);//头部向下移动一个单位后的坐标
        } else if(direction == Direction.LEFT){
            head = body.getFirst().coor;//获取头部
            next_coor = new Pos(head.x, head.y-1);//头部向左移动一个单位后的坐标
        } else if(direction == Direction.RIGHT){
            head = body.getFirst().coor;//获取头部
            next_coor = new Pos(head.x, head.y+1);//头部向右移动一个单位后的坐标
        }

        if(checkDeath(next_coor)) {//判断下一步是否死亡
            new Music("music//over.wav").start();
            GameUI.quit = true;
            GameUI.pause = true;
            GameUI.die = true;
            GameUI.repaint();
        } else {
            Body next_node = new Body(next_coor, headIcon[headIconTag]);
            body.addFirst(next_node);//添头
            GameUI.setMap(next_coor.x, next_coor.y, 1);
            next_node.label.setVisible(true);
            GameUI.add(next_node.label);

            if(!checkEat(next_coor)) {//没吃到食物就去尾，否则不用去掉，因为添加的头刚好是吃到一个食物后增长的一节
                Body tail = body.pollLast();//去尾
                if (tail != null) {
                    GameUI.setMap(tail.coor.x, tail.coor.y, 0);
                    tail.label.setVisible(false);
                    GameUI.remove(tail.label);
                    //添头去尾实现移动
                }
            }else{
                GameUI.removeFood(new Pos(next_coor.x, next_coor.y));
            }
        }
    }

    public void run(){
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (!GameUI.pause && !GameUI.quit) {
                move();
                show();
                Util.PrintMap(GameUI.getMap(), "debug//map.txt");
            }
        }, 0, speed, TimeUnit.MILLISECONDS);
    }
}