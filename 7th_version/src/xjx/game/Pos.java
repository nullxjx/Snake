package xjx.game;

public class Pos {
    public int x;
    public int y;
    /*
    * x,y分别表示该点在数组中的行号和列号
    * */

    public Pos(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Pos sub(Pos p){
        return new Pos(this.x-p.x, this.y-p.y);
    }

    public Pos add(Pos p){
        return new Pos(this.x+p.x, this.y+p.y);
    }

    public boolean equal(Pos p){
        return p.x == this.x && p.y == this.y;
    }

    public int getX(){
        return this.x;
    }

    public void setX(int x){
        this.x = x;
    }

    public int getY(){
        return this.y;
    }

    public void setY(int y){
        this.y = y;
    }
}
