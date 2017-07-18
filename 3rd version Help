package xjx;

import java.awt.*;
import javax.swing.*;

public class Help extends JDialog {
    private static final long serialVersionUID = 4693799019369193520L;
    private JPanel contentPane;
    private Font f = new Font("微软雅黑",Font.PLAIN,15);
	
    public Help() {
        setTitle("游戏规则说明");//设置窗体标题
        Image img=Toolkit.getDefaultToolkit().getImage("title.png");//窗口图标
        setIconImage(img);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModal(true);//设置为模态窗口
        setSize(400,300);
        setResizable(false);
        setLocationRelativeTo(null);
        contentPane = new JPanel();// 创建内容面板
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        ShadePanel shadePanel = new ShadePanel();// 创建渐变背景面板
        contentPane.add(shadePanel, BorderLayout.CENTER);// 添加面板到窗体内容面板
        shadePanel.setLayout(null);
        
        JTextArea J1 = new JTextArea("注意，此份游戏说明是针对第三版本的，后续游戏会有更新，游戏说明也会相应进行更新\n"
        		+ "此游戏基于诺基亚原版设计，"
        		+ "经过了本人的一些思考和改造，有了一些新玩法。\n游戏说明如下：\n通过键盘上的方向键来控制蛇前进的方向，长按可以加速。游戏界面"
        		+ "按ESC键可以直接重新开始游戏。按空格键可以实现暂停和开始。菜单栏的设置菜单可以更改蛇头，蛇身，选择速度"
        		+ "，更换背景，以及设置网格是否可见。游戏界面右边会显示你的当前长度以及得分。游戏中有多种食物，它们对应的"
        		+ "分值不同，出现的概率也不同，而且食物会在相应时间内自动移动或者消失。所以，请随时加速。");
        J1.setFocusable(false);
    	J1.setFont(f);
    	J1.setEditable(false);
    	J1.setOpaque(false);//背景透明
    	J1.setLineWrap(true);
    	shadePanel.add(J1);
    	J1.setBounds(10, 10, 380, 280);
    	setVisible(true);
    }
    
    public static void main(String[] args) {
		new Help();
	}
}
