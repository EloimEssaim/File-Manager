import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.lang.*;
import java.text.SimpleDateFormat;

public class FileManager extends JFrame
{
    public static void main(String[] args)
    {
        fileSystem myFileSystem=new fileSystem();
        userInterface ui=new userInterface(myFileSystem);
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
    private fileSystem system;

    public userInterface(fileSystem system)
    {

        setTitle("File Manager");
        setSize(WIDTH,HEIGHT);
        setLayout(new GridLayout(1,3));

        this.system=system;

        informationPanel=new informationPanel();
        catalogPanel=new catalogPanel(this,system);
        buttonPanel=new buttonPanel(this,system, catalogPanel);

        add(informationPanel);
        add(buttonPanel);
        add(catalogPanel);
    }
}

//信息面板，显示剩余空间和当前路径
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

class openFileWindow extends JFrame
{
    private JMenuItem menuItem;
    private JMenuBar menuBar;
    private JMenu menu;
    private JTextArea textArea;
    private JScrollPane scrollPane;

    public openFileWindow(FCB openFCB)
    {
        menuBar = new JMenuBar();
        menuItem = new JMenuItem("保存");

        menu = new JMenu();
        menu.setText("文件"+openFCB.getFileName());
        menu.add(menuItem);

        menuBar.add(menu);

        textArea = new JTextArea();
        textArea.setLineWrap(true);// 激活自动换行功能
        textArea.setWrapStyleWord(true);// 激活断行不断字功能
        textArea.setBackground(Color.WHITE);

        scrollPane= new JScrollPane(textArea);

        add(scrollPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setJMenuBar(menuBar);

        this.setSize(400, 400);
        this.setLocation(500, 300);
        this.setVisible(true);


    }


}


class buttonPanel extends JPanel
{
    private fileSystem system;
    private userInterface ui;
    private catalogPanel catalogPane;

    private JButton createFolderButton;//创建文件夹
    private JButton createFileButton;//创建文件
    private JButton openFileButton;//打开文件
    private JButton closeFileButton;//关闭文件
    private JButton formatButton;//格式化
    private JButton deleteButton;//删除文件夹或文件
    private JButton renameButton;//重命名文件或文件夹
    private JButton propertyButton;//查看文件或文件夹属性

    public buttonPanel(userInterface ui,fileSystem system,catalogPanel catalogPane)
    {
        this.ui=ui;
        this.system=system;
        this.catalogPane=catalogPane;


        setLayout(new GridLayout(8,1));
        createFolderButton =new JButton("Create Folder");
        createFileButton=new JButton("Create File");
        openFileButton=new JButton("Open File");
        closeFileButton=new JButton("Close File");
        formatButton=new JButton("Formatting");
        deleteButton=new JButton("Delete");
        renameButton=new JButton("Rename");
        propertyButton=new JButton("Property");

        //添加按钮响应函数
        createFolderButton.addActionListener(createFolderListener);
        createFileButton.addActionListener(createFileListener);
        openFileButton.addActionListener(openFileListener);
        closeFileButton.addActionListener(closeFileListener);
        formatButton.addActionListener(formatListener);
        deleteButton.addActionListener(deleteListener);
        renameButton.addActionListener(renameListener);
        propertyButton.addActionListener(propertyListener);

        add(createFileButton);
        add(createFolderButton);
        add(openFileButton);
        add(closeFileButton);
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
                JOptionPane.showMessageDialog(null,"请选择要创建文件的父文件夹~！");//如果不选中树形目录则无法进行操作
            else
            {
                JOptionPane optionPane = new JOptionPane("请输入文件名", JOptionPane.QUESTION_MESSAGE, JOptionPane.CANCEL_OPTION);
                JDialog dialog = optionPane.createDialog("文件名");
                optionPane.setWantsInput(true);
                dialog.setVisible(true);
                String fileName=(String)optionPane.getInputValue();//获取输入的文件名

                FCB createdFile=system.createFile(fileName,catalogPane.selectedFCB);//创建文件

                if(createdFile==null)
                     JOptionPane.showMessageDialog(null,"同一文件夹下，文件和文件不能重名~！");
                else
                    catalogPane.addNewNode(createdFile);
            }

        }
    };

    //创建文件夹按钮响应函数
    ActionListener createFolderListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if(catalogPane.selectedNode==null)//如果没有选择目录节点，无法删除
                JOptionPane.showMessageDialog(null,"请选择要创建子文件夹的文件夹~！");

            else
            {
                System.out.println(catalogPane.selectedFCB.getFcbType());
                if(catalogPane.selectedFCB.getFcbType()==FCB_TYPE.FOLDER)//如果是选中folder类型的，则可以创建文件夹
                {
                    JOptionPane optionPane=new JOptionPane("请输入文件夹名", JOptionPane.QUESTION_MESSAGE, JOptionPane.CANCEL_OPTION);
                    JDialog dialog = optionPane.createDialog("文件夹名");
                    optionPane.setWantsInput(true);
                    dialog.setVisible(true);
                    String folderName=(String)optionPane.getInputValue();//获取输入的文件夹名

                    FCB createdFolder=system.createFolder(folderName,catalogPane.selectedFCB);

                    if(createdFolder==null)
                         JOptionPane.showMessageDialog(null,"同一文件夹下文件夹和文件夹不能重名~！");
                    else
                        catalogPane.addNewNode(createdFolder);
                }

                else if(catalogPane.selectedFCB.getFcbType()==FCB_TYPE.FILE)
                {
                    JOptionPane.showMessageDialog(null,"不能在文件下创建文件夹~！");
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
                JOptionPane.showMessageDialog(null,"请选择要打开的文件或文件夹~！");
            else
            {
                openFileWindow fileWindow=new openFileWindow(catalogPane.selectedFCB);
            }
        }
    };



    //关闭文件按钮响应函数
    ActionListener closeFileListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if(catalogPane.selectedNode==null)//如果没有选择目录节点，无法删除
                JOptionPane.showMessageDialog(null,"请选择要关闭的文件或文件夹~！");
            else
            {

            }
        }
    };


    //格式化按钮响应函数
    ActionListener formatListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if(catalogPane.selectedNode==null)//如果没有选择目录节点，无法格式化
                JOptionPane.showMessageDialog(null,"请选择要格式化的文件或文件夹~！");
            else
            {
                int n=JOptionPane.showConfirmDialog(null,"确定格式化?","格式化",JOptionPane.YES_NO_OPTION);
                if(n==0)
                    system.format(catalogPane.selectedFCB);
            }
        }
    };

    //删除按钮响应函数
    ActionListener deleteListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if(catalogPane.selectedNode==null)//如果没有选择目录节点，无法删除
                JOptionPane.showMessageDialog(null,"请选择要删除的文件或文件夹~！");
            else
            {
                if(catalogPane.selectedNode.isRoot())//选取了节点，但是选取的是根目录
                {
                    JOptionPane.showMessageDialog(null,"不能删除根目录~！");
                }

                else
                {
                    if(catalogPane.selectedFCB.getFcbType()==FCB_TYPE.FILE)
                    {
                        catalogPane.deleteNode(catalogPane.selectedNode);
                        system.deleteFile(catalogPane.selectedFCB);
                    }

                    else if(catalogPane.selectedFCB.getFcbType()==FCB_TYPE.FOLDER)
                    {
                        catalogPane.deleteNode(catalogPane.selectedNode);
                        system.deleteFolder(catalogPane.selectedFCB);//删除文件夹时要删除文件夹下所有的子文件和文件夹
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
                JOptionPane.showMessageDialog(null,"请选择要重命名的文件或文件夹~！");
            else
            {

                if(catalogPane.selectedNode.isRoot())
                {
                    JOptionPane.showMessageDialog(null,"不能重命名根目录~！");
                }

                else
                {
                    JOptionPane optionPane=new JOptionPane("请输入新的文件名", JOptionPane.QUESTION_MESSAGE, JOptionPane.CANCEL_OPTION);
                    JDialog dialog = optionPane.createDialog("重命名");
                    optionPane.setWantsInput(true);
                    dialog.setVisible(true);
                    String newFileName=(String)optionPane.getInputValue();//获取输入的文件名

                    system.rename(newFileName,catalogPane.selectedFCB);
                    catalogPane.setNodeName(newFileName);
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
                JOptionPane.showMessageDialog(null,"请选择要查看属性的文件或文件夹~！");
            else
            {
                String fcbProperty=catalogPane.selectedFCB.getProperty();
                JOptionPane.showMessageDialog(null,fcbProperty);
            }
        }
    };


}

//目录面板
class catalogPanel extends JPanel
{
    private userInterface ui;
    private fileSystem system;
    private JScrollPane catalogPane;
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");//根目录作为根结点
    private DefaultTreeModel model=new DefaultTreeModel(root);
    private JTree catalogTree=new JTree(model);

    private Map<DefaultMutableTreeNode,FCB> nodeFCBMap;//建立树结点到FCB的映射

    public FCB selectedFCB;
    public DefaultMutableTreeNode selectedNode;

    public catalogPanel(userInterface ui,fileSystem system)
    {

        this.ui=ui;
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

    public void addNewNode(FCB newFileFCB)
    {
        DefaultMutableTreeNode newTreeNode=new DefaultMutableTreeNode(newFileFCB.getFileName());//创建新结点

        selectedNode.add(newTreeNode);
        nodeFCBMap.put(newTreeNode,newFileFCB);//加入map
        model.reload();
    }

    public void deleteNode(DefaultMutableTreeNode node)
    {
        model.removeNodeFromParent(node);
        FCB fcb=nodeFCBMap.get(node);
        nodeFCBMap.remove(fcb);//从map中删除
    }

    public void setNodeName(String newName)
    {
        selectedNode.setUserObject(newName);
    }



}



class fileSystem
{
    private LinkedList<FCB> fileCatalog=null;//目录
    private FCB fileRoot;//根目录
    private FAT fatList;//fat表

    public fileSystem()
    {
        this.fileCatalog=new LinkedList<FCB>();
        this.fileRoot=new FCB("Root",FCB_TYPE.FOLDER,0,0);
        this.fatList=new FAT();
        fileCatalog.add(fileRoot);//加入目录

    }

    public FCB getFileRoot()
    {
        return fileRoot;
    }//获取根目录FCB

    public FAT getFatList(){return fatList;}//获取FAT

    public FCB createFile(String name,FCB folderFCB)//创建文件
    {
        //检查是否重名
        if(checkDuplicateName(name,FCB_TYPE.FILE,folderFCB)==true)
        {
            return null;
        }

        //若不重名，则创建文件
        else
        {
            System.out.println("不重名");

            if(fatList.getTop()!=0)
            {
                int blockId=fatList.getFreeBlock();//获取空闲块
                System.out.println("获取空闲块"+blockId);

                FCB fileFCB=new FCB(name,FCB_TYPE.FILE,blockId,0);//创建文件
                fatList.setEndBlock(blockId);//设块内容为-1（因为文件目前还为空）


                FCB folder=getFCB(name,folderFCB);//遍历目录链表，找到文件的父文件夹
                fileFCB.setFolderFCB(folder);

                System.out.println("文件夹"+folderFCB.getFileName()+"下的名为"+name+"的文件被创建了");

                folderFCB.addChildren(fileFCB);//父文件夹的子链表插入儿子

                fileCatalog.add(fileFCB);//加入文件目录
                return fileFCB;
            }

            else
            {
                System.out.println(Constants.SPACE_FULL_MESSAGE);
                return null;//空间满，无法创建
            }

        }
    }

    public FCB createFolder(String name,FCB parentFolderFCB)
    {
        //检查同一路径下是否重名,若重名则无法创建
        if(checkDuplicateName(name,FCB_TYPE.FOLDER,parentFolderFCB)==true)
        {
            return null;
        }

        //若不重名，则创建文件夹
        else
        {
            FCB folderFCB=new FCB(name,parentFolderFCB,FCB_TYPE.FOLDER);//创建文件夹FCB
            FCB parent=getFCB(name,parentFolderFCB);//遍历目录链表，找到文件夹的父文件夹
            folderFCB.setFolderFCB(parent); //设置文件夹的父文件夹

            System.out.println("文件夹"+parentFolderFCB.getFileName()+"下的名为"+name+"的文件夹被创建了");
            parent.addChildren(folderFCB);//父文件夹的子链表中插入FCB

            fileCatalog.add(folderFCB); //加入目录链表
            return folderFCB;
        }

    }



    //删除文件
    public void deleteFile(FCB fcbToDelete)
    {
        fatList.deleteFromFAT(fcbToDelete.getFileStartBlock());//fat表中释放文件所占全部块

        for(Iterator iter=fileCatalog.iterator();iter.hasNext();)
        {
            FCB indexFCB=(FCB)iter.next();
            if(indexFCB==fcbToDelete)
            {
                fileCatalog.remove(fcbToDelete);
                System.out.println(fcbToDelete.getFileName()+"已被删除");
                break;
            }
        }
    }


    //删除空文件夹
    public void deleteEmptyFolder(FCB fcbToDelete)
    {
        for(Iterator iter=fileCatalog.iterator();iter.hasNext();)
        {
            FCB indexFCB=(FCB)iter.next();
            if(indexFCB==fcbToDelete)
            {
                fileCatalog.remove(fcbToDelete);
                System.out.println("文件夹"+fcbToDelete.getFileName()+"已被删除");
                break;
            }
        }
    }


    //删除文件夹
    public void deleteFolder(FCB fcbToDelete)
    {

        for(Iterator iter=fileCatalog.iterator();iter.hasNext();)
        {
            FCB indexFCB=(FCB)iter.next();

            if(indexFCB==fcbToDelete)
            {
                for(Iterator contentIter=indexFCB.childrenList.iterator();contentIter.hasNext();)
                {
                    FCB content=(FCB)contentIter.next();
                    if(content.getFcbType()==FCB_TYPE.FILE)
                    {
                        deleteFile(content);
                        System.out.println("文件夹"+content.getFolderFCB().getFileName()+"下的"+content.getFileName()+"被删除");
                    }

                    else if(content.getFcbType()==FCB_TYPE.FOLDER)
                    {
                        if(content.childrenList.size()!=0)
                            deleteFolder(content);//递归删除文件夹中全部内容
                        else//如果为最后一个文件夹且为空
                        {
                            deleteEmptyFolder(content);
                        }
                    }

                }
                deleteEmptyFolder(indexFCB);
                break;
            }

        }

    }

    //格式化
    public void format(FCB fcb)
    {
        if(fcb.getFcbType()==FCB_TYPE.FILE)//格式化文件
        {
            fcb.setFileSize(0);
            fcb.setModifyTime();
        }

        else if(fcb.getFcbType()==FCB_TYPE.FOLDER)//格式化文件夹
        {
            for(Iterator iter=fcb.childrenList.iterator();iter.hasNext();)
            {
                FCB indexFCB=(FCB)iter.next();
                format(indexFCB);
            }

            fcb.setModifyTime();
        }
    }

    //重命名文件、文件夹
    public void rename(String newName,FCB fcbToRename)
    {
        for(Iterator iter=fileCatalog.iterator();iter.hasNext();)
        {
            FCB indexFCB=(FCB)iter.next();
            if(indexFCB==fcbToRename)
            {
                indexFCB.setFileName(newName);
                break;
            }
        }

    }

    //检查是否重名
    public boolean checkDuplicateName(String name,FCB_TYPE type,FCB folderFCB)  //查找父文件夹的孩子，如果类型相同且名称相同，则重名
    {
        boolean isDuplicate=false;

        for(Iterator iter=folderFCB.childrenList.iterator();iter.hasNext();)
        {
            FCB indexFCB=(FCB)iter.next();
            String indexName=indexFCB.getFileName();
            FCB_TYPE indexType=indexFCB.getFcbType();

            if(name.equals(indexName)&&type.equals(indexType))//如果同文件夹下重名
            {
                System.out.println(name+Constants.NAME_DUPLICATE_MESSAGE);
                isDuplicate=true;
                break;
            }
        }

        return isDuplicate;
    }

    //根据文件名和父文件夹的fcb找到文件的fcb
    public FCB getFCB(String name,FCB folderFCB)
    {
        FCB indexFCB=new FCB();
         for(Iterator iter=fileCatalog.iterator();iter.hasNext();)
         {
             indexFCB=(FCB)iter.next();
             if(indexFCB==folderFCB)
             {
                 break;
             }
         }
         return indexFCB;
    }


}



class FAT implements Serializable//文件FAT表
{
    private int[] fatBlockList;
    private int[] freeBlockStack;//空闲块栈

    private int top=0;//栈顶指针

    public FAT()
    {
        fatBlockList=new int[Constants.BLOCK_NUM];
        freeBlockStack=new int[Constants.BLOCK_NUM];
        for(int i=Constants.BLOCK_NUM-1;i>=2;i--)
        {
            freeBlock(i);
        }//初始化，2-255块设为空闲块

        fatBlockList[0]=1;
        fatBlockList[1]=Constants.BLOCK_END;

        for(int j=0;j<Constants.BLOCK_NUM-1;j++)
        {
            System.out.println("磁盘块"+j+"中下一块为"+fatBlockList[j]);
        }
    }

    //释放序号为currentBlockNum的物理块，压入空闲块栈
    public void freeBlock(int currentBlockNum)
    {
        freeBlockStack[top]=currentBlockNum;//要压栈的空闲块放于栈顶
        top++;//栈顶指针移位
        fatBlockList[currentBlockNum]=Constants.BLOCK_FREE;//FAT表中该项为空

    }

    //获取空闲的物理块
    public int getFreeBlock()
    {
        top--;
        return freeBlockStack[top];
    }

    //获取下一块序号
    public int getNextBlock(int currentBlockNum)
    {
        return fatBlockList[currentBlockNum];
    }

    //设置块与块间链接
    public void setNextBlock(byte currentBlockNum,byte nextBlockNum)
    {
        fatBlockList[currentBlockNum]=nextBlockNum;
    }

    public int getTop()
    {
        return top;
    }

    public int getEndBlock(int currentBlockNum)
    {
        int index=currentBlockNum;
        while(!isEndBlock(index))
        {
            index=getNextBlock(index);
        }
        return index;//最后一块序号
    }


    public void setEndBlock(int currentBlockNum)
    {
        fatBlockList[currentBlockNum]=Constants.BLOCK_END;
    }//设置物理块末尾

    public boolean isEndBlock(int currentBlockNum)
    {
        if(fatBlockList[currentBlockNum]==Constants.BLOCK_END)
            return true;
        else
            return false;
    }

    //删除某个文件所占全部块
    public void deleteFromFAT(int startBlockNum)
    {
        int index=startBlockNum;
        while(!isEndBlock(index))
        {
            freeBlock(index);
            index=getNextBlock(index);
        }
    }


}

class FCB implements Serializable
{
    private String fileName;//文件名
    private String folderName;//文件夹名

    private FCB_TYPE fcbType;//是文件还是文件夹

    private FCB folderFCB;//文件夹的FCB
    public LinkedList<FCB> childrenList;//如果是文件夹，则存放文件夹内的文件或文件夹

    private boolean isOpened;//文件是否被打开

    private int fileSize;//文件大小
    private int fileStartBlock;//开始盘块

    private String createTime;//创建时间
    private String modifyTime;//修改时间

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public FCB()
    {
        this.fileSize=0;
        this.childrenList=new LinkedList<FCB>();

        Date date = new Date();
        long times = date.getTime();
        this.createTime = formatter.format(date);
        this.modifyTime=formatter.format(date);

        this.isOpened=false;
    }

    //构造根目录fcb
    public FCB(String name,FCB_TYPE type,int startBlock,int size)
    {

        this.fileSize=size;
        this.fileName=name;
        this.fcbType=type;

        this.childrenList=new LinkedList<FCB>();

        this.isOpened=false;
        this.fileStartBlock=startBlock;
        this.fileSize=size;

        Date date = new Date();
        long times = date.getTime();

        this.createTime = formatter.format(date);
        this.modifyTime=formatter.format(date);

    }

    public FCB(String name,FCB_TYPE type,FCB folder,int startBlock,int size)
    {
        this.fileName=name;
        this.fileSize=size;//大小初始化为0
        this.fcbType=type;
        this.folderFCB=folder;

        this.childrenList=new LinkedList<FCB>();

        this.isOpened=false;
        this.fileStartBlock=startBlock;
        this.fileSize=size;

        Date date = new Date();
        long times = date.getTime();
        this.createTime = formatter.format(date);
        this.modifyTime=formatter.format(date);

    }

    //构造文件夹FCB
    public FCB(String name,FCB folderFCB,FCB_TYPE type)
    {
        this.childrenList=new LinkedList<FCB>();
        this.fileName=name;
        this.folderFCB=folderFCB;
        this.fcbType=type;

        Date date = new Date();
        long times = date.getTime();
        this.createTime = formatter.format(date);
        this.modifyTime=formatter.format(date);


    }

    //获取文件信息
    public String getProperty()
    {
        String fcbProperty="";
        fcbProperty+="名称："+this.fileName;
        fcbProperty+="\n";
        fcbProperty+="类型："+this.fcbType;
        fcbProperty+="\n";

        if(this.folderFCB!=null)
        {
            fcbProperty+="处于文件夹："+this.folderFCB.getFileName();
            fcbProperty+="\n";
        }
        fcbProperty+="大小："+this.fileSize;
        fcbProperty+="\n";
        fcbProperty+="创建时间："+this.createTime;
        fcbProperty+="\n";
        fcbProperty+="修改时间："+this.modifyTime;
        fcbProperty+="\n";

        return fcbProperty;
    }

    //获取所需块数
    public int getBlockNeeded()
    {
        return  (int)(fileSize/Constants.BYTE_PER_BLOCK);

    }

    public FCB getFolderFCB(){return folderFCB;}

    public void setFolderFCB(FCB folderFCB){this.folderFCB=folderFCB;}

    public FCB_TYPE getFcbType(){return fcbType;}

    public String getFileName(){return fileName;}

    public void setFileName(String fileName){this.fileName=fileName;}

    public int getFileSize(){return fileSize;}

    public void setFileSize(int size){this.fileSize=size;}

    public int getFileStartBlock(){return fileStartBlock;}

    public void addChildren(FCB fcb)
    {
        this.childrenList.add(fcb);
    }

    public void setModifyTime()
    {
        Date date = new Date();
        long times = date.getTime();
        this.modifyTime=formatter.format(date);
    }



}

































