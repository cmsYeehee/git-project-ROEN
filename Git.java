import java.io.*;
public class Git {
    public static void main(String[] args) throws IOException {
        
        File file = new File("git");
        File fileObj = new File("git/objects");
        File fileText = new File ("git/objects/index");
        if (fileText.exists()) //does kind of assume that fileText has to exist to delete anything, but I think it's fine because it's my tester, not a method. 
        {
            fileText.delete();
            fileObj.delete();
            file.delete();
            if (!file.exists()&& !fileObj.exists() && !fileText.exists() )
            {
                System.out.println("deleted");
            }
            
        }
        initRepo();
        if (file.exists() && fileObj.exists() && fileText.exists())
        {
            System.out.println("Git repositories exist");
        }
        

        //Need to finish stretch goal 1 here by checking for and deleting all the created directories and files
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