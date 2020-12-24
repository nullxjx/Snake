package xjx.snake;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import xjx.game.*;

class pair{
    Pos p;
    double cost;

    pair(Pos p, double cost){
        this.p = p;
        this.cost = cost;
    }

    int compare_min(pair p2){
        return Double.compare(this.cost, p2.cost);
    }
}

class path{
    Vector<Pos> pos_sets;   //点集合
    Double cost;            //起点到终点的距离，起点为pos_sets中的第一个，终点为pos_sets中的最后一个
    path(Vector<Pos> pos_sets, Double cost){
        this.pos_sets = pos_sets;
        this.cost = cost;
    }

    int campare(path p){return Double.compare(this.cost, p.cost);}
}

public class AISnake {
    private Scene GameUI;//游戏主界面
    private Deque<Body> body = new LinkedList<>();//用于描述蛇身体节点的数组，保存蛇身体各个节点的坐标
    private final ImageIcon headIcon;
    private final ImageIcon bodyIcon;

    private ScheduledExecutorService executor;

    static Comparator<pair> cmp_min = pair::compare_min;//最小值优先
    static Comparator<path> path_cmp_min = path::campare;

    private int mapCols;//地图一行包含多少列，后面坐标转换的时候要用

    public AISnake(Scene GameUI) {
        this.GameUI = GameUI;
        headIcon = new ImageIcon("head//AI_head.png");
        headIcon.setImage(headIcon.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
        bodyIcon = new ImageIcon("body//AI_body.png");
        bodyIcon.setImage(bodyIcon.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));

        int cols = GameUI.getMap()[0].length;
        Body head = new Body(0,cols-1, headIcon);
        body.addFirst(head);
        GameUI.add(head.label);
        head.label.setBounds(Util.getPixel(head.coor.y, GameUI.padding, GameUI.pixel_per_unit),
                Util.getPixel(head.coor.x, GameUI.padding, GameUI.pixel_per_unit), 20, 20);
        GameUI.setMap(0, cols-1, 4);

        mapCols = GameUI.getMap()[0].length;

        run();
    }

    private void writeHistory(){
        JSONObject json = new JSONObject();

        json.put("rows", GameUI.getMap().length);
        json.put("cols", GameUI.getMap()[0].length);

        Vector<Pos> food_coors = GameUI.getAllFoodCoor();
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(food_coors);
        json.put("food", jsonArray);

        List<Pos> body_list = new ArrayList<>();
        for(Body b : body){
            body_list.add(new Pos(b.coor.x, b.coor.y));
        }
        jsonArray = new JSONArray();
        jsonArray.addAll(body_list);
        json.put("body", jsonArray);
        json.put("length", jsonArray.size());

//        System.out.println(json.toJSONString());
        String json_file = "debug//history.txt";
        try {
            Util.Write2Txt(json.toJSONString(), json_file);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void readHistory(){
        Vector<Pos> coors = GameUI.ai_body;
        for(Pos coor : coors){
            //初始化身体结点
            Body head = new Body(coor.x, coor.y, headIcon);
            body.addFirst(head);
            GameUI.add(head.label);
            head.label.setBounds(Util.getPixel(head.coor.y, GameUI.padding, GameUI.pixel_per_unit),
                    Util.getPixel(head.coor.x, GameUI.padding, GameUI.pixel_per_unit), 20, 20);
        }
        show();
        GameUI.updateInfos("AILength", ""+body.size());
    }

    private Vector<Pos> getNeighbors(int[][] map, Pos now){
        int rows = map.length, cols = map[0].length;
        Vector<Pos> res = new Vector<>();
        int[][] dir = new int[][]{ {-1, 0}, {1, 0}, {0, -1}, {0, 1} };//上、下、左、右
        for (int i = 0; i < 4; i++) {
            Pos tmp = new Pos(now.x+dir[i][0], now.y+dir[i][1]);
            if (tmp.x >= 0 && tmp.x < rows && tmp.y >= 0 && tmp.y < cols &&
                    map[tmp.x][tmp.y] != 4 && map[tmp.x][tmp.y] != 3 ) {
                //1表示是玩家蛇，4表示是AI蛇，3表示是障碍物
                //目前允许AI蛇穿过玩家蛇，但是玩家蛇不能穿过AI蛇
                res.add(tmp);
            }
        }
        return res;
    }

    private double calCost(Pos current, Pos next) {
        //返回曼哈顿距离
        return Math.abs(current.x-next.x) + Math.abs(current.y-next.y);
    }

    private double heuristic(Pos goal, Pos next) {
        //返回曼哈顿距离
        return Math.abs(goal.x - next.x) + Math.abs(goal.y - next.y);
    }

    private Vector<Pos> getBFSPath(Pos start, Pos end){
        Queue<Pos> frontier = new LinkedList<>();
        frontier.add(start);
        HashMap<Integer, Pos> came_from = new HashMap<>();//标记当前点是从哪个结点过来的
        came_from.put(Util.coor_trans(start, mapCols), new Pos(-1, -1));

        while(!frontier.isEmpty()){
            Pos current = frontier.poll();
            Vector<Pos> neighbors = getNeighbors(GameUI.getMap(), current);
            for(Pos next : neighbors){
                if(!came_from.containsKey(Util.coor_trans(next, mapCols))){
                    frontier.add(next);
                    came_from.put(Util.coor_trans(next, mapCols), current);
                }
            }
        }

        if(came_from.containsKey(Util.coor_trans(end, mapCols))){
            //计算路径
            Deque<Pos> res = new LinkedList<>();
            Pos tmp = new Pos(end.x, end.y);
            while(tmp.equal(start)){
                tmp = came_from.get(Util.coor_trans(tmp, mapCols));
                res.addFirst(tmp);
            }

            Vector<Pos> path = new Vector<>();
            path.addAll(res);
            return path;
        }else{
            return new Vector<Pos>();
        }
    }

    private path AStar(int[][] map, Pos start, Pos goal){
        PriorityQueue<pair> frontier = new PriorityQueue<>(cmp_min);
        frontier.add(new pair(start, 0));
        HashMap<Integer, Pos> came_from = new HashMap<>();//标记当前点是从哪个结点过来的
        came_from.put(Util.coor_trans(start, mapCols), new Pos(-1, -1));
        HashMap<Integer, Double> cost_so_far = new HashMap<>();//从起始点到当前点的代价
        cost_so_far.put(Util.coor_trans(start, mapCols), 0.);

        while(!frontier.isEmpty()){
            pair current = frontier.poll();
            Pos current_pos = current.p;
            if(current_pos.equal(goal)) break;

            Vector<Pos> neighbors = getNeighbors(map, current_pos);
            for (Pos next : neighbors) {
                double new_cost = cost_so_far.get(Util.coor_trans(current_pos, mapCols)) + calCost(current_pos, next);
                if (!came_from.containsKey(Util.coor_trans(next, mapCols)) ||
                        new_cost < cost_so_far.get(Util.coor_trans(next, mapCols))) {
                    cost_so_far.put(Util.coor_trans(next, mapCols), new_cost);

                    double priority = new_cost + heuristic(goal, next);
                    frontier.add(new pair(next, priority));

                    came_from.put(Util.coor_trans(next, mapCols), current_pos);
                }
            }
        }

        if(cost_so_far.getOrDefault(Util.coor_trans(goal, mapCols), -1.) > 0){
            Vector<Pos> pos_sets = getPath(came_from, start, goal);
            return new path(pos_sets, cost_so_far.getOrDefault(Util.coor_trans(goal, mapCols), -1.));
        }else {
            return new path(null, -1.);
        }
    }

    private Vector<Pos> getPath(HashMap<Integer, Pos> came_from, Pos start, Pos goal) {
        Stack<Pos> s = new Stack<>();
        Vector<Pos> pos_sets = new Vector<>();

        Pos tmp = new Pos(goal.x, goal.y);
        while(!tmp.equal(start)){
            s.add(tmp);
            tmp = came_from.get(Util.coor_trans(tmp, mapCols));
        }
        s.add(start);

        while(!s.empty()){
            pos_sets.add(s.pop());
        }

        return pos_sets;
    }

    private Deque<Pos> virtualSnake(){
        Deque<Pos> v_snake = new LinkedList<>();
        for(Body tmp : body){
            Pos coor = tmp.coor;
            v_snake.add(new Pos(coor.x, coor.y));
        }
        return v_snake;
    }

    private boolean existPath(Pos start, Pos goal, Deque<Pos> v_snake){//判断从start出发是否存在一条到goal的路
        int rows = GameUI.getMap().length, cols = GameUI.getMap()[0].length;
        int[][] tmpMap = new int[rows][cols];
        for(int i = 0; i < rows; i++){
            tmpMap[i] = GameUI.getMap()[i].clone();//注意，一定要是深度复制，不能影响原来的数组
        }

        //由于v_snake是虚拟蛇，它并没有把自己的身体标记在地图当中，所以这里我们得复制原来的地图，并且把虚拟蛇的身体标记上去
        for(Pos body : v_snake){
            tmpMap[body.x][body.y] = 4;//AI蛇身体标记为4
        }
        Vector<Pos> neighbors = getNeighbors(tmpMap, start);

        if(!neighbors.isEmpty()){
            for(Pos next : neighbors){
                tmpMap[next.x][next.y] = 4;
                tmpMap[goal.x][goal.y] = 0;//goal是蛇尾巴，在蛇移动过程中会空出来
                path p = AStar(tmpMap, next, goal);//使用临时地图进行判断
                if(p.cost > 0) return true;
                else{
                    tmpMap[next.x][next.y] = 0;
                }
            }
        }else{
            tmpMap[goal.x][goal.y] = 4;//没有临近位置可以移动，尾巴重新标记为4
            path p = AStar(tmpMap, start, goal);//使用临时地图进行判断
            return p.cost > 0;
        }

        return false;
    }

    private boolean attemptMove(Pos start, Pos target, Vector<Pos> path_){
        Deque<Pos> v_snake = virtualSnake();
        Vector<Pos> path = new Vector<>();//不要修改path_原来的数据，所以先拷贝一份
        for(Pos p : path_){
            path.add(new Pos(p.x, p.y));
        }

        while(!path.isEmpty()){
            Pos next = path.get(0);
            path.remove(0);

            if(next.equal(start)) continue;

            v_snake.addFirst(next);//添头

            if (!next.equal(target)){//没有吃到食物
                v_snake.pollLast();//去尾
            } else {//吃到了食物
                Pos tail = v_snake.getLast();
                Pos head = v_snake.getFirst();

                //判断是否还存在出去的路
                return existPath(head, tail, v_snake);
            }
        }
        return false;
    }

    private Vector<Pos> getDFSPath(Pos start, Pos end) {
        int[][] map = GameUI.getMap();
        boolean[][] visited = new boolean[map.length][map[0].length];
        Vector<Pos> path = new Vector<>();
        dfs(map, start, end, path, visited);

        return path;
    }

    private boolean dfs(int[][] map, Pos start, Pos end, Vector<Pos> path, boolean[][] visited) {
        if(start.equal(end)){
            return true;
        }

        visited[start.x][start.y] = true;//标记走过
        path.add(start);

        //尝试往周围走一步
        Vector<Pos> neighbors = getNeighbors(map, start);
        for(Pos next : neighbors){
            if(!visited[next.x][next.y]){
                if(dfs(map, next, end, path, visited)){
                    return true;
                }
            }
        }

        //如果能执行到此处，说明上面的邻接点都没法访问到，那么就向上回溯一层
        Pos last = path.lastElement();
        visited[last.x][last.y] = false;
        path.remove(last);
        return false;
    }

    private boolean moveToTail(){
        //移动到尾巴处
        Pos head = body.getFirst().coor;
        Pos tail = body.getLast().coor;
        Vector<Pos> path_to_tail = null;

        Vector<Pos> neighbours = getNeighbors(GameUI.getMap(), tail);
        double longest_path_len = 0.;
        for(Pos next : neighbours){
            //注意，由于尾巴在地图中已经被标记为4，所以我们是找不到通往尾巴的路径的
            //所以我们找通往尾巴周围结点的路径
            Vector<Pos> res = getBFSPath(head, next);
            if(res.size() > longest_path_len){
                path_to_tail = res;
                longest_path_len = res.size();
            }
        }

        if(longest_path_len > 1){
            Pos next = path_to_tail.get(1);
            moveOneStep(next);
            return true;
        }else{
            return false;
        }
    }

    private boolean checkEat(Pos next){
        Vector<Pos> food_coors = GameUI.getAllFoodCoor();
        for (Pos food : food_coors) {
            if(food.x == next.x && food.y == next.y) {
                GameUI.removeFood(new Pos(next.x, next.y));
                return true;
            }
        }
        return false;
    }

    private void moveOneStep(Pos next){//朝着next位置移动一步
        Pos next_coor = new Pos(next.x, next.y);
        Body next_node = new Body(next_coor, headIcon);
        body.addFirst(next_node);//添头

        GameUI.setMap(next.x, next.y, 4);
        next_node.label.setVisible(true);
        GameUI.add(next_node.label);

        if(!checkEat(next)){//没有吃到食物
            Body tail = body.pollLast();//去尾
            if (tail != null) {
                GameUI.setMap(tail.coor.x, tail.coor.y, 0);
                tail.label.setVisible(false);
                GameUI.remove(tail.label);
            }
        }else{//吃到了食物
            GameUI.updateInfos("AILength", "" + body.size());
        }

        Util.PrintMap(GameUI.getMap(), "debug//map.txt");
    }

    private void removeAll(){
        for (Body node : body) {
            node.label.setVisible(false);
            GameUI.remove(node.label);
        }
    }

    public void goDie(){
        quit();
        GameUI.pause = true;
        GameUI.quit = true;
        GameUI.ai_die = true;
    }

    public void restart(){
        removeAll();
        quit();
    }

    public void quit(){
        executor.shutdownNow();//退出线程
    }

    private void show(){
        for (Body node : body) {
            node.label.setBounds(Util.getPixel(node.coor.y, GameUI.padding, GameUI.pixel_per_unit),
                    Util.getPixel(node.coor.x, GameUI.padding, GameUI.pixel_per_unit), 20, 20);
            node.label.setIcon(bodyIcon);
        }
        Body node = body.getFirst();
        node.label.setIcon(headIcon);
    }

    private void move(){
        Pos head = body.getFirst().coor;
        PriorityQueue<path> all_path = new PriorityQueue<>(path_cmp_min);
        Vector<Pos> food_coors = GameUI.getAllFoodCoor();

        for (Pos food : food_coors) {
            Pos goal = new Pos(food.x, food.y);//将这个食物设为目标
            path path_to_food = AStar(GameUI.getMap(), head, goal);//寻找通往这个食物的最短路径
            if(path_to_food.cost > 0){//证明这个路径存在
                all_path.add(path_to_food);
            }
        }

        if(all_path.isEmpty()){//不存在通往任何一个食物的路径，则尝试移动到尾巴位置
            if(!moveToTail()){//无法移动到尾巴
                GameUI.updateInfos("Status", "die");
                System.out.println("unable to find path");
                show();
                GameUI.repaint();
                goDie();
            } else {
                GameUI.updateInfos("Status", "wander");
            }
        } else {
            while(!all_path.isEmpty()){
                path p = all_path.poll();
                Vector<Pos> path_to_goal = p.pos_sets;
                Pos goal = path_to_goal.lastElement();

                if(attemptMove(head, goal, path_to_goal)){//能够安全到达这个食物
                    //move one step forward
                    Pos next = path_to_goal.get(1);//通往食物路径的下一步位置
                    moveOneStep(next);
                    GameUI.updateInfos("Status", "eat");
                    return;
                }
            }

            //执行到此处，说明通往所有食物的路径都是不安全的
            //那么，此时应该尝试移动到蛇尾巴位置
            if(!moveToTail()){//无法移动到尾巴
                System.out.println("unable to find path");
                show();
                GameUI.repaint();
                goDie();
            }else {
                GameUI.updateInfos("Status", "wander");
            }
        }
    }

    private void run(){
        executor = Executors.newSingleThreadScheduledExecutor();
        //用于描述蛇移动速度的变量，其实是作为蛇刷新线程的时间用的
        int speed = 50;
        executor.scheduleAtFixedRate(() -> {
            if (!GameUI.pause && !GameUI.quit) {
                move();
                Util.PrintMap(GameUI.getMap(), "debug//map.txt");
//                writeHistory();
                show();
            }
        }, 0, speed, TimeUnit.MILLISECONDS);
    }
}
