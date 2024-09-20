import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    public static void createBlob(Path path) throws IOException, NoSuchAlgorithmException
    {
        String hash = findHash(path);

        
        
        //read the file
        //convert that to hash code
        //put the file into the objects directory
        //copy the text over
    }

    public static String findHash(Path path) throws IOException, NoSuchAlgorithmException
    {
        BufferedReader reader = Files.newBufferedReader(path);
        String fileText = "";
        while (reader.ready())
        {
            fileText += reader.readLine() + "\n"; // will this still work for the last line?
        }
        byte[] inputBytes = fileText.getBytes();
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] finalBytes = sha1.digest(inputBytes);
        String finalHash = finalBytes.toString();
        return finalHash;
        //System.out.println(finalHash); //not going to be used laster on
    }

}