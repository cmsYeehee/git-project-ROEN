import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Git {
    //universal compression toggle factor
    public static boolean compressionToggle = false;
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        
        File file = new File("git");
        File fileObj = new File("git/objects");
        File fileText = new File ("git/objects/index");
        File blobFile = new File ("data");
        File blob2File = new File ("dataBlob");
        
        //stretch goal 1: checks for a deletes all created files and directories to ensure their creation
        initCheckAndDeleteTester( fileText,  fileObj,  file);
        createBlobTester(blobFile, blob2File, fileText);
    

       


        
        //Need to finish stretch goal 1 here by checking for and deleting all the created directories and files
    }
    public static void createBlobTester(File blobFile, File blob2File, File fileText) throws IOException, NoSuchAlgorithmException
    {
        //resets the TestFiles by deleting them
        ResetTestFile(blobFile);
        ResetTestFile(blob2File);
        //writes into both files
        if (!blobFile.exists())
        {
            blobFile.createNewFile();
            Path blobPath = blobFile.toPath();
            BufferedWriter writer = Files.newBufferedWriter(blobPath);
            writer.write("derp");
            writer.close();
        }
        if (!blob2File.exists())
        {
            blob2File.createNewFile();
            Path blob2Path = blob2File.toPath();
            BufferedWriter writer = Files.newBufferedWriter(blob2Path);
            writer.write("Taasdofefawefahawefhwaeog");
            writer.close();
        }
        //Creates the blobs
        Git.createBlob(blobFile);
        Git.createBlob(blob2File);
        //This would be no compression blob creation
         if (!compressionToggle)
         {
            //manually inputted for the text inside, so don't change the text inside unless you want to change these lines
            File fileBlob = new File ("git/objects/e057d4ea363fbab414a874371da253dba3d713bc");
            File fileBlob2 = new File ("git/objects/873285fe864b9869ee9332a8baa58941ffbbd447"); 
            //determines if the blobs exist with the right name in the object folder
            if (fileBlob.exists() & fileBlob2.exists())
            {
                System.out.println("hash's correctly and puts files into the objects folder");
            }
            else{
                System.out.println("hash's incorrectly or does not put files into the objects folder");
            }
            //determines if the data inside the file is correct
            if (fileReader(fileBlob).equals("derp") && fileReader(fileBlob2).equals("Taasdofefawefahawefhwaeog"))
            {
                System.out.println("data inside the file is correct");
            }
            else{
                System.out.println("data inside the file is incorrect");
            }
            
            String fileIndexText = "";
            fileIndexText = fileReader(fileText);
            
            //determines if the index file is correctly updated
            if (fileIndexText.equals("e057d4ea363fbab414a874371da253dba3d713bc data\n873285fe864b9869ee9332a8baa58941ffbbd447 dataBlob"))
            {
                System.out.println("index file correct");
            }
            else{
                System.out.println("index file incorrect");
            }
            
         }
    }
    public static void deleteFiles(File fileText, File fileObj, File file) throws IOException
    {
        boolean deleted1 = false;
        boolean deleted2 = false;
        boolean deleted3 = false;
        if (fileText.exists()) 
        {
             deleted1 = fileText.delete();
            if (fileObj.exists())
            {
                File [] files = fileObj.listFiles();
                for (int i = 0; i < files.length; i ++)
                {
                    files[i].delete();
                }
                 deleted2 = fileObj.delete();
                if (file.exists())
                {
                     deleted3 = file.delete();
                }
            }
            
            
            if (deleted1 && deleted2 && deleted3)
            {
                System.out.println("deleted");
            }

        }
    }
    public static void checkForFiles(File fileText, File fileObj, File file) throws IOException
    {
        if (file.exists() && fileObj.exists() && fileText.exists())
        {
            System.out.println("Git repositories exist");
        }
    }
    public static void initCheckAndDeleteTester(File fileText, File fileObj, File file) throws IOException
    {
        //checks to see if all the files have been deleted each time you run through it
        deleteFiles(fileText, fileObj, file);
        //initializes the repository
            initRepo();
        //checks to see if files/directories exists after we create them
        checkForFiles(fileText, fileObj, file);
    }
    public static String fileReader (File file) throws IOException
    {
        BufferedReader reader = Files.newBufferedReader(file.toPath());
            String fileBlobText = "";
            fileBlobText += reader.readLine();
            while (reader.ready())
            {
            fileBlobText += "\n" + reader.readLine(); // will this still work for the last line?
            }
            return fileBlobText;
    }
    public static void ResetTestFile(File file)
    {
        if (file.exists())
        {
            file.delete();
        }

    }
    public static void initRepo() throws IOException
    {
        File file = new File("git");
        File fileObj = new File("git/objects");
        File fileText = new File ("git/objects/index");
        //checks to see if initialization already occurred, and if git repository already existed
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
        //if compression Toggle is on, create the files using the compression factors
        if (compressionToggle)
        {
            createBlobWithZip(file);
        }
        else{

        
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
        Files.copy(file.toPath(), targetFile,StandardCopyOption.REPLACE_EXISTING); 
        //edit the index file
        File indexFile = new File ("git/objects/index");
        Path indexPath = indexFile.toPath();

        //confused on why index function isn't working
        BufferedWriter writer = Files.newBufferedWriter(indexPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        //writer.write(currentFile);
        writer.write(hash);
        writer.write(' ');
        writer.write(file.getName());
        writer.write("\n");
        //writer.newLine();
        writer.close();
        }


        
        
        //read the file: check
        //convert that to hash code: check
        //put the file into the objects directory: check
        //copy the text over: check
        // move it into index: check
    }
    public static byte[] compressBlob(File file) throws IOException
    {
        BufferedReader reader = Files.newBufferedReader(file.toPath());
        String fileBlobText = "";
        fileBlobText += reader.readLine();
        while (reader.ready())
        {
            fileBlobText += "\n" + reader.readLine(); // will this still work for the last line?
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream zos = new GZIPOutputStream(baos);

            zos.write(fileBlobText.getBytes("UTF-8"));
            zos.finish();
            zos.flush();

            byte[] udpBuffer = baos.toByteArray();
            return udpBuffer;

    }
    public static void createBlobWithZip(File file) throws IOException, NoSuchAlgorithmException
    {
            byte [] compressed = compressBlob(file);
            String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(compressed);
            sha1 = byteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        File fileText = new File ("git/objects/" + sha1);
        
        if(!fileText.exists())
            {
                fileText.createNewFile();
            }
        Path targetFile = fileText.toPath();
        //copy the data into the new file, named target File: doesn't work: good
        Files.write(targetFile, compressed);
        //edit the index file: good
        File indexFile = new File ("git/objects/index");
        Path indexPath = indexFile.toPath();

        //confused on why index function isn't working
        BufferedWriter writer = Files.newBufferedWriter(indexPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        //writer.write(currentFile);
        writer.write(sha1);
        writer.write(' ');
        writer.write(file.getName());
        writer.write("\n");
        //writer.newLine();
        writer.close();

    }


    public static String findHash(Path path) throws IOException, NoSuchAlgorithmException
    {
        BufferedReader reader = Files.newBufferedReader(path);
        String fileText = "";
        fileText += reader.readLine();
        while (reader.ready())
        {
            fileText += "\n" + reader.readLine(); // will this still work for the last line?
        }
        //byte[] inputBytes = fileText.getBytes();
        //MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        //byte[] finalBytes = sha1.digest(inputBytes);
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(fileText.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return sha1;
    
    }
    private static String byteToHex(final byte[] hash)
{
    Formatter formatter = new Formatter();
    for (byte b : hash)
    {
        formatter.format("%02x", b);
    }
    String result = formatter.toString();
    formatter.close();
    //System.out.println (result);
    return result;
}
    
        
        
        //return result;
        //System.out.println(finalHash); //not going to be used laster on
        
    }

