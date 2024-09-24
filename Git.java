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
    public static boolean compressionToggle = false;
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        
        File file = new File("git");
        File fileObj = new File("git/objects");
        File fileText = new File ("git/objects/index");
        File testFile = new File("test.txt");
        File blobFile = new File ("data");
        File blob2File = new File ("dataBlob");
        ResetTestFile(blobFile);
        ResetTestFile(blob2File);
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
        //Git.createBlob(blob2File);
        Git.createBlob(blobFile);
        Git.createBlob(blob2File);
         if (!compressionToggle)
         {
            File fileBlob = new File ("git/objects/e057d4ea363fbab414a874371da253dba3d713bc");
            File fileBlob2 = new File ("git/objects/873285fe864b9869ee9332a8baa58941ffbbd447");
            if (fileBlob.exists() & fileBlob2.exists())
            {
                System.out.println("hash's correctly and puts files into the objects folder");
            }
            else{
                System.out.println("hash's incorrectly or does not put files into the objects folder");
            }
            BufferedReader reader = Files.newBufferedReader(fileText.toPath());
            String fileBlobText = "";
            fileBlobText += reader.readLine();
            while (reader.ready())
            {
            fileBlobText += "\n" + reader.readLine(); // will this still work for the last line?
            }
            if (fileBlobText.equals("e057d4ea363fbab414a874371da253dba3d713bc data\n873285fe864b9869ee9332a8baa58941ffbbd447 dataBlob"))
            {
                System.out.println("input file correct");
            }
            else{
                System.out.println("input file incorrect");
            }
            
         }
       

        //need to do stretch goal 2 and 3 now

        
        //Need to finish stretch goal 1 here by checking for and deleting all the created directories and files
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
        if (compressionToggle)
        {
            createBlobWithZip(file);
        }
        else{

        
        //find the hash of the content within the file and save it: this woks, produces right hash and puts it into the right folder
        String hash = findHash(file.toPath());
        //Create a new file with the hash in the objects folder: good
        File fileText = new File ("git/objects/" + hash);
        
        if(!fileText.exists())
            {
                fileText.createNewFile();
            }
        Path targetFile = fileText.toPath();
        //copy the data into the new file, named target File: doesn't work: good
        Files.copy(file.toPath(), targetFile,StandardCopyOption.REPLACE_EXISTING); 
        //edit the index file: good
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

