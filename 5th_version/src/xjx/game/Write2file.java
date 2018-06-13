package xjx.game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Write2file {
	//字符串写出到文本
    public static void  Write2Txt(String str,String filepath) throws Exception{
        FileWriter fw = null;
        String path = filepath;
        File f = new File(path);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            fw = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(fw);
            // FileOutputStream fos = new FileOutputStream(f); 
            // OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8"); 
            out.write(str.toString());
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
    	
    	for(int i = 0;i < map.length;i++)
    	{
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
}
