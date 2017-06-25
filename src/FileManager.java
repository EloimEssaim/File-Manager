import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.lang.*;


public class FileManager extends JFrame
{

    public static void main(String[] args)
    {
        fileSystem myFileSystem=new fileSystem();
        FileManagerUI ui=new FileManagerUI(myFileSystem);
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.show();

    }
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

    public void setFatList(FAT fatList){this.fatList=fatList;}


}





































