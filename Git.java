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
    public static void createBlob(File file) throws IOException, NoSuchAlgorithmException
    {
        //find the hash of the content within the file and save it
        String hash = findHash(file.toPath());
        //Create a new file with the hash in the objects folder
        File fileText = new File ("git/objects/" + hash);
        if(!fileText.exists())
            {
                fileText.createNewFile();
            }
        Path targetFile = fileText.toPath();
        //copy the data into the new file, named target File
        Files.copy(file.toPath(), targetFile);
        //edit the index file
        BufferedWriter writer = Files.newBufferedWriter(targetFile);
        writer.newLine();
        writer.write(hash);
        writer.write(' ');
        writer.write(file.getName());
        writer.close();


        
        
        //read the file: check
        //convert that to hash code: check
        //put the file into the objects directory: check
        //copy the text over: check
        // move it into index: check
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