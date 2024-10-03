import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Git {
    // universal compression toggle factor
    public static boolean compressionToggle = false;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        MakeHead();
        File file = new File("git");
        File fileObj = new File("git/objects");
        File fileText = new File("git/index");
        File blobFile = new File("data");
        File blob2File = new File("dataBlob");

        File directoryFile = new File("directoryFile");
        File random = new File("directoryFile/random");
        File random2 = new File("directoryFile/random/random2.txt");

        // // stretch goal 1: checks for a deletes all created files and directories to
        // // ensure their creation
        initCheckAndDeleteTester(fileText, fileObj, file);
        createBlobTester(blobFile, blob2File, fileText);
        addTreeTester(directoryFile, random, random2, fileText);

        // Need to finish stretch goal 1 here by checking for and deleting all the
        // created directories and files
        //MakeCommitFile("Christian Stubbeman", "testing");
        //MakeSnapshot();
    }

    public static void createBlobTester(File blobFile, File blob2File, File fileText)
            throws IOException, NoSuchAlgorithmException {
        // resets the TestFiles by deleting them
        ResetTestFile(blobFile);
        ResetTestFile(blob2File);
        // writes into both files
        if (!blobFile.exists()) {
            blobFile.createNewFile();
            Path blobPath = blobFile.toPath();
            BufferedWriter writer = Files.newBufferedWriter(blobPath);
            writer.write("derp");
            writer.close();
        }
        if (!blob2File.exists()) {
            blob2File.createNewFile();
            Path blob2Path = blob2File.toPath();
            BufferedWriter writer = Files.newBufferedWriter(blob2Path);
            writer.write("Taasdofefawefahawefhwaeog");
            writer.close();
        }
        // Creates the blobs
        Git.createBlob(blobFile);
        Git.createBlob(blob2File);
        // This would be no compression blob creation
        if (!compressionToggle) {
            // manually inputted for the text inside, so don't change the text inside unless
            // you want to change these lines
            File fileBlob = new File("git/objects/e057d4ea363fbab414a874371da253dba3d713bc");
            File fileBlob2 = new File("git/objects/873285fe864b9869ee9332a8baa58941ffbbd447");

            // determines if the blobs exist with the right name in the object folder
            if (fileBlob.exists() & fileBlob2.exists()) {
                System.out.println("hash's correctly and puts files into the objects folder");
            } else {
                System.out.println("hash's incorrectly or does not put files into the objects folder");
            }
            // determines if the data inside the file is correct
            if (fileReader(fileBlob).equals("derp") && fileReader(fileBlob2).equals("Taasdofefawefahawefhwaeog")) {
                System.out.println("data inside the file is correct");
            } else {
                System.out.println("data inside the file is incorrect");
            }

            String fileIndexText = "";
            fileIndexText = fileReader(fileText);

            // determines if the index file is correctly updated
            if (fileIndexText.equals(
                    "blob e057d4ea363fbab414a874371da253dba3d713bc data\nblob 873285fe864b9869ee9332a8baa58941ffbbd447 dataBlob")) {
                System.out.println("index file correct");
            } else {
                System.out.println("index file incorrect");
            }

        }
    }

    public static void deleteFiles(File fileText, File fileObj, File file) throws IOException {
        boolean deleted1 = false;
        boolean deleted2 = false;
        boolean deleted3 = false;
        if (fileText.exists()) {
            deleted1 = fileText.delete();
            if (fileObj.exists()) {
                File[] files = fileObj.listFiles();
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
                deleted2 = fileObj.delete();
                if (file.exists()) {
                    deleted3 = file.delete();
                }
            }

            if (deleted1 && deleted2 && deleted3) {
                System.out.println("deleted");
            }

        }
    }

    public static void checkForFiles(File fileText, File fileObj, File file) throws IOException {
        if (file.exists() && fileObj.exists() && fileText.exists()) {
            System.out.println("Git repositories exist");
        }
    }

    public static void initCheckAndDeleteTester(File fileText, File fileObj, File file) throws IOException {
        // checks to see if all the files have been deleted each time you run through it
        deleteFiles(fileText, fileObj, file);
        // initializes the repository
        initRepo();
        // checks to see if files/directories exists after we create them
        checkForFiles(fileText, fileObj, file);
    }

    public static String fileReader(File file) throws IOException {
        BufferedReader reader = Files.newBufferedReader(file.toPath());
        String fileBlobText = "";
        fileBlobText += reader.readLine();
        while (reader.ready()) {
            fileBlobText += "\n" + reader.readLine(); // will this still work for the last line?
        }
        return fileBlobText;
    }

    public static void ResetTestFile(File file) {
        if (file.exists()) {
            file.delete();
        }

    }

    public static void initRepo() throws IOException {
        File file = new File("git");
        File fileObj = new File("git/objects");
        File fileText = new File("git/index");
        // checks to see if initialization already occurred, and if git repository
        // already existed
        if (file.exists() && fileObj.exists() && fileText.exists()) {
            System.out.println("Git repository already exists");
        } else {
            if (!file.exists()) {
                file.mkdir();

            }
            if (!fileObj.exists()) {
                fileObj.mkdir();
            }
            if (!fileText.exists()) {
                fileText.createNewFile();
            }
        }

    }

    public static void createBlob(File file) throws IOException, NoSuchAlgorithmException {
        // if compression Toggle is on, create the files using the compression factors
        if (compressionToggle) {
            createBlobWithZip(file);
        } else {

            // find the hash of the content within the file and save it
            String hash = findHash(file.toPath());
            // Create a new file with the hash in the objects folder
            File fileText = new File("git/objects/" + hash);

            if (!fileText.exists()) {
                fileText.createNewFile();
            }
            Path targetFile = fileText.toPath();
            // copy the data into the new file, named target File
            Files.copy(file.toPath(), targetFile, StandardCopyOption.REPLACE_EXISTING);
            // edit the index file
            addToIndexFile(file);
        }

        // read the file: check
        // convert that to hash code: check
        // put the file into the objects directory: check
        // copy the text over: check
        // move it into index: check
    }

    public static void addToIndexFile(File file) throws NoSuchAlgorithmException, IOException {
        String hash = findHash(file.toPath());

        File indexFile = new File("git/index");
        Path indexPath = indexFile.toPath();

        // confused on why index function isn't working
        BufferedWriter writer = Files.newBufferedWriter(indexPath, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
        // writer.write(currentFile);
        if (file.isDirectory()) {
            writer.write("tree ");
        } else {
            writer.write("blob ");
        }
        writer.write(hash);
        writer.write(' ');
        writer.write(file.getPath());
        writer.write("\n");
        writer.close();
    }

    public static byte[] compressBlob(File file) throws IOException {
        BufferedReader reader = Files.newBufferedReader(file.toPath());
        String fileBlobText = "";
        fileBlobText += reader.readLine();
        while (reader.ready()) {
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

    public static void createBlobWithZip(File file) throws IOException, NoSuchAlgorithmException {
        byte[] compressed = compressBlob(file);
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(compressed);
            sha1 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        File fileText = new File("git/objects/" + sha1);

        if (!fileText.exists()) {
            fileText.createNewFile();
        }
        Path targetFile = fileText.toPath();
        // copy the data into the new file, named target File: doesn't work: good
        Files.write(targetFile, compressed);
        // edit the index file: good
        File indexFile = new File("git/index");
        Path indexPath = indexFile.toPath();

        // confused on why index function isn't working
        BufferedWriter writer = Files.newBufferedWriter(indexPath, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
        // writer.write(currentFile);
        writer.write(sha1);
        writer.write(' ');
        writer.write(file.getName());
        writer.write("\n");
        // writer.newLine();
        writer.close();

    }

    public static String findHash(Path path) throws IOException, NoSuchAlgorithmException {
        BufferedReader reader = Files.newBufferedReader(path);
        String fileText = "";
        fileText += reader.readLine();
        while (reader.ready()) {
            fileText += "\n" + reader.readLine(); // will this still work for the last line?
        }
        // byte[] inputBytes = fileText.getBytes();
        // MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        // byte[] finalBytes = sha1.digest(inputBytes);
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(fileText.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sha1;

    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        // System.out.println (result);
        return result;
    }

    // return result;
    // System.out.println(finalHash); //not going to be used laster on

    public static void addTree(File file) throws NoSuchAlgorithmException, IOException {
        String allFiles = "";
        String hash = getDirectoryHash(file);
        String inObj = "tree " + hash + " " + file.getPath() + "\n";
        if (file.isDirectory()) {
            File[] arrFiles = file.listFiles();
            for (int i = 0; i < arrFiles.length; i++) {
                if (arrFiles[i].isDirectory()) {
                    addTree(arrFiles[i]);
                    allFiles += ("tree " + getDirectoryHash(arrFiles[i]) + " " + arrFiles[i].getPath() + "\n");
                } else {
                    createBlob(arrFiles[i]);
                    allFiles += "blob " +  hashBlob(Files.readAllBytes(arrFiles[i].toPath())) + " " + arrFiles[i].getPath() + "\n";
                }
            }
        }
        File indexFile = new File("git/index");
        File objectFile = new File("git/objects/" + getDirectoryHash(file));
        Path indexPath = indexFile.toPath();
        Path objectPath = objectFile.toPath();
        BufferedWriter OBJwriter = Files.newBufferedWriter(objectPath, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
        BufferedWriter writer = Files.newBufferedWriter(indexPath, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
        // writer.write(currentFile);
        if (file.isDirectory()) {
            OBJwriter.write(allFiles);
            writer.write(inObj);
            writer.close();
            OBJwriter.close();
            return;
        } else {
            OBJwriter.write(allFiles);
            writer.write(inObj);
        }
        writer.close();
        OBJwriter.close();

    }

    public static void addTreeTester(File directoryFile, File random, File random2, File fileText)
            throws NoSuchAlgorithmException, IOException {
        // resets the TestFiles by deleting them
        ResetTestFile(directoryFile);
        ResetTestFile(random);
        ResetTestFile(random2);

        // writes into directory
        if (!directoryFile.exists()) {
            directoryFile.mkdir();
        }
        if (!random.exists()) {
            random.mkdir();
        }
        if (!random2.exists()) {
            random2.createNewFile();
            Path blobPath = random2.toPath();
            BufferedWriter writer = Files.newBufferedWriter(blobPath);
            writer.write("derp2");
            writer.close();
        }

        addTree(directoryFile);

        String fileIndexText = "";
        fileIndexText = fileReader(fileText);

        System.out.println(fileIndexText);

        // if (fileIndexText.equals(
        // "blob e057d4ea363fbab414a874371da253dba3d713bc git/tree/random/random2.txt"))
        // {
        // System.out.println("index file correct");
        // } else {
        // System.out.println("index file incorrect");
        // }
        // }
    }
    //Makes head for git
    public static void MakeHead() throws IOException
    {
        File Head = new File("git/HEAD");
        Path commitPath = Head.toPath();
        BufferedWriter writer = Files.newBufferedWriter(commitPath);
        writer.close();
        if (Head.createNewFile() == true)
        {
            System.out.println("HEAD is made");
        }
        else{
            System.out.println("HEAD is not made");
        }
        return;
    }

    public static void MakeCommitFile(String author, String message) throws IOException, NoSuchAlgorithmException
    {
        Date d1 = new Date(); 
        System.out.println("Current date is " + d1); 
        String treeHash = makeTreeForCommit();
        File Head = new File("git/HEAD");
        byte[] data = Files.readAllBytes(Head.toPath());
        String content = new String(data, StandardCharsets.UTF_8);
        System.out.println("The content is " + content);
        File toCommit = new File("git/objects/" + treeHash);
        Path commitPath = toCommit.toPath();
        BufferedWriter writer = Files.newBufferedWriter(commitPath);
        writer.write("tree " + treeHash);
        writer.write("\nparent " + content);
        writer.write("\nauthor " + author);
        writer.write("\ndate " + d1);
        writer.write("\nmessage " + message);
        writer.close();
        Path headPath = Head.toPath();
        BufferedWriter headwriter = Files.newBufferedWriter(headPath);
        headwriter.write(treeHash);
        writer.close();
        if (toCommit.createNewFile() == true)
        {
            System.out.println("commit file outline made");
        }
        return;
    }
    //Makes a snapshot
    public static void MakeSnapshot() throws NoSuchAlgorithmException, IOException
    {
        File Snapshot = new File("git/Snapshot");
        Snapshot.mkdir();
        File index = new File("git/index");
        BufferedReader br = new BufferedReader(new FileReader(index));
        String line = br.readLine();
        ArrayList<String> lines = new ArrayList<String>();
        //Checking through index to determine what to add to the snapshot folder.
        while (line != null)
        {
            lines.add(line);
            line = br.readLine();
            //Have to go from back to create directories
        }
        br.close();
        for (int i = lines.size(); i > 1; i --)
        {
            String theLine = lines.get(i-1);
            System.out.println("the Line is " + theLine);
            if (theLine.substring(0,4).equals("blob"))
            {  
            String path = theLine.substring(46);
            File newFile = new File("./git/Snapshot/" + path);
            if (newFile.createNewFile() == true)
            {
            }
            File pastFile = new File("./" + path);
            Path newPath = newFile.toPath();
            System.out.println("./" + path);
            BufferedWriter writer = Files.newBufferedWriter(newPath);
            byte[] data = Files.readAllBytes(pastFile.toPath());
            String content = new String(data, StandardCharsets.UTF_8);
            writer.write(content);
            writer.close();
            if (newFile.createNewFile())
            {
                createBlob(pastFile);
            }
            }
            else if (theLine.substring(0,4).equals("tree"))
            {
                String path = theLine.substring(46);
                File dirToMake = new File("./git/Snapshot/" + path);
                dirToMake.mkdir();
            }
        }
        return;

    }
    //gets the Hash for this tree
    public static String makeTreeForCommit() throws NoSuchAlgorithmException, IOException
    {
        File snapshot = new File("./git/Snapshot");
        String str = getDirectoryHash(snapshot);

        return str;
    }






    //uses the index file to determine what to put in the snapshot.
//Gets the specific hash for a directory
public static String getDirectoryHash(File file) throws NoSuchAlgorithmException, IOException
{
    File directory = file;
    String allFiles = "";
    byte[] hashes;
    for (File subfile : directory.listFiles())
    {
        if (subfile.isDirectory())
        {
            //Recursively calls itself
            allFiles += getDirectoryHash(subfile);
        }
        else
        {
            allFiles += "blob " +  hashBlob(Files.readAllBytes(subfile.toPath())) + " " + subfile.getName() + "\n";
        }
    }
    hashes = allFiles.getBytes();
    String finalHash;
    finalHash = hashBlob(hashes);
    return finalHash;
}
public static String hashBlob(byte[] data) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] messageDigest = md.digest(data);
        BigInteger n = new BigInteger(1, messageDigest);
        String hash = n.toString(16);
        while (hash.length() < 40)
            hash = "0" + hash;
        return hash;
    }
public static void doACommit()
{

}
// isFile()
// isDirectory()
// listFiles()
} 
