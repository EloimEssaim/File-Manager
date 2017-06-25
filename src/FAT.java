import javax.swing.*;
import java.io.Serializable;

class FAT implements Serializable//文件FAT表
{
    private int[] fatBlockList;
    private int[] freeBlockStack;//空闲块栈

    private int top=0;//栈顶指针

    public FAT()
    {
        fatBlockList=new int[Constants.BLOCK_NUM];
        freeBlockStack=new int[Constants.BLOCK_NUM];
        for(int i=Constants.BLOCK_NUM-1;i>=0;i--)
        {
            freeBlock(i);
        }//初始化，0-255块设为空闲块


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

