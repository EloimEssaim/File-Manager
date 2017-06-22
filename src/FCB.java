import java.util.*;

public class FCB
{
    private int id;
    private int fatherId;

    public boolean isOpened;//文件是否被打开
    public String fileName;//文件名
    private String filePath;//文件路径

    private int fileSize;//文件大小
    private int fileStartBlock;//开始盘块

    Calendar createTime;//创建时间

    public FCB()
    {

    }

    public FCB(String name)
    {
        fileName=name;
    }

    public int getId()
    {
        return id;
    }

    public int getFatherId()
    {
        return fatherId;
    }


}
