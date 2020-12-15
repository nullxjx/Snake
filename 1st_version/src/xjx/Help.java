package xjx;

import java.awt.*;
import javax.swing.*;

public class Help extends JDialog {
    public Help() {
        setTitle("游戏规则说明");//设置窗体标题
        Image img=Toolkit.getDefaultToolkit().getImage("title.png");//窗口图标
        setIconImage(img);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModal(true);//设置为模态窗口
        setSize(410,380);
        setResizable(false);
        setLocationRelativeTo(null);
        JPanel contentPane = new JPanel();// 创建内容面板
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        ShadePanel shadePanel = new ShadePanel();// 创建渐变背景面板
        contentPane.add(shadePanel, BorderLayout.CENTER);// 添加面板到窗体内容面板
        shadePanel.setLayout(null);

        JTextArea J1 = new JTextArea("此份游戏说明是针对贪吃蛇第1版本的。\n\n" +
                "游戏说明如下：\n游戏中贪吃蛇的头部是一个红色方块，贪吃蛇的身体结点是渐变色的方块。" +
                "食物是绿色的圆形。您可以通过键盘上的方向键或者WASD键来控制蛇的移动。" +
                "在游戏界面按ESC键可以直接重新开始游戏，按空格键可以实现暂停和开始。" +
                "菜单栏的设置菜单可以设置网格以及边框是否可见。游戏界面右边会显示你的当前长度和当前所花时间\n");
        J1.setFocusable(false);
        Font f = new Font("微软雅黑", Font.PLAIN, 15);
        J1.setFont(f);
        J1.setEditable(false);
        J1.setOpaque(false);//背景透明
        J1.setLineWrap(true);

        JScrollPane scroll = new JScrollPane(J1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createTitledBorder("How to play"));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);//JScrollPane设置成透明需加上这一行
        shadePanel.add(scroll);
        scroll.setBounds(10, 10, 385, 330);

        setVisible(true);
    }

    public static void main(String[] args) {
        new Help();
    }
}