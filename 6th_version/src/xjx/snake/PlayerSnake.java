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
    private int speed = 300;//用于描述蛇移动速度的变量，其实是作为蛇刷新线程的时间用的
    private int defaultSpeed = 300;//默认速度
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
        head.label.setBounds(GameUI.getPixel(head.coor.x, GameUI.padding, GameUI.pixel_per_unit),
                GameUI.getPixel(head.coor.y, GameUI.padding, GameUI.pixel_per_unit), 20, 20);
        GameUI.setMap(0, 0, 1);

        run();
    }

    //蛇身体移动
    public void move(){
        Coordinate head,next_coor = new Coordinate(0,0);
        if(direction == Direction.UP){
            head = body.getFirst().coor;//获取头部
            next_coor = new Coordinate(head.x,head.y - 1);//头部向上移动一个单位后的坐标
        } else if(direction == Direction.DOWN){
            head = body.getFirst().coor;//获取头部
            next_coor = new Coordinate(head.x,head.y + 1);//头部向下移动一个单位后的坐标
        } else if(direction == Direction.LEFT){
            head = body.getFirst().coor;//获取头部
            next_coor = new Coordinate(head.x - 1,head.y);//头部向左移动一个单位后的坐标
        } else if(direction == Direction.RIGHT){
            head = body.getFirst().coor;//获取头部
            next_coor = new Coordinate(head.x + 1,head.y);//头部向右移动一个单位后的坐标
        }

        if(checkDeath(next_coor)) {//判断下一步是否死亡
            new Music("music//over.wav").start();
            GameUI.quit = true;
            GameUI.pause = true;
            GameUI.die = true;
            GameUI.repaint();
        } else {
            Body next_node = new Body(next_coor,headIcon[headIconTag]);
            body.addFirst(next_node);//添头
            GameUI.setMap(next_node.coor.y, next_node.coor.x, 1);
            next_node.label.setVisible(true);
            GameUI.add(next_node.label);

            if(!checkEat(next_coor)) {//没吃到食物就去尾，否则不用去掉，因为添加的头刚好是吃到一个食物后增长的一节
                Body tail = body.pollLast();//去尾
                if (tail != null) {
                    GameUI.setMap(tail.coor.y, tail.coor.x, 0);
                    tail.label.setVisible(false);
                    GameUI.remove(tail.label);
                    //添头去尾实现移动
                }
            }
        }
    }

    //判断一个坐标位置是否是蛇死亡的位置
    public boolean checkDeath(Coordinate coor){
        int rows = GameUI.getMap().length, cols = GameUI.getMap()[0].length;
        return coor.x < 0 || coor.x >= cols || coor.y < 0 || coor.y >= rows || GameUI.getMap()[coor.y][coor.x] == 3;
    }

    public boolean checkEat(Coordinate coor){
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

            GameUI.removeFood(new Coordinate(coor.y, coor.x));
            //刷新界面信息
            GameUI.updateInfos("Score", "" + point);
            GameUI.updateInfos("Length", "" + body.size());
            GameUI.updateInfos("Amount", "" + GameUI.getFoodCoor().size());
            GameUI.setMap(coor.y, coor.x, 1);
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

    public void removeAll(){
        for (Body node : body) {
            node.label.setVisible(false);
            GameUI.remove(node.label);
        }
    }

    public Coordinate searchTarget(Coordinate coor, Direction d){
        int row = coor.y, col = coor.x;
        int[][] gameMap = GameUI.getMap();
        int rows = gameMap.length, cols = gameMap[0].length;
        switch (d){
            case UP:
            {
                for(int j = row; j >= 0; j--){
                    if(gameMap[j][col] == 3){
                        return new Coordinate(col, j);
                    }
                }
            }
            case DOWN:
            {
                for(int j = row; j < rows; j++){
                    if(gameMap[j][col] == 3){
                        return new Coordinate(col, j);
                    }
                }
            }
            case LEFT:{
                for(int i = col; i >= 0; i--){
                    if(gameMap[row][i] == 3){
                        return new Coordinate(i, row);
                    }
                }
            }
            case RIGHT:{
                for(int i = col; i < cols; i++){
                    if(gameMap[row][i] == 3){
                        return new Coordinate(i, row);
                    }
                }
            }
        }

        return new Coordinate(0, 0);
    }

    public void fire(){
        if(bulletNumber > 0){
            System.out.println("Fire a bullet");
            Direction d = getDirection();
            Coordinate coor = body.getFirst().coor;
            Coordinate target = searchTarget(coor, d);//找到火焰的目标
            System.out.println("Target is:" + target.x + "," +target.y);
            new Fire(coor, target, d);

            bulletNumber--;
            GameUI.updateInfos("Weapon", "" + bulletNumber);//刷新界面上显示的子弹数目
        }
    }

    public void show(){
        for (Body node : body) {
            node.label.setBounds(GameUI.getPixel(node.coor.x, GameUI.padding, GameUI.pixel_per_unit),
                    GameUI.getPixel(node.coor.y, GameUI.padding, GameUI.pixel_per_unit), 20, 20);
            node.label.setIcon(bodyIcon[bodyIconTag]);
        }
        Body node = body.getFirst();
        node.label.setIcon(headIcon[headIconTag]);
    }

    public void quit(){
        executor.shutdownNow();//退出线程
    }

    public void run(){
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (!GameUI.pause && !GameUI.quit) {
                move();
                GameUI.PrintMap(GameUI.getMap(), "debug//map.txt");
                show();
            }
        }, 0, speed, TimeUnit.MILLISECONDS);
    }

    public class Fire {
        private Coordinate fireCoor;
        private JLabel fireLabel;
        private Coordinate target;
        private Direction moveDirection;
        private boolean quit = false;

        public Fire(Coordinate snakehead,Coordinate target,Direction d){
            ImageIcon fireIcon = new ImageIcon("image//fire.png");//射击子弹时产生的火焰图标
            fireIcon.setImage(fireIcon.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));//保持图片的清晰
            fireLabel = new JLabel(fireIcon);

            this.target = target;
            this.moveDirection = d;
            //初始化火焰起始坐标
            if(moveDirection == Direction.UP) {
                fireCoor = new Coordinate(snakehead.x,snakehead.y-1);
            } else if(moveDirection == Direction.DOWN) {
                fireCoor = new Coordinate(snakehead.x,snakehead.y+1);
            } else if(moveDirection == Direction.LEFT) {
                fireCoor = new Coordinate(snakehead.x-1,snakehead.y);
            } else if(moveDirection == Direction.RIGHT) {
                fireCoor = new Coordinate(snakehead.x+1,snakehead.y);
            }

            GameUI.add(fireLabel);
            show();
            moveThread();
        }

        public void show(){
            if(fireCoor.x == target.x && fireCoor.y == target.y) {
                int rows = GameUI.getMap().length, cols = GameUI.getMap()[0].length;
                if( target.x >= 0 && target.x < cols && target.y >= 0 && target.y < rows){
                    System.out.println("hit target " + target.x + "," + target.y);
                    new Music("music//explode.wav").start();//击中障碍物播放音效

                    //地图上该位置标记为0
                    GameUI.setMap(target.y, target.x, 0);
                    GameUI.removeBrick(new Coordinate(target.y, target.x));
                }
                fireLabel.setVisible(false);
                GameUI.remove(fireLabel);
                quit = true;
            }
            fireLabel.setVisible(false);
            fireLabel.setBounds(GameUI.getPixel(fireCoor.x, GameUI.padding, GameUI.pixel_per_unit),
                    GameUI.getPixel(fireCoor.y, GameUI.padding, GameUI.pixel_per_unit), 20, 20);
            fireLabel.setVisible(true);
        }

        public void move(){
            if(moveDirection == Direction.UP) {
                fireCoor.y--;
            } else if(moveDirection == Direction.DOWN) {
                fireCoor.y++;
            } else if(moveDirection == Direction.LEFT) {
                fireCoor.x--;
            } else if(moveDirection == Direction.RIGHT) {
                fireCoor.x++;
            }
        }

        public void moveThread(){
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                if(!quit && !GameUI.pause){
                    move();
                    show();
                }else {
                    return;
                }
            }, 0, 50, TimeUnit.MILLISECONDS);
        }
    }
}