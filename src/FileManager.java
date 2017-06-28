import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import java.lang.*;


public class FileManager extends JFrame implements Serializable
{


    public static void main(String[] args)
    {

        FileManagerUI ui=new FileManagerUI();
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.show();

    }

    /*public static Object readUI()
    {
        File f = new File("ui.ser");
        fileSystem temp = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(f);
            ois =  new ObjectInputStream(fis);
            temp = (fileSystem) ois.readObject();
            fis.close();
            ois.close();
        } catch (FileNotFoundException e) {
            temp = null;
        } catch (IOException e) {
            temp = null;
        } catch (ClassNotFoundException e) {
            temp = null;
        }
        return temp;
    }

    public static void saveUI()
    {
        File systemFile = new File("ui.ser");
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = new FileOutputStream(systemFile, false);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ui);
            fos.close();
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e)
    {
    }

    @Override
    public void windowClosing(WindowEvent e)
    {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e)
    {

    }*/


}


class fileSystem implements Serializable
{
    private LinkedList<FCB> fileCatalog=null;//目录
    private FCB fileRoot;//根目录
    private FAT fatList;//fat表

    public fileSystem()
    {
        this.fileCatalog=new LinkedList<FCB>();
        this.fileRoot=new FCB("root",FCB_TYPE.FOLDER,0,0);
        this.fatList=new FAT();
        fileCatalog.add(fileRoot);//加入目录
    }

    public FCB createFile(String name,FCB folderFCB)//创建文件
    {
        //检查是否重名
        if(checkDuplicateName(name,FCB_TYPE.FILE,folderFCB)==true)
        {
            JOptionPane.showMessageDialog(null,"Duplicate name!");
            return null;
        }

        //若不重名，则创建文件
        else
        {
            if(fatList.getTop()!=0)
            {
                int blockId=fatList.getFreeBlock();//获取空闲块
                FCB fileFCB=new FCB(name,FCB_TYPE.FILE,blockId,0);//创建文件
                fatList.setEndBlock(blockId);//设块内容为-1（因为文件目前还为空）

                FCB folder=getFCB(name,folderFCB);//遍历目录链表，找到文件的父文件夹
                fileFCB.setFolderFCB(folder);

                folderFCB.addChildren(fileFCB);//父文件夹的子链表插入儿子

                fileCatalog.add(fileFCB);//加入文件目录
                return fileFCB;
            }

            else
            {
                JOptionPane.showMessageDialog(null,"Space Full!");
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
                break;
            }
        }

        for(Iterator it=fileCatalog.iterator();it.hasNext();)//把父文件夹的孩子中也删除
        {
            FCB index=(FCB)it.next();
            if(index==fcbToDelete.getFolderFCB())//找到父文件夹
            {
                index.deleteFromChildren(fcbToDelete);
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
                break;
            }
        }

        for(Iterator it=fileCatalog.iterator();it.hasNext();)//把父文件夹的孩子中也删除
        {
            FCB index=(FCB)it.next();
            if(index==fcbToDelete.getFolderFCB())//找到父文件夹
            {
                index.deleteFromChildren(fcbToDelete);
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

        for(Iterator it=fileCatalog.iterator();it.hasNext();)//把父文件夹的孩子中也删除
        {
            FCB index=(FCB)it.next();
            if(index==fcbToDelete.getFolderFCB())//找到父文件夹
            {
                index.deleteFromChildren(fcbToDelete);
            }
        }

    }

    //格式化
    public void format(FCB fcb)
    {
        if(fcb.getFcbType()==FCB_TYPE.FILE)//格式化文件
        {
            fcb.setFileSize(0);
            fcb.setInnerText("");
            fcb.setModifyTime();
            fatList.formatFromFAT(fcb.getFileStartBlock());
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
    public FCB rename(String newName,FCB fcbToRename)
    {
        FCB newFCB=new FCB();
        for(Iterator iter=fileCatalog.iterator();iter.hasNext();)
        {
            FCB indexFCB=(FCB)iter.next();
            if(indexFCB==fcbToRename)
            {
                indexFCB.setFileName(newName);
                fcbToRename.setModifyTime();
                newFCB=indexFCB;
                break;
            }
        }
        return newFCB;

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

    public FCB getFileRoot()
    {
        return fileRoot;
    }//获取根目录FCB

    public FAT getFatList(){return fatList;}//获取FAT

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
        for(int i=Constants.BLOCK_NUM-1;i>=1;i--)
        {
            freeBlock(i);
        }//初始化，0-255块设为空闲块

        fatBlockList[0]=-1;


    }

    //释放序号为currentBlockNum的物理块，压入空闲块栈
    public void freeBlock(int currentBlockNum)
    {

        freeBlockStack[top]=currentBlockNum;//要压栈的空闲块放于栈顶
        top++;//栈顶指针移位
        System.out.println(top);
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
    public void setNextBlock(int currentBlockNum,int nextBlockNum)
    {
        fatBlockList[currentBlockNum]=nextBlockNum;
    }

    public int getTop()
    {
        return top;
    }

    public void setEndBlock(int currentBlockNum)
    {
        fatBlockList[currentBlockNum]=Constants.BLOCK_END;
    }//设置块末尾

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
        int num=0;
        if(isEndBlock(index))
        {
            freeBlock(index);
        }

        else
        {
            while(!isEndBlock(index))
            {
                num=index;
                index=getNextBlock(index);
                freeBlock(num);
            }

            freeBlock(index);
        }

    }

    public void formatFromFAT(int startBlockNum)
    {
        int index=startBlockNum;
        int num=0;

        while(!isEndBlock(index))
        {
            num=index;
            index=getNextBlock(index);
            freeBlock(num);
        }

        freeBlock(index);
        setEndBlock(startBlockNum);
        top--;

    }
    //将文件存入FAT
    public void saveToFAT(FCB fcb)
    {
        if(this.top<fcb.getBlockNeeded())//如果空闲块不够
        {
            JOptionPane.showMessageDialog(null,"空间已满！");
        }

        else//有空闲块
        {
            fcb.setFileStartBlock(getFreeBlock());//从空闲栈中获取新块

            if(fcb.getFileSize()<=Constants.BYTE_PER_BLOCK)//如果文件大小小于块大小
            {
                setEndBlock(fcb.getFileStartBlock());
            }

            else if(fcb.getFileSize()>Constants.BYTE_PER_BLOCK)//如果文件大小大于块大小
            {
                int blockSum=fcb.getBlockNeeded();
                int startBlock=fcb.getFileStartBlock();

                for(int i=0;i<blockSum-1;i++)
                {
                    setNextBlock(startBlock,getFreeBlock());
                    startBlock=getNextBlock(startBlock);
                }
                setEndBlock(startBlock);
                top--;
            }
        }

    }

    public int[] getFatBlockList()
    {
        return this.fatBlockList;
    }

}

class FCB implements Serializable
{
    private String fileName;//文件名
    private String innerText;//文件内容
    private FCB_TYPE fcbType;//是文件还是文件夹

    private FCB folderFCB;//文件夹的FCB
    public LinkedList<FCB> childrenList;//如果是文件夹，则存放文件夹内的文件或文件夹

    private int fileSize;//文件大小(占多少个字节）
    private int fileStartBlock;//开始盘块
    private int blockSum;//所占盘块数

    private String createTime;//创建时间
    private String modifyTime;//修改时间

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public FCB()
    {
        this.fileSize=0;
        this.childrenList=new LinkedList<FCB>();
        this.innerText="";
        Date date = new Date();
        this.createTime = formatter.format(date);
        this.modifyTime=formatter.format(date);
        this.blockSum=1;

    }

    //构造根目录fcb
    public FCB(String name,FCB_TYPE type,int startBlock,int size)
    {

        this.fileSize=size;
        this.fileName=name;
        this.fcbType=type;
        this.innerText=null;
        this.childrenList=new LinkedList<FCB>();

        this.fileStartBlock=startBlock;
        this.fileSize=size;
        this.blockSum=1;
        Date date = new Date();
        this.createTime = formatter.format(date);
        this.modifyTime=formatter.format(date);

    }

    public FCB(String name,FCB_TYPE type,FCB folder,int startBlock,int size)
    {
        this.fileName=name;
        this.fileSize=size;//大小初始化为0
        this.fcbType=type;
        this.folderFCB=folder;
        this.blockSum=1;/////////
        this.childrenList=new LinkedList<FCB>();
        this.innerText="";
        this.fileStartBlock=startBlock;
        this.fileSize=size;

        Date date = new Date();
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

        if(this.getFcbType()==FCB_TYPE.FILE)
        {
            fcbProperty+="大小："+this.fileSize+"字节";
            fcbProperty+="\n";
        }
        fcbProperty+="创建时间："+this.createTime;
        fcbProperty+="\n";
        fcbProperty+="修改时间："+this.modifyTime;
        fcbProperty+="\n";
        fcbProperty+="开始盘块："+this.fileStartBlock;
        fcbProperty+="\n";
        return fcbProperty;
    }

    public void save(String content)
    {
        this.innerText=content;
        byte[] fileBytes=content.getBytes();//String转byte
        this.fileSize=fileBytes.length;
        this.blockSum=getBlockNeeded();//计算所需盘块数

        Date date = new Date();
        this.modifyTime=formatter.format(date);//改变修改时间
    }

    //获取所需块数
    public int getBlockNeeded()
    {
        return  (int)((fileSize/Constants.BYTE_PER_BLOCK)+1);

    }

    public FCB getFolderFCB(){return folderFCB;}

    public void setFolderFCB(FCB folderFCB){this.folderFCB=folderFCB;}

    public void deleteFromChildren(FCB fcb){this.childrenList.remove(fcb);}

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



    public String getInnerText() {return this.innerText;}

    public void setInnerText(String newText){this.innerText=newText;}

    public void setFileStartBlock(int startBlock){this.fileStartBlock=startBlock;}

}





































