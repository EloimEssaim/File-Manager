/**
 * Created by chrischen on 2017/6/22.
 */
public class FAT
{
    private int[] fileFAT;
    public FAT()
    {
        fileFAT=new int[Constants.BLOCK_NUM];
        for(int i=0;i<Constants.BLOCK_NUM;i++)
        {
            fileFAT[i]=Constants.BLOCK_UNUSED;
        }

    }

    public int findFreeBlock()
    {
        int freeBlock=-1;
        for(int i=0;i<Constants.BLOCK_NUM;i++)
        {
            if(fileFAT[i]==Constants.BLOCK_UNUSED)
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
        fileFAT[nextBlockId]=Constants.BLOCK_END;
    }

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
}
