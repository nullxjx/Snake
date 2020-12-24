package xjx.game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class Util {
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
    public static void Write2Txt(String str,String filepath) {
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

        for(int i = 0;i < map.length;i++) {
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

    //注意，序号从0开始
    public static int getPixel(int i, int paddind, int pixels_per_unit) {
        //通过方格序号返回其横坐标
        return 1+paddind+i*pixels_per_unit;
    }

    public static int coor_trans(Pos coor, int cols_per_row){
        //把gamemap里面的二维坐标(x,y)转化成一维坐标
        int x = coor.x, y = coor.y;
        return x * cols_per_row + y;
    }
}
