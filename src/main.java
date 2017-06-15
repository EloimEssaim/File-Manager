import java.lang.*;
import java.awt.*;
import javax.swing.*;

public class main extends JFrame
{
    public static void main(String[] args)
    {
        userInterface ui=new userInterface();
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.show();
    }
}

class userInterface extends JFrame
{
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private infoPanel infoPanel;

    public userInterface()
    {
        setTitle("File Manager");
        setSize(WIDTH,HEIGHT);
        infoPanel=new infoPanel();
        add(infoPanel);

    }
}

class infoPanel extends JPanel
{
    private JTextArea currentDirectory;

    public infoPanel()
    {
        currentDirectory=new JTextArea("当前目录");
        add(currentDirectory);
    }
}

class folderPanel extends JPanel
{
    private JButton []folderButton;

    public folderPanel()
    {

    }
}

class filePanel extends JPanel
{
    private JButton []fileButton;
    public filePanel()
    {
        fileButton=new JButton[4];
    }
}


class file
{
    public file()
    {

    }

    //创建文件，建立FCB
    public void createFile()
    {

    }

    //打开文件
    public void openFile()
    {

    }

    //写文件
    public void writeFile()
    {

    }

    //读文件
    public void readFile()
    {

    }

    //删除文件
    public void deleteFile()
    {

    }



}

//文件控制块
class FCB
{
    private String name;
    private String type;

    public FCB()
    {

    }
}

//文件目录
class fileCatalog
{

    public fileCatalog()
    {

    }
}

//系统打开文件表
class systemFileTable
{
    public systemFileTable()
    {

    }
}






