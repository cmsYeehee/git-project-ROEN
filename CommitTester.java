import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class CommitTester {
    public static void main(String[]args) throws NoSuchAlgorithmException, IOException
    {
        Git test = new Git();
        test.MakeSnapshot();
    }
}
