
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

public class FileManagerUI extends JFrame
{
    public userInterface ui;
    public FileManagerUI()
    {
        ui=new userInterface();
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.show();
    }
}

class userInterface extends JFrame
{
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 600;
    private buttonPanel buttonPanel;
    private informationPanel informationPanel;
    private catalogPanel catalogPanel;
    private contentPanel contentPanel;
    private catalogContentPanel catalogContentPanel;
    public userInterface()
    {
        setTitle("File Manager");
        setSize(WIDTH,HEIGHT);
        setLayout(new GridLayout(4,1));

        informationPanel=new informationPanel();
        buttonPanel=new buttonPanel();
        catalogPanel=new catalogPanel();
        contentPanel=new contentPanel();
        catalogContentPanel=new catalogContentPanel(catalogPanel,contentPanel);

        add(informationPanel);
        add(buttonPanel);
        add(catalogContentPanel);

    }
}

class informationPanel extends JPanel
{
    private JLabel memoryLeft;//剩余空间
    private JTextField leftMemoryDisplay;

    private JLabel currentPath;//当前路径
    private JTextField currentPathDisplay;

    public informationPanel()
    {
        setLayout(new GridLayout(2,2));
        memoryLeft=new JLabel("Disk memory left:");
        leftMemoryDisplay=new JTextField();

        currentPath=new JLabel("Current path:");
        currentPathDisplay=new JTextField();

        add(memoryLeft);
        add(leftMemoryDisplay);
        add(currentPath);
        add(currentPathDisplay);

    }
}

class buttonPanel extends JPanel
{
    private JButton createFolderButton;//创建文件夹
    private JButton createFileButton;//创建文件
    private JButton openFileButton;//打开文件
    private JButton closeFileButton;//关闭文件
    private JButton formatButton;//格式化
    private JButton deleteButton;//删除文件夹或文件

    public buttonPanel()
    {
        setLayout(new GridLayout(1,6));
        createFolderButton =new JButton("Create Folder");
        createFileButton=new JButton("Create File");
        openFileButton=new JButton("Open File");
        closeFileButton=new JButton("Close File");
        formatButton=new JButton("Formatting");
        deleteButton=new JButton("Delete");

        //添加按钮响应函数
        createFolderButton.addActionListener(createFolderListener);
        createFileButton.addActionListener(createFileListener);
        openFileButton.addActionListener(openFileListener);
        closeFileButton.addActionListener(closeFileListener);
        formatButton.addActionListener(formatListener);
        deleteButton.addActionListener(deleteListener);

        add(createFolderButton);
        add(createFileButton);
        add(openFileButton);
        add(closeFileButton);
        add(formatButton);
        add(deleteButton);
    }

    //创建文件夹按钮响应函数
    ActionListener createFolderListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {

        }
    };

    //打开文件按钮响应函数
    ActionListener openFileListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {

        }
    };

    //关闭文件按钮响应函数
    ActionListener closeFileListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {

        }
    };


    //格式化按钮响应函数
    ActionListener formatListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {

        }
    };

    //删除按钮响应函数
    ActionListener deleteListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {

        }
    };

    //创建文件按钮响应函数
    ActionListener createFileListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {

        }
    };
}

class catalogContentPanel extends JPanel
{
    private catalogPanel catalogPanel;
    private contentPanel contentPanel;

    public catalogContentPanel(catalogPanel catalog,contentPanel content)
    {
        setLayout(new GridLayout(1,2));
        catalogPanel=catalog;
        contentPanel=content;
        add(catalogPanel);
        add(contentPanel);

    }
}

class catalogPanel extends JPanel//目录面板
{
    private JScrollPane catalogPane;

    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");//根目录作为根结点
    private DefaultTreeModel model=new DefaultTreeModel(root);
    private JTree catalogTree=new JTree(model);

    public catalogPanel()
    {
        catalogPane=new JScrollPane();
        catalogPane.setViewportView(catalogTree);
        catalogTree.setToolTipText("File Catalog");
        catalogTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        catalogTree.addTreeSelectionListener(new TreeSelectionListener()
        {
            @Override
            public void valueChanged(TreeSelectionEvent e)
            {
                DefaultMutableTreeNode node=(DefaultMutableTreeNode)catalogTree.getLastSelectedPathComponent();
                if(node==null)
                    return;

            }
        });

        add(catalogPane);

    }




}


class contentPanel extends JPanel//文件内容面板
{

    private JScrollPane content;

    public contentPanel()
    {
        content=new JScrollPane();
        add(content);
    }
}









