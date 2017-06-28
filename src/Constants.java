enum FCB_TYPE
{
    FILE,
    FOLDER
}


//FAT 块为-1，为FAT结尾；块为-2则空
public class Constants
{
    public static final int BLOCK_NUM=256;//256个块
    public static final int BYTE_PER_BLOCK=64;//一块64B
    public static final int BLOCK_END=-1;//块结束
    public static final int BLOCK_FREE=0;//块为空

}



