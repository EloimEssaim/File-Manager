/**
 * Created by chrischen on 2017/6/22.
 */
public class FileCatalog
{
    private FCB fileRoot;//文件根目录

    public FileCatalog()
    {
        fileRoot=new FCB("Root");
    }

    public FCB getRoot()
    {
        return fileRoot;
    }

    public boolean create()
    {
        return false;
    }

    public boolean delete()
    {
        return false;
    }



}
