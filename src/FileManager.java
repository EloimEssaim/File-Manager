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
        setLayout(new GridLayout(3,1));

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
    public openFileWindow()
    {
        JMenuItem jMenuItem = new JMenuItem("保存");
        JMenuBar jMenuBar = new JMenuBar();
        JMenu jMenu = new JMenu();
        JTextArea jTextArea = new JTextArea();
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
    private JButton renameButton;

    public buttonPanel(userInterface ui,fileSystem system,catalogPanel catalogPane)
    {
        this.ui=ui;
        this.system=system;
        this.catalogPane=catalogPane;

        setLayout(new GridLayout(1,7));
        createFolderButton =new JButton("Create Folder");
        createFileButton=new JButton("Create File");
        openFileButton=new JButton("Open File");
        closeFileButton=new JButton("Close File");
        formatButton=new JButton("Formatting");
        deleteButton=new JButton("Delete");
        renameButton=new JButton("Rename");

        //添加按钮响应函数
        createFolderButton.addActionListener(createFolderListener);
        createFileButton.addActionListener(createFileListener);
        openFileButton.addActionListener(openFileListener);
        closeFileButton.addActionListener(closeFileListener);
        formatButton.addActionListener(formatListener);
        deleteButton.addActionListener(deleteListener);
        renameButton.addActionListener(renameListener);

        add(createFileButton);
        add(createFolderButton);
        add(openFileButton);
        add(closeFileButton);
        add(formatButton);
        add(deleteButton);
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


                FCB createdFile=system.createFile(fileName,catalogPane.selectedFCB.getFileName());//创建文件
                File entityFile=new File(fileName+".txt");//创建实体文件
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

                    FCB createdFolder=system.createFolder(folderName,catalogPane.selectedFCB.getFileName());
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
                if(catalogPane.selectedNode.isRoot())
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
        catalogPane.setPreferredSize(new Dimension(1000,400));
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
                    System.out.println("选中"+selectedFCB.getFileName());
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
        model.removeNodeFromParent(node);//可能会出BUG!!!!!!!!!!!!!!!!!!!1
        FCB fcb=nodeFCBMap.get(node);
        nodeFCBMap.remove(fcb);//从map中删除
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

    public FCB createFile(String name,String folderName)//创建文件
    {
        //检查是否重名
        if(checkDuplicateName(name,folderName)==true)
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


                FCB folderFCB=getFCB(name,folderName);//遍历目录链表，找到文件的父文件夹
                fileFCB.setFolderFCB(folderFCB);

                System.out.println("文件夹"+folderName+"下的名为"+name+"的文件被创建了");

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

    public FCB createFolder(String name,String folderName)
    {
        //检查同一路径下是否重名,若重名则无法创建
        if(checkDuplicateName(name,folderName)==true)
        {
            return null;
        }

        //若不重名，则创建文件夹
        else
        {
            FCB folderFCB=new FCB(name,folderName,FCB_TYPE.FOLDER);//创建文件夹FCB

            FCB parentFolderFCB=getFCB(name,folderName);
            parentFolderFCB.addChildren(folderFCB);//加入父文件夹子链表
            folderFCB.setFolderFCB(parentFolderFCB);

            System.out.println("文件夹"+folderName+"下的名为"+name+"的文件夹被创建了");
            parentFolderFCB.addChildren(folderFCB);//父文件夹的子链表中插入FCB
            fileCatalog.add(folderFCB);
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
                        deleteFolder(content);//递归删除文件夹中全部内容
                    }

                }

                break;
            }

        }

    }

    public void format(String name,String parentName)
    {

    }


    public boolean checkDuplicateName(String name,String parentName)
    {
        boolean isDuplicate=false;

        for(Iterator iter=fileCatalog.iterator();iter.hasNext();)
        {
            FCB indexFCB=(FCB)iter.next();
            if(indexFCB.getFileName()==name&&indexFCB.getFolderFCB().getFileName()==parentName)//如果同文件夹下重名
            {
                System.out.println(name+Constants.NAME_DUPLICATE_MESSAGE);
                isDuplicate=true;
                break;
            }
        }

        return isDuplicate;
    }

    public FCB getFCB(String name,String parentName)
    {
        FCB indexFCB=new FCB();
        for(Iterator iter=fileCatalog.iterator();iter.hasNext();)
        {
            indexFCB=(FCB)iter.next();
            if(indexFCB.getFileName()==name&&indexFCB.getFolderFCB().getFileName()==parentName)//如果同文件夹下重名
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

    public void freeBlock(int currentBlockNum)
    {
        freeBlockStack[top]=currentBlockNum;//要压栈的空闲块放于栈顶
        top++;//栈顶指针移位
        fatBlockList[currentBlockNum]=Constants.BLOCK_FREE;//FAT表中该项为空

    }//释放序号为currentBlockNum的物理块，压入空闲块栈

    public int getFreeBlock()
    {
        top--;
        return freeBlockStack[top];
    }//获取空闲的物理块

    public int getNextBlock(int currentBlockNum)
    {
        return fatBlockList[currentBlockNum];
    }//获取下一块序号

    public void setNextBlock(byte currentBlockNum,byte nextBlockNum)
    {
        fatBlockList[currentBlockNum]=nextBlockNum;
    }//设置块与块间链接

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

    public void deleteFromFAT(int startBlockNum)
    {
        int index=startBlockNum;
        while(!isEndBlock(index))
        {
            freeBlock(index);
            index=getNextBlock(index);
        }
    }//删除某个文件所占全部块


}

/*class fatBlock implements Serializable
{
    private int maxSize=Constants.BYTE_PER_BLOCK;
    private int blockId;
    private int nextBlockId;

    private int usedSize;
    private boolean isUsed;

    public fatBlock()
    {
        blockId=this.hashCode();
        isUsed=false;
        usedSize=0;
        nextBlockId=Constants.BLOCK_END;
    }

}*/



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

    private long createTime;//创建时间
    private long modifyTime;//修改时间

    public FCB()
    {
        this.fileSize=0;
        this.childrenList=new LinkedList<FCB>();
        this.createTime=System.currentTimeMillis();
        this.modifyTime=System.currentTimeMillis();
        this.isOpened=false;
    }

    public FCB(String name,FCB_TYPE type,int startBlock,int size)
    {

        this.fileSize=size;
        this.fileName=name;
        this.fcbType=type;

        this.childrenList=new LinkedList<FCB>();
        this.createTime=System.currentTimeMillis();
        this.modifyTime=System.currentTimeMillis();

        this.isOpened=false;
        this.fileStartBlock=startBlock;
        this.fileSize=size;
    }//构造根目录fcb

    public FCB(String name,FCB_TYPE type,FCB folder,int startBlock,int size)
    {
        this.fileName=name;
        this.fileSize=size;//大小初始化为0
        this.fcbType=type;
        this.folderFCB=folder;

        this.childrenList=new LinkedList<FCB>();

        this.createTime=System.currentTimeMillis();
        this.modifyTime=System.currentTimeMillis();
        this.isOpened=false;
        this.fileStartBlock=startBlock;
        this.fileSize=size;
    }

    public FCB(String name,String folderName,FCB_TYPE type)
    {
        this.childrenList=new LinkedList<FCB>();
        this.fileName=name;
        this.folderName=folderName;
        this.fcbType=type;

    }//构造文件夹FCB


    public int getBlockNeeded()
    {
        return  (int)(fileSize/Constants.BYTE_PER_BLOCK);

    }//获取所需块数

    public FCB getFolderFCB(){return folderFCB;}

    public void setFolderFCB(FCB folderFCB){this.folderFCB=folderFCB;}

    public FCB_TYPE getFcbType(){return fcbType;}

    public boolean getOpenedState(){return isOpened;}

    public String getFileName(){return fileName;}

    public int getFileSize(){return fileSize;}

    public int getFileStartBlock(){return fileStartBlock;}

    public void addChildren(FCB fcb)
    {
        this.childrenList.add(fcb);
    }

}

































