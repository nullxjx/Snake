package xjx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

class ShadePanel extends JPanel {

    private static final long serialVersionUID = -2644424271663261406L;

    public ShadePanel() {
        super();
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g1) {// 重写绘制组件外观
        Graphics2D g = (Graphics2D) g1;
        super.paintComponent(g);// 执行超类方法
        int width = getWidth();// 获取组件大小
        int height = getHeight();
        // 创建填充模式对象
        GradientPaint paint = new GradientPaint(0, 0, Color.CYAN, 0, height,Color.MAGENTA);//实现颜色渐变
        //GradientPaint paint = new GradientPaint(0, 0, Color.red, 0, height,Color.blue);
        g.setPaint(paint);// 设置绘图对象的填充模式
        g.fillRect(0, 0, width, height);// 绘制矩形填充控件界面
    }
}

public class About extends JDialog {
    public About() {
        setTitle("关于");//设置窗体标题
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

        JTextArea J1 = new JTextArea("版本：2017_07_15_1.0.0\n开发者：XJX\n开发语言：Java\n"
                + "Email: thexjx@outlook.com");
        J1.setFocusable(false);
        Font f2 = new Font("微软雅黑", Font.PLAIN, 20);
        J1.setFont(f2);
        J1.setEditable(false);
        J1.setOpaque(false);//背景透明
        shadePanel.add(J1);
        J1.setBounds(10, 10, 400, 180);
        ImageIcon icon = new ImageIcon("image//title.png");
        icon.setImage(icon.getImage().getScaledInstance(120,120,Image.SCALE_SMOOTH));//保持图片的清晰
        JLabel label = new JLabel(icon);
        shadePanel.add(label);
        label.setBounds(270, 0, 130, 130);

        JPanel p = new JPanel();
        p.setBounds(5, 130, 380, 1);
        p.setBorder(BorderFactory.createLineBorder(Color.black));
        shadePanel.add(p);

        JLabel J2 = new JLabel("欢迎访问我的主页:");
        J2.setBounds(10, 145, 200, 30);
        J2.setFont(f2);
        shadePanel.add(J2);

        JLabel MyGithub_Label = new JLabel("Github:");
        MyGithub_Label.setFont(f2);
        final JLabel MyGithub = new JLabel("https://github.com/njuxjx");
        MyGithub.setFont(f2);
        MyGithub.setBackground(Color.white);
        MyGithub.addMouseListener(new InternetMonitor());
        JLabel MyCnBlog_Label = new JLabel("博客园:");
        MyCnBlog_Label.setFont(f2);
        final JLabel MyCnBlog = new JLabel("http://www.cnblogs.com/njuxjx");
        MyCnBlog.setFont(f2);
        MyCnBlog.addMouseListener(new InternetMonitor());
        JTextArea Copyright = new JTextArea("");
        Copyright.setFocusable(false);
        Copyright.setOpaque(false);
        Font f1 = new Font("微软雅黑", Font.PLAIN, 15);
        Copyright.setFont(f1);
        Copyright.setEditable(false);

        shadePanel.add(MyGithub_Label);
        MyGithub_Label.setBounds(10, 180, 400, 20);
        shadePanel.add(MyGithub);
        MyGithub.setBounds(10, 200, 400, 30);
        shadePanel.add(MyCnBlog_Label);
        MyCnBlog_Label.setBounds(10, 240, 400, 25);
        shadePanel.add(MyCnBlog);
        MyCnBlog.setBounds(10, 265, 400, 30);
        shadePanel.add(Copyright);
        Copyright.setBounds(10, 300, 400, 50);

        setVisible(true);
    }

    public static void main(String[] args) {
        new About();
    }
}

class InternetMonitor extends MouseAdapter{
    public void mouseClicked(MouseEvent e){
        JLabel JLabel_temp = (JLabel)e.getSource();
        String J_temp = JLabel_temp.getText();
        System.out.println(J_temp);
        URI uri ;
        try {
            uri = new URI(J_temp);
            Desktop desk=Desktop.getDesktop();
            if(Desktop.isDesktopSupported() && desk.isSupported(Desktop.Action.BROWSE)){
                try {
                    desk.browse(uri);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
    }
    public void mouseEntered(MouseEvent e){
        JLabel JLabel_temp = (JLabel)e.getSource();
        JLabel_temp.setForeground(Color.red);
    }
    public void mouseExited(MouseEvent e){
        JLabel JLabel_temp = (JLabel)e.getSource();
        JLabel_temp.setForeground(Color.blue);
    }
}
