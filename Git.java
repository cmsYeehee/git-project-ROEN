import java.io.*;
public class Git {
    public static void main(String[] args) throws IOException {
        initRepo();
    }
    public static void initRepo() throws IOException
    {
        File file = new File("git");
        File fileObj = new File("git/objects");
        File fileText = new File ("git/objects/index");
        if (file.exists() && fileObj.exists() && fileText.exists())
        {
            System.out.println("Git repository already exists");
        }
        else{
        if (!file.exists())
        {
            file.mkdir();
            
        }
        if(!fileObj.exists())
            {
                fileObj.mkdir();
            }
        if(!fileText.exists())
            {
                fileText.createNewFile();
            }
        }


    }
}