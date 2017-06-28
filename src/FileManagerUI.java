import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


public class FileManagerUI extends JFrame implements Serializable
{
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 600;

    private fileSystem system;
    private fatPanel fatPanel;
    private buttonPanel buttonPanel;
    private catalogPanel catalogPanel;


    public FileManagerUI()
    {

        setTitle("File Manager");
        setSize(WIDTH,HEIGHT);
        setLayout(new GridLayout(1,3));

        this.system=new fileSystem();
        this.fatPanel=new fatPanel();
        this.catalogPanel=new catalogPanel(system);
        this.buttonPanel=new buttonPanel(system, catalogPanel,fatPanel);

        add(fatPanel);
        add(buttonPanel);
        add(catalogPanel);


    }


}


//信息面板，显示剩余空间和当前路径
class fatPanel extends JPanel implements Serializable
{
    private JScrollPane pane;
    private String[] columnName={"Block","Value"};
    private DefaultTableModel model;
    private JTable table;
    private Object[][] data;
    public fatPanel()
    {
        this.pane=new JScrollPane();
        this.pane.setPreferredSize(new Dimension(330,600));
        this.model=new DefaultTableModel()
        {
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };

        this.data= new Object[Constants.BLOCK_NUM][2];

        this.table=new JTable(model);
        this.table.setToolTipText("FAT");

        pane.setViewportView(table);
        this.add(pane);
    }

    public void update(FAT fat)
    {

        for(int i=0;i<Constants.BLOCK_NUM;i++)
        {
            data[i][0] = i;
            data[i][1] = fat.getFatBlockList()[i];
        }
        model.setDataVector(data,columnName);
    }//每次文件空间变化，FAT都会变化

}

class buttonPanel extends JPanel implements Serializable
{
    private fileSystem system;
    private catalogPanel catalogPane;
    private fatPanel fatPane;
    private JButton createFolderButton;//创建文件夹
    private JButton createFileButton;//创建文件
    private JButton openFileButton;//打开文件
    private JButton formatButton;//格式化
    private JButton deleteButton;//删除文件夹或文件
    private JButton renameButton;//重命名文件或文件夹
    private JButton propertyButton;//查看文件或文件夹属性

    public buttonPanel(fileSystem system,catalogPanel catalogPane,fatPanel fatPane)
    {
        this.system=system;
        this.catalogPane=catalogPane;
        this.fatPane=fatPane;

        setLayout(new GridLayout(7,1));
        createFolderButton =new JButton("Create Folder");
        createFileButton=new JButton("Create File");
        openFileButton=new JButton("Open File");
        formatButton=new JButton("Formatting");
        deleteButton=new JButton("Delete");
        renameButton=new JButton("Rename");
        propertyButton=new JButton("Property");

        //添加按钮响应函数
        createFolderButton.addActionListener(createFolderListener);
        createFileButton.addActionListener(createFileListener);
        openFileButton.addActionListener(openFileListener);
        formatButton.addActionListener(formatListener);
        deleteButton.addActionListener(deleteListener);
        renameButton.addActionListener(renameListener);
        propertyButton.addActionListener(propertyListener);


        add(createFileButton);
        add(createFolderButton);
        add(openFileButton);
        add(formatButton);
        add(deleteButton);
        add(renameButton);
        add(propertyButton);
    }

    //创建文件按钮响应函数
    ActionListener createFileListener=new ActionListener()
    {

        public void actionPerformed(ActionEvent e)
        {
            if(catalogPane.selectedNode==null)
                JOptionPane.showMessageDialog(null,"Please select a folder!");//如果不选中树形目录则无法进行操作

            else
            {
                JOptionPane optionPane = new JOptionPane("Please enter the file name", JOptionPane.QUESTION_MESSAGE, JOptionPane.CANCEL_OPTION);
                JDialog dialog = optionPane.createDialog("Create File");

                optionPane.setWantsInput(true);
                dialog.setVisible(true);
                String fileName=(String)optionPane.getInputValue();//获取输入的文件名

                FCB createdFile=system.createFile(fileName,catalogPane.selectedFCB);//创建文件

                if(createdFile!=null)
                    catalogPane.addNewNode(createdFile);
                fatPane.update(system.getFatList());

            }

        }
    };


    //创建文件夹按钮响应函数
    ActionListener createFolderListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if(catalogPane.selectedNode==null)//如果没有选择目录节点，无法删除
                JOptionPane.showMessageDialog(null,"Please select a file or folder!");

            else
            {
                if(catalogPane.selectedFCB.getFcbType()==FCB_TYPE.FOLDER)//如果是选中folder类型的，则可以创建文件夹
                {
                    JOptionPane optionPane=new JOptionPane("Please enter the folder name", JOptionPane.QUESTION_MESSAGE, JOptionPane.CANCEL_OPTION);
                    JDialog dialog = optionPane.createDialog("Create Folder");
                    optionPane.setWantsInput(true);
                    dialog.setVisible(true);
                    String folderName=(String)optionPane.getInputValue();//获取输入的文件夹名

                    FCB createdFolder=system.createFolder(folderName,catalogPane.selectedFCB);

                    if(createdFolder==null)
                        JOptionPane.showMessageDialog(null,"Duplicate name!");
                    else
                        catalogPane.addNewNode(createdFolder);
                }

                else if(catalogPane.selectedFCB.getFcbType()==FCB_TYPE.FILE)
                {
                    JOptionPane.showMessageDialog(null,"A folder cannot be created under a file!");//不能在文件下创建文件夹
                }

            }

        }
    };



    //打开文件按钮响应函数
    ActionListener openFileListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if(catalogPane.selectedNode==null)//如果没有选择目录节点，无法删除
                JOptionPane.showMessageDialog(null,"Please select a file");
            else
            {
                if(catalogPane.selectedFCB.getFcbType()==FCB_TYPE.FOLDER)
                {
                    JOptionPane.showMessageDialog(null,"Please select a file!");
                }
                else
                {
                    openFileWindow fileWindow=new openFileWindow(catalogPane.selectedFCB,system.getFatList(),fatPane);
                }

                fatPane.update(system.getFatList());

            }
        }
    };


    //格式化按钮响应函数
    ActionListener formatListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if(catalogPane.selectedNode==null)//如果没有选择目录节点，无法格式化
                JOptionPane.showMessageDialog(null,"Please select a file or folder!");
            else
            {
                int n=JOptionPane.showConfirmDialog(null,"Do you want to format"+catalogPane.selectedFCB.getFileName()+"?","Formatting",JOptionPane.YES_NO_OPTION);
                if(n==0)//若选择了要格式化
                {
                    system.format(catalogPane.selectedFCB);
                    catalogPane.formatNode(catalogPane.selectedFCB);
                    fatPane.update(system.getFatList());
                }

            }
        }
    };

    //删除按钮响应函数
    ActionListener deleteListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if(catalogPane.selectedNode==null)//如果没有选择目录节点，无法删除
                JOptionPane.showMessageDialog(null,"Please select a file or folder!");
            else
            {
                if(catalogPane.selectedNode.isRoot())//选取了节点，但是选取的是根目录
                {
                    JOptionPane.showMessageDialog(null,"Root folder cannot be deleted!");
                }

                else
                {
                    if(catalogPane.selectedFCB.getFcbType()==FCB_TYPE.FILE)
                    {
                        catalogPane.deleteNode(catalogPane.selectedNode);
                        system.deleteFile(catalogPane.selectedFCB);
                        fatPane.update(system.getFatList());
                    }

                    else if(catalogPane.selectedFCB.getFcbType()==FCB_TYPE.FOLDER)
                    {
                        catalogPane.deleteNode(catalogPane.selectedNode);
                        system.deleteFolder(catalogPane.selectedFCB);//删除文件夹时要删除文件夹下所有的子文件和文件夹
                        fatPane.update(system.getFatList());
                    }
                }



            }
        }
    };

    //重命名按钮响应函数
    ActionListener renameListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if(catalogPane.selectedNode==null)//如果没有选择目录节点，无法查看属性
                JOptionPane.showMessageDialog(null,"Please select a file or folder!");
            else
            {

                if(catalogPane.selectedNode.isRoot())
                {
                    JOptionPane.showMessageDialog(null,"Root folder cannot be renamed!");
                }

                else
                {
                    JOptionPane optionPane=new JOptionPane("Please enter the new name", JOptionPane.QUESTION_MESSAGE, JOptionPane.CANCEL_OPTION);
                    JDialog dialog = optionPane.createDialog("Rename");
                    optionPane.setWantsInput(true);
                    dialog.setVisible(true);

                    String newFileName=(String)optionPane.getInputValue();//获取输入的文件名

                    FCB newFCB=system.rename(newFileName,catalogPane.selectedFCB);
                    catalogPane.setNodeName(newFCB);
                }

            }
        }
    };

    //查看属性按钮响应函数
    ActionListener propertyListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if(catalogPane.selectedNode==null)//如果没有选择目录节点，无法查看属性
                JOptionPane.showMessageDialog(null,"Please select a file or folder!");
            else
            {
                String fcbProperty=catalogPane.selectedFCB.getProperty();
                JOptionPane.showMessageDialog(null,fcbProperty);
            }
        }
    };



}

//目录面板
class catalogPanel extends JPanel implements Serializable
{
    private fileSystem system;
    private JScrollPane catalogPane;
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");//根目录作为根结点
    private DefaultTreeModel model=new DefaultTreeModel(root);
    private JTree catalogTree=new JTree(model);

    private Map<DefaultMutableTreeNode,FCB> nodeFCBMap;//建立树结点到FCB的映射

    public FCB selectedFCB;
    public DefaultMutableTreeNode selectedNode;

    public catalogPanel(fileSystem system)
    {
        this.system=system;
        this.nodeFCBMap=new HashMap<DefaultMutableTreeNode,FCB>();

        catalogPane=new JScrollPane();
        catalogPane.setPreferredSize(new Dimension(340,600));
        catalogPane.setViewportView(catalogTree);
        catalogTree.setToolTipText("File Catalog");
        catalogTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);


        nodeFCBMap.put(root,system.getFileRoot());//将根目录节点和根目录fcb的映射放入map

        catalogTree.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent e)
            {
                if(catalogTree.getLastSelectedPathComponent()!=null)
                {
                    selectedNode=(DefaultMutableTreeNode)catalogTree.getLastSelectedPathComponent();
                    selectedFCB=nodeFCBMap.get(selectedNode);//获取选中树节点映射的FCB
                }


            }
        });//文件树监听
        add(catalogPane);

    }

    public void addNewNode(FCB newFCB)
    {
        DefaultMutableTreeNode newTreeNode=new DefaultMutableTreeNode(newFCB.getFileName());//创建新结点

        selectedNode.add(newTreeNode);
        nodeFCBMap.put(newTreeNode,newFCB);//加入map
        model.reload();
    }

    public void deleteNode(DefaultMutableTreeNode node)
    {
        model.removeNodeFromParent(node);
        FCB fcb=nodeFCBMap.get(node);
        nodeFCBMap.remove(fcb);//从map中删除
    }

    public void setNodeName(FCB newFCB)
    {
        nodeFCBMap.remove(selectedNode);
        selectedNode.setUserObject(newFCB.getFileName());
        nodeFCBMap.put(selectedNode,newFCB);
    }

    public void formatNode(FCB newFCB)
    {
        nodeFCBMap.remove(selectedNode);
        nodeFCBMap.put(selectedNode,newFCB);
    }



}

class openFileWindow extends JFrame implements Serializable
{
    private JMenuItem menuItem;
    private JMenuBar menuBar;
    private JMenu menu;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private FAT fat;
    private fatPanel fatPanel;

    public openFileWindow(FCB openFCB,FAT fat,fatPanel fatPanel)
    {

        this.menuBar = new JMenuBar();
        this.menuItem = new JMenuItem("save");
        this.menu = new JMenu();
        this.menu.setText("File:"+openFCB.getFileName());
        this.menu.add(menuItem);
        this.menuBar.add(menu);
        this.textArea = new JTextArea(openFCB.getInnerText());
        this.textArea.setLineWrap(true);// 激活自动换行功能
        this.textArea.setWrapStyleWord(true);// 激活断行不断字功能
        this.textArea.setBackground(Color.WHITE);
        this.scrollPane= new JScrollPane(textArea);
        this.add(scrollPane);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setJMenuBar(menuBar);
        this.setSize(400, 400);
        this.setLocation(500, 300);
        this.setVisible(true);
        this.fatPanel=fatPanel;
        this.fat=fat;


        menuItem.addActionListener((ActionEvent e) ->
        {

            String text=textArea.getText();
            byte[] textByte=text.getBytes();

            openFCB.save(text);
            fat.deleteFromFAT(openFCB.getFileStartBlock());
            fat.saveToFAT(openFCB);
            fatPanel.update(fat);

        });//保存按钮响应函数
    }

}





