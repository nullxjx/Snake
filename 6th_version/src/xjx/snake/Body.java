package xjx.snake;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import xjx.game.*;

//身体节点的数据结构
public class Body {
    Coordinate coor;//坐标
    JLabel label;
    public Body(int x, int y, ImageIcon icon) {
        coor = new Coordinate(x,y);
        label = new JLabel(icon);
    }

    public Body(Coordinate coor, ImageIcon icon){
        this.coor = coor;
        label = new JLabel(icon);
    }

    public Body(Body temp){
        coor = temp.coor;
        label = temp.label;
    }
}
