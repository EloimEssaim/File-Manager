import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

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
        fcbProperty+="大小："+this.fileSize+"字节";
        fcbProperty+="\n";
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
