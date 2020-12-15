package xjx.game;

//坐标数据结构，用于表示蛇身体，食物，障碍物的坐标
//这里的坐标用方块的序号表示，实际显示时再换成屏幕真实坐标(即像素点)
public class Coordinate {
    public int x,y;

    public Coordinate(int x0,int y0){
        x = x0;
        y = y0;
    }

    public Coordinate(Coordinate temp){
        x = temp.x;
        y = temp.y;
    }
}
