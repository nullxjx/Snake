package xjx.snake;

import xjx.game.*;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Fire {
    private Pos fireCoor;
    private JLabel fireLabel;
    private Pos target;
    private Direction moveDirection;
    private boolean quit = false;
    private Scene GameUI;

    public Fire(Scene GameUI, Pos initPos, Pos target, Direction d){
        ImageIcon fireIcon = new ImageIcon("image//fire.png");//射击子弹时产生的火焰图标
        fireIcon.setImage(fireIcon.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH));//保持图片的清晰
        fireLabel = new JLabel(fireIcon);

        this.GameUI = GameUI;
        this.target = target;
        this.moveDirection = d;
        //初始化火焰起始坐标
        this.fireCoor = new Pos(initPos.x, initPos.y);
        GameUI.add(fireLabel);
        run();
    }

    public void show(){
        if(fireCoor.x == target.x && fireCoor.y == target.y) {
            int rows = GameUI.getMap().length, cols = GameUI.getMap()[0].length;
            if( target.x >= 0 && target.x < rows && target.y >= 0 && target.y < cols){
                System.out.println("hit target " + target.x + "," + target.y);
                new Music("music//explode.wav").start();//击中障碍物播放音效

                //地图上该位置标记为0
                GameUI.setMap(target.x, target.y, 0);
                GameUI.removeBrick(new Pos(target.x, target.y));
            }
            fireLabel.setVisible(false);
            GameUI.remove(fireLabel);
            quit = true;
        }
        fireLabel.setBounds(Util.getPixel(fireCoor.y, GameUI.padding, GameUI.pixel_per_unit),
                Util.getPixel(fireCoor.x, GameUI.padding, GameUI.pixel_per_unit), 20, 20);
        fireLabel.setVisible(true);
    }

    public void move(){
        if(moveDirection == Direction.UP) {
            fireCoor.x--;
        } else if(moveDirection == Direction.DOWN) {
            fireCoor.x++;
        } else if(moveDirection == Direction.LEFT) {
            fireCoor.y--;
        } else if(moveDirection == Direction.RIGHT) {
            fireCoor.y++;
        }
    }

    public void run(){
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