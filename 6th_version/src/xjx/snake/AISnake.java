package xjx.snake;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import xjx.game.*;

class pos{
    int x;
    int y;
    pos(int x, int y){
        this.x = x;
        this.y = y;
    }

    public pos sub(pos p){
        return new pos(this.x-p.x, this.y-p.y);
    }

    public pos sub(int[] v){
        return new pos(this.x-v[0], this.y-v[1]);
    }

    public pos add(pos p){
        return new pos(this.x+p.x, this.y+p.y);
    }

    public pos add(int[] v){
        return new pos(this.x+v[0], this.y+v[1]);
    }

    public boolean equal(pos p){
        return p.x == this.x && p.y == this.y;
    }
}

class pair{
    pos p;
    double cost;

    pair(pos p, double cost){
        this.p = p;
        this.cost = cost;
    }

    int compare(pair p2){
        return Double.compare(this.cost, p2.cost);
    }
}

class path{
    HashMap<Integer, pos> came_from;
    Double cost;

    path(HashMap<Integer, pos> came_from, Double cost){
        this.came_from = came_from;
        this.cost = cost;
    }
}

public class AISnake {
    private Scene GameUI;//母窗体,即游戏主界面
    private Deque<Body> body = new LinkedList<>();//用于描述蛇身体节点的数组，保存蛇身体各个节点的坐标
    private ImageIcon headIcon;
    private ImageIcon bodyIcon;

    private Vector<pos> path_pos = new Vector<>();
    private Vector<JLabel> path_labels = new Vector<>();
    private pos target;

    private ScheduledExecutorService executor;

    public AISnake(Scene GameUI) {
        this.GameUI = GameUI;
        headIcon = new ImageIcon("head//AI_head.png");
        headIcon.setImage(headIcon.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));//保持图片的清晰
        bodyIcon = new ImageIcon("body//AI_body.png");
        bodyIcon.setImage(bodyIcon.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));//保持图片的清晰

        int cols = GameUI.getMap()[0].length;
        Body head = new Body(cols-1,0,headIcon);
        body.addFirst(head);
        GameUI.add(head.label);
        head.label.setBounds(GameUI.getPixel(head.coor.x, GameUI.padding, GameUI.pixel_per_unit),
                GameUI.getPixel(head.coor.y, GameUI.padding, GameUI.pixel_per_unit), 20, 20);
        GameUI.setMap(0, cols-1, 4);

        long stime = System.currentTimeMillis();
        FindPath(new pos(0, cols-1));
        long etime = System.currentTimeMillis();
        System.out.printf("执行时长：%d 毫秒.", (etime - stime));

        run();
    }

    public void show(){
        for (Body node : body) {
            node.label.setBounds(GameUI.getPixel(node.coor.x, GameUI.padding, GameUI.pixel_per_unit),
                    GameUI.getPixel(node.coor.y, GameUI.padding, GameUI.pixel_per_unit), 20, 20);
            node.label.setIcon(bodyIcon);
        }
        Body node = body.getFirst();
        node.label.setIcon(headIcon);
    }

    public int coor_trans(pos p){
        //把gamemap里面的二维坐标(x,y)转化成一维坐标
        int x = p.x, y = p.y;
        int cols = GameUI.getMap()[0].length;
        return x * cols + y;
    }

    static Comparator<pair> cmp = pair::compare;

    Vector<pos> getNeighbors(int[][] map, pos now){
        int rows = map.length, cols = map[0].length;
        Vector<pos> res = new Vector<>();
        int[][] dir = new int[][]{ {-1, 0}, {1, 0}, {0, -1}, {0, 1} };//上、下、左、右
        for (int i = 0; i < 4; i++) {
            pos tmp = now.add(dir[i]);
            if (tmp.x >= 0 && tmp.x < rows && tmp.y >= 0 && tmp.y < cols &&
                    map[tmp.x][tmp.y] != 4 && map[tmp.x][tmp.y] != 3 ) {
                //1表示是玩家蛇，4表示是AI蛇，3表示是障碍物
                //目前允许AI蛇穿过玩家蛇，但是玩家蛇不能穿过AI蛇
                res.add(tmp);
            }
        }
        return res;
    }

    double calCost(pos current, pos next) {
        //返回曼哈顿距离
        return Math.abs(current.x-next.x) + Math.abs(current.y-next.y);
    }

    double heuristic(pos goal, pos next) {
        //返回曼哈顿距离
        return Math.abs(goal.x - next.x) + Math.abs(goal.y - next.y);
    }

    public path AStar(int[][] map, pos start, pos goal){
        PriorityQueue<pair> frontier = new PriorityQueue<>(cmp);
        frontier.add(new pair(start, 0));

        HashMap<Integer, pos> came_from = new HashMap<>();
        came_from.put(coor_trans(start), new pos(-1, -1));

        //从起始点到当前点的代价
        HashMap<Integer, Double> cost_so_far = new HashMap<>();
        cost_so_far.put(coor_trans(start), 0.);

        while(!frontier.isEmpty()){
            pair current = frontier.poll();
            pos current_pos = current.p;
            if(current_pos == goal) break;

            Vector<pos> neighbors = getNeighbors(map, current_pos);
            for (pos next : neighbors) {
                double new_cost = cost_so_far.get(coor_trans(current_pos)) + calCost(current_pos, next);
                if (!came_from.containsKey(coor_trans(next)) || new_cost < cost_so_far.get(coor_trans(next))) {
                    cost_so_far.put(coor_trans(next), new_cost);

                    double priority = new_cost + heuristic(goal, next);
                    frontier.add(new pair(next, priority));

                    came_from.put(coor_trans(next), current_pos);
                }
            }
        }

        return new path(came_from, cost_so_far.getOrDefault(coor_trans(goal), -1.));
    }

    int checkDirection(pos now, pos other){
        int[][] direction = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };//上、下、左、右
        for(int i = 0; i < 4; i++){
            if(now.add(direction[i]).equal(other))
                return i;
        }

        return -1;
    }

    public void PrintArrow(pos prior, pos now, pos next){
        if(prior != null && next != null){
            int prior_now = checkDirection(prior, now);
//        int now_next = checkDirection(now, next);
            //0上、1下、2左、3右
            String[] dir = new String[]{"up", "down", "left", "right"};
            String icon = "image//arrow_" + dir[prior_now] + "_" + dir[prior_now] + ".png";
            ImageIcon arrow_icon = new ImageIcon(icon);
            arrow_icon.setImage(arrow_icon.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));//保持图片的清晰
            JLabel arrow_label = new JLabel(arrow_icon);
            GameUI.add(arrow_label);
            arrow_label.setBounds(GameUI.getPixel(now.y, GameUI.padding, GameUI.pixel_per_unit),
                    GameUI.getPixel(now.x, GameUI.padding, GameUI.pixel_per_unit), 20, 20);
            path_labels.add(arrow_label);
        }
    }

    public void PrintPath(pos start, pos goal, Vector<pos> path){
//        for(pos p : path){
//            System.out.println(p.x + " " + p.y);
//        }

        System.out.println("path len: " + path.size());
        for(int i = 0; i < path.size(); i++){
            if(i == 0){
                PrintArrow(null, path.get(i), path.get(i+1));
            } else if(i == path.size()-1){
                PrintArrow(path.get(i-1), path.get(i), null);
            } else {
                PrintArrow(path.get(i-1), path.get(i), path.get(i+1));
            }
        }
    }

    public void CalPath(path p, pos goal, pos start) {
        HashMap<Integer, pos> came_from = p.came_from;
        Stack<pos> s = new Stack<>();
        Vector<pos> res = new Vector<>();
        pos tmp = new pos(goal.x, goal.y);
        while(!tmp.equal(start)){
            s.add(tmp);
            tmp = came_from.get(coor_trans(tmp));
        }
        s.add(start);

        while(!s.empty()){
            pos p_ = s.pop();
            res.add(p_);
            if(!p_.equal(start)) path_pos.add(p_);
        }

        PrintPath(start, goal, res);
    }

    public void FindPath(pos start){
        Vector<Coordinate> food_coors = GameUI.getFoodCoor();
        double min_cost = 99999999.;//需要保证足够大
        path min_path = null;
        pos end = null;
        for (Coordinate next : food_coors) {
            pos goal = new pos(next.x, next.y);
            path p = AStar(GameUI.getMap(), start, goal);
            if(p.cost > 0){
                if(p.cost < min_cost) {
                    min_cost = p.cost;
                    min_path = p;
                    end = goal;
                    target = new pos(goal.x, goal.y);
                }
            }else{
                //证明没有找到路径
                System.out.println("unable to find path");
                show();
                GameUI.repaint();
                goDie();
            }
        }

        //在界面上显示食物坐标
        GameUI.updateInfos("FoodCoor", "(" + target.x + "," + target.y + ")");
        CalPath(min_path, end, start);
    }

    public void FindNewPath(){
        removeAllPath();
        path_labels.clear();
        path_pos.clear();
        Coordinate head = body.getFirst().coor;
        FindPath(new pos(head.y, head.x));
    }

    public Coordinate getTarget() {
        return new Coordinate(target.x, target.y);
    }

    public void goDie(){
        quit();
        GameUI.pause = true;
        GameUI.quit = true;
        GameUI.ai_die = true;
    }

    public void move(){
        if(!path_pos.isEmpty()){
            pos next = path_pos.get(0);
            path_pos.remove(0);
            Coordinate next_coor = new Coordinate(next.y, next.x);

            if(!next.equal(target)){
                //去除界面上的箭头
                JLabel arrow_label = path_labels.get(0);
                path_labels.remove(0);
                arrow_label.setVisible(false);
                GameUI.remove(arrow_label);
            }

            Body next_node = new Body(next_coor, headIcon);
            body.addFirst(next_node);//添头
            GameUI.setMap(next_node.coor.y, next_node.coor.x, 4);
            GameUI.PrintMap(GameUI.getMap(), "debug//map.txt");
            next_node.label.setVisible(true);
            GameUI.add(next_node.label);

            if (!next.equal(target)){//没有吃到食物
                Body tail = body.pollLast();//去尾
                if (tail != null) {
                    GameUI.setMap(tail.coor.y, tail.coor.x, 0);
                    GameUI.PrintMap(GameUI.getMap(), "debug//map.txt");
                    tail.label.setVisible(false);
                    GameUI.remove(tail.label);
                    //添头去尾实现移动
                }
            } else {//吃到了食物
                new Music("music//eat.wav").start();
                GameUI.updateInfos("AILength", "" + body.size());
                GameUI.removeFood(new Coordinate(next_node.coor.y, next_node.coor.x));
                GameUI.updateInfos("Amount", "" + GameUI.getFoodCoor().size());
                System.out.println("AI eat food!");

                path_labels.clear();
                path_pos.clear();
                target = null;
                //重新寻路
                FindPath(new pos(next.x, next.y));
            }
        }
    }

    public void removeAll(){
        for (Body node : body) {
            node.label.setVisible(false);
            GameUI.remove(node.label);
        }
    }

    public void removeAllPath(){
        for (JLabel label : path_labels){
            label.setVisible(false);
            GameUI.remove(label);
        }
    }

    public void quit(){
        executor.shutdownNow();//退出线程
    }

    public void run(){
        executor = Executors.newSingleThreadScheduledExecutor();
        //用于描述蛇移动速度的变量，其实是作为蛇刷新线程的时间用的
        int speed = 500;
        executor.scheduleAtFixedRate(() -> {
            if (!GameUI.pause && !GameUI.quit) {
                move();
                GameUI.PrintMap(GameUI.getMap(), "debug//map.txt");
                show();
            }
        }, 0, speed, TimeUnit.MILLISECONDS);
    }
}
