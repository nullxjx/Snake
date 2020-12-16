# Snake  

这是XJX用Java语言实现的一个贪吃蛇游戏。

## 1st version
- 游戏总的代码行数差不多是700行。  
- 游戏中贪吃蛇的头部是一个红色方块，贪吃蛇的身体结点是渐变色的方块。食物是绿色的圆形。  
- 您可以通过键盘上的方向键或者WASD键来控制蛇的移动。  
- 在游戏界面按ESC键可以直接重新开始游戏，按空格键可以实现暂停和开始。  
- 菜单栏的设置菜单可以设置网格以及边框是否可见。游戏界面右边会显示你的当前长度和当前所花时间。  
- 吃到食物和死亡时都会有相应的音效。  
- 右边会显示你的当前长度和所花时间。  
- 游戏界面的宽度（横向的格子数）和高度（纵向的格子数）分别可以通过 Scene 类中的 width 和 height变量来设置，默认两者都是20。  

截图如下：  
![example-image](https://github.com/njuxjx/Snake/blob/master/1st_version/screenshots/Snipaste_2020-12-15_14-52-24.png)
![example-image](https://github.com/njuxjx/Snake/blob/master/1st_version/screenshots/Snipaste_2020-12-15_14-53-12.png)
![example-image](https://github.com/njuxjx/Snake/blob/master/1st_version/screenshots/Snipaste_2020-12-15_14-54-01.png)  


## 2nd version
- 此版本更换了蛇的外形，包括蛇头以及蛇身。  
- 加入了多种食物，每种食物对应的得分不同，不同分值的食物产生的概率不同，原则是分值越高的食物产生的概率越低。加入了记分功能。  
- 每种食物在产生5秒时间内如果没有被吃掉就会自动移动或者消失。  


## 3rd version
- 本次对程序结构进行了一定的调整，调整后所有源程序在同一个包xjx下。  

- 原来的SnakeDemo.java文件基本保持不变，做了一些修改。  
- 主界面写在了另外一个源文件MainWindow.java中。  
- 程序主界面进行了重大改变  
    - 蛇的活动范围增大了一些
    - 加入了设置菜单，可以设置游戏背景，蛇身体，蛇头部，速度。  
    - 加入了背景图片，为了让背景显得简洁一点，可以选择是否显示网格。  
    - 菜单栏加入了游戏使用说明，以及关于游戏。  


## 4th version
- 此版本相对上一版本加入了障碍物，障碍物随机产生，每隔一段时间自动随机移动，障碍物的长度也随机，排列也随机。  
- 经过本人的相关测试，不排除游戏开始时障碍物出现在你面前导致来不及躲的情况，以及你加速中障碍物改变位置时也可能出现在你面前，所以加速不像上个版本那样，此版本加速有风险。  
- 为了配合障碍物的出现，游戏加入了蛇发射子弹击毁前进道路上的障碍物的技能。目前每次只能发射一个子弹，不能连续发射多枚子弹。  
- 子弹通过吃特定的食物获得，食物样子为一把枪。吃得枪每次增加一颗子弹，不增加得分，增加长度。  
- 子弹产生的概率在所有食物中最低。按Shift键发射子弹。  

## 5th version
- 对代码进行了一次重构。  
- 修复部分bug。 

## 6th version
- 对代码进行了一次彻底重构。
- 修复更换图片后，游戏界面刷新不及时的问题
- 修复打开“设置背景图片”面板时间比较长的问题  
- 移除长按加速  
- 移除食物自动刷新  
- 移除障碍物自动刷新  
- 加入AI🐍  
- 游戏现在有三种模式
    - 仅玩家蛇
    - 仅AI蛇
    - 玩家蛇和AI蛇同时存在
- 游戏地图从map目录下读取，你可以按照目录下的txt文件格式设计自己的地图，目录里面已经包含了三个地图  
    - 注意，每行的每个字符后面都有一个\t字符，不能包含任何空行。  
    - 其中，0表示这个位置是路，3表示是障碍物，不能有其他数字。

map.txt格式如下：  
0	0	0	0	0	0	0	0	0	0	0	  
0	0	0	0	0	0	0	0	0	0	0	  
0	0	0	0	0	0	0	0	0	0	0	  
0	0	0	0	0	0	0	0	0	3	3	  
0	0	0	0	0	0	0	0	0	0	0	  
0	0	0	0	0	0	0	0	0	0	0	  
0	0	0	3	0	0	0	0	0	0	0	  
0	0	0	3	0	0	0	0	0	0	0	  
0	0	0	3	0	0	0	0	0	0	0	  
0	0	0	3	0	0	0	0	0	0	0	  


截图如下：  
![example-image](https://github.com/njuxjx/Snake/blob/master/6th_version/screenshots/Snipaste_2020-12-15_21-00-56.png)
![example-image](https://github.com/njuxjx/Snake/blob/master/6th_version/screenshots/Snipaste_2020-12-15_21-01-48.png)
![example-image](https://github.com/njuxjx/Snake/blob/master/6th_version/screenshots/Snipaste_2020-12-15_21-02-14.png)
![example-image](https://github.com/njuxjx/Snake/blob/master/6th_version/screenshots/Snipaste_2020-12-15_21-03-00.png)
![example-image](https://github.com/njuxjx/Snake/blob/master/6th_version/screenshots/Snipaste_2020-12-16_20-56-08.png)


注意：  
- 由于AI蛇目前使用的是静态寻路算法，所以为了避免玩家蛇故意挡在AI蛇前面造成AI蛇死亡，游戏中允许AI蛇碰到玩家蛇，但是不允许玩家蛇头部碰到AI蛇。  
- 如果玩家蛇吃掉了AI蛇的目标食物，AI蛇会自动寻找下一个距离它最近的食物，然后去吃掉。
- 目前AI蛇只有在2种情况下会死
    - 进入一个凹形区域去吃里面的食物，然后发现出不来了。（玩家蛇可以使用子弹击毁障碍物出来，AI不可以发射子弹）
    - 自己身体把自己的头包围起来了，然后也找不到出去的路了。
    - AI蛇和玩家蛇死了游戏都会自动结束，按Esc键可以重新开始。
 
比如下面这种情况就是AI蛇进入凹形区域后发现出不来了，然后就只有选择goDie了🤣  

![example-image](https://github.com/njuxjx/Snake/blob/master/6th_version/screenshots/Snipaste_2020-12-16_20-29-08.png)
![example-image](https://github.com/njuxjx/Snake/blob/master/6th_version/screenshots/Snipaste_2020-12-16_20-31-19.png)

##
有问题通过本人邮件联系我  

thexjx@gmail.com
