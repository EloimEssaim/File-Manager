import java.io.*;
import java.util.*;
import javax.swing.*;
import java.lang.*;

public class FileManager
{

    public static void main(String[] args)
    {
        FileManagerUI fileManagerUI=new FileManagerUI();
        fileCatalog myFileCatalog=new fileCatalog();
        FAT myFAT=new FAT();


    }
}



class fileCatalog
{
    private FCB fileRoot;//文件根目录
    private FCB currentDirectory;//当前目录

    public fileCatalog()
    {
        this.fileRoot=new FCB("Root",FCB_TYPE.FOLDER);//创建文件根目录
        this.currentDirectory=fileRoot;//设根目录为当前目录
    }

    public FCB getRoot()
    {
        return fileRoot;
    }

    public boolean createFile()
    {
        return false;
    }

    public boolean createFolder()
    {
        return false;
    }

    public boolean deleteFile()
    {
        return false;
    }

    public boolean deleteFolder()
    {
        return false;
    }


}

class FCB
{
    private FCB_TYPE fcbType;//是文件还是文件夹
    private int id;
    private int fatherId;

    private boolean isOpened;//文件是否被打开
    private String fileName;//文件名

    private String filePath;//文件路径

    private int fileSize;//文件大小
    private int fileStartBlock;//开始盘块

    private long createTime;//创建时间
    private long modifyTime;//修改时间

    public FCB()
    {

    }

    public FCB(String name,FCB_TYPE type)
    {
        this.fileName=name;
        this.fileSize=0;//大小初始化为0
        this.fcbType=type;
        this.createTime=System.currentTimeMillis();
        this.modifyTime=System.currentTimeMillis();

    }

    public int getId() {return id;}

    public int getFatherId() {return fatherId;}

    public FCB_TYPE getFcbType(){return fcbType;}

    public boolean getOpenedState(){return isOpened;}

    public String getFileName(){return fileName;}

    public int getFileSize(){return fileSize;}

    public int getFileStartBlock(){return fileStartBlock;}


}

class FAT implements Serializable//文件FAT表
{
    private int[] fileFAT;
    private int[] freeBlockList;

    public FAT()
    {
        fileFAT=new int[Constants.BLOCK_NUM];
        for(int i=0;i<Constants.BLOCK_NUM;i++)
        {
            fileFAT[i]=Constants.BLOCK_FREE;//初始化
        }

    }

    public int findFreeBlock()
    {
        int freeBlock=-1;
        for(int i=0;i<Constants.BLOCK_NUM;i++)
        {
            if(fileFAT[i]==Constants.BLOCK_FREE)
            {
                freeBlock=i;
                break;
            }
        }
        return freeBlock;
    }

    public void getUsedBlock()
    {

    }

    public void setNext(int currentBlockId,int nextBlockId)
    {
        fileFAT[currentBlockId]=nextBlockId;
    }

    public int getNext(int currentBlockId)
    {
        return fileFAT[currentBlockId];
    }//获取下一块块号

    public void setLast(int currentBlockId)
    {
        fileFAT[currentBlockId]=Constants.BLOCK_END;
    }

    public boolean isLast(int currentBlockId)
    {
        if(fileFAT[currentBlockId]==Constants.BLOCK_END)
            return true;
        else
            return false;
    }//判断是否为最后一块

}


class fileBlock
{

    private int blockId;
    private int nextBlockId;
    private int usedSize;
    private boolean used;

    public fileBlock()
    {
        used=false;
        usedSize=0;
        nextBlockId=-1;
    }

    public int getBlockId() {return blockId;}

    public int getNextBlockId() {return nextBlockId;}

    public int getUsedSize() {return usedSize;}

    public boolean getUsedState(){return used;}

}
















