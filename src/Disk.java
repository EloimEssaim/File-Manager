
//用256*256的int型数组模拟磁盘空间
public class Disk
{
    private int[][] disk;
    public Disk()
    {
        disk=new int[Constants.BLOCK_NUM][Constants.BYTE_PER_BLOCK];

    }

    public void reloadDisk()
    {

    }

}
