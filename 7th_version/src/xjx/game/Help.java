package xjx.game;

import java.awt.*;
import javax.swing.*;

public class Help extends JDialog {
    private static final long serialVersionUID = 4693799019369193520L;
    private JPanel contentPane;
    private Font f = new Font("微软雅黑",Font.PLAIN,15);
    private JScrollPane scroll;

    public Help() {
        setTitle("游戏规则说明");//设置窗体标题
        Image img=Toolkit.getDefaultToolkit().getImage("title.png");//窗口图标
        setIconImage(img);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModal(true);//设置为模态窗口
        setSize(410,380);
        setResizable(false);
        setLocationRelativeTo(null);
        contentPane = new JPanel();// 创建内容面板
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        ShadePanel shadePanel = new ShadePanel();// 创建渐变背景面板
        contentPane.add(shadePanel, BorderLayout.CENTER);// 添加面板到窗体内容面板
        shadePanel.setLayout(null);

        JTextArea J1 = new JTextArea("此份游戏说明是针对第6版本的。游戏说明如下：\n\n" +
                "通过键盘上的方向键或者WASD键来控制贪吃蛇的前进。\n\n" +
                "在游戏界面按ESC键可以直接重新开始游戏，按空格键可以实现暂停和开始。\n\n" +
                "菜单栏的设置菜单可以更改贪吃蛇头部图片，贪吃蛇身体图片，设置贪吃蛇速度，更换游戏背景，" +
                "设置网格和边框是否可见，以及更换游戏模式（AI和玩家蛇同时存在暂不支持。）\n\n" +
                "游戏界面右边会显示你的当前长度，得分，以及当前拥有的子弹数。\n\n" +
                "游戏中有多种食物，它们对应的分值不同，出现的概率也不同。\n\n" +
                "游戏中玩家蛇可以通过按Shift键来发射子弹击毁障碍物。子弹是通过吃特定的食物来获得。\n\n" +
                "吃子弹不增加得分，仅增加长度。子弹产生的概率在所有食物中最低。\n\n" +
                "AI蛇通过A*算法来寻路，目前仅使用了最简单的A*算法，AI蛇在某些情况下会出现死亡。\n\n");
        J1.setFocusable(false);
        J1.setFont(f);
        J1.setEditable(false);
        J1.setOpaque(false);//背景透明
        J1.setLineWrap(true);

        scroll = new JScrollPane(J1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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