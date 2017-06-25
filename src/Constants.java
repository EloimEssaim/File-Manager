enum FCB_TYPE
{
    FILE,
    FOLDER
}

enum ERROR_TYPE
{
    NAME_DUPLICATE,
    NAME_ILLEGAL
}


//FAT 块为-1，为FAT结尾；块为-2则空
public class Constants
{
    public static final int BLOCK_NUM=256;//256个块
    public static final int BYTE_PER_BLOCK=64;//一块64B
    public static final int BLOCK_END=-1;//块结束
    public static final int BLOCK_FREE=0;//块为空
    public static final int CATALOG_BLOCK=-3;//根目录

    public static final int CATALOG_NUM=32;//根目录32条
    public static final int CATALOG_ITEM_SIZE=16;//每个目录项16B

    public static final int ROOT_PARENT=-1;

    public static final String NAME_DUPLICATE_MESSAGE="重名！";
    public static final String SPACE_FULL_MESSAGE="空间已满！";

    public static final int NO_FREE_BLOCK=-1000;


}



