package xjx;

import java.util.LinkedList;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Snake {
    private Scene GameUI;//母窗体,即游戏主界面
    public Direction direction = Direction.RIGHT;//蛇当前前进的方向,初始化默认向右移动
    private Deque<Coordinate> body = new LinkedList<>();//用于描述蛇身体节点的数组，保存蛇身体各个节点的坐标
    private ScheduledExecutorService executor;//刷新线程
    private Coordinate food;//食物坐标

    public Snake(Scene GameUI){
        this.GameUI = GameUI;
        Coordinate head = new Coordinate(0, 0);//初始化头部在(0,0)位置
        body.addFirst(head);
        produceFood();
        run();
    }

    public Coordinate randomCoor(){
        int rows = GameUI.height, cols = GameUI.width;
        Random rand = new Random();
        Coordinate res;
        int x = rand.nextInt(cols-1);
        int y = rand.nextInt(rows-1);

        while(true) {
            boolean tag = false;
            for(Coordinate coor : body){
                if(x == coor.y && y == coor.x){
                    x = rand.nextInt(cols-1);
                    y = rand.nextInt(rows-1);
                    tag = true;
                    break;
                }
            }

            if(!tag){
                break;
            }
        }
        res = new Coordinate(x, y);
        return res;
    }

    //蛇身体移动
    public void move(){
        Coordinate head, next_coor = new Coordinate(0,0);
        if(direction == Direction.UP){
            head = body.getFirst();//获取头部
            next_coor = new Coordinate(head.x,head.y - 1);//头部向上移动一个单位后的坐标
        } else if(direction == Direction.DOWN){
            head = body.getFirst();//获取头部
            next_coor = new Coordinate(head.x,head.y + 1);//头部向下移动一个单位后的坐标
        } else if(direction == Direction.LEFT){
            head = body.getFirst();//获取头部
            next_coor = new Coordinate(head.x - 1,head.y);//头部向左移动一个单位后的坐标
        } else if(direction == Direction.RIGHT){
            head = body.getFirst();//获取头部
            next_coor = new Coordinate(head.x + 1,head.y);//头部向右移动一个单位后的坐标
        }

        if(checkDeath(next_coor)) {//判断下一步是否死亡
            new Music("music//over.wav").start();
            GameUI.quit = true;
            GameUI.pause = true;
            GameUI.die = true;
            GameUI.repaint();
        } else {
            Coordinate next_node = new Coordinate(next_coor);
            body.addFirst(next_node);//添头
            if(!checkEat(next_coor)) {//没吃到食物就去尾，否则不用去掉，因为添加的头刚好是吃到一个食物后增长的一节
                body.pollLast();//去尾
            }else{
                new Music("music//eat.wav").start();
                GameUI.updateLength(body.size());
                produceFood();
            }
        }
    }

    //判断一个坐标位置是否是蛇死亡的位置
    public boolean checkDeath(Coordinate coor){
        int rows = GameUI.height, cols = GameUI.width;
        return coor.x < 0 || coor.x >= cols || coor.y < 0 || coor.y >= rows;
    }

    public boolean checkEat(Coordinate coor){
        return food.x == coor.x && food.y == coor.y;
    }

    public Deque<Coordinate> getBodyCoors(){
        return body;
    }

    public Coordinate getFoodCoor(){
        return food;
    }

    public void produceFood(){
        food = randomCoor();
    }

    public void show(){
        GameUI.repaint();
    }

    public void quit(){
        executor.shutdownNow();//退出线程
    }

    public void run(){
        executor = Executors.newSingleThreadScheduledExecutor();
        int speed = 500;//用于描述蛇移动速度的变量，其实是作为蛇刷新线程的时间用的
        executor.scheduleAtFixedRate(() -> {
            if (!GameUI.pause && !GameUI.quit) {
                move();
                show();
            }
        }, 0, speed, TimeUnit.MILLISECONDS);
    }
}
