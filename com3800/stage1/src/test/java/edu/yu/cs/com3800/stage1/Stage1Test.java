package edu.yu.cs.com3800.stage1;

import org.junit.jupiter.api.Test;

import edu.yu.cs.com3800.stage1.Client.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Unit test for simple App.
 */
class Stage1Test {
    
    @Test
    public void simpleClientTest() throws IOException
    {
        System.out.println("Running simple Client test");
        Client client = new ClientImpl("localhost", 9000);
        System.out.println("Sending Compile and run request");
        client.sendCompileAndRunRequest("public class Test { public String run() { return \"hello\";}}");
        Response response = client.getResponse();
        System.out.println("Response: "+ response.getBody());    
    }

    @Test
    public void responseTest() throws IOException
    {
        Client client = new ClientImpl("localhost", 9000);
        client.sendCompileAndRunRequest("public class Test { public String run() { return \"hello\";}}");

        Response response = client.getResponse();
        String ExpectedResponse = "This is the response";
        String actualResponse = response.getBody();

        System.out.println("Expected response:");
        System.out.println("[ " + ExpectedResponse + " ]");
        System.out.println("Actual response:");
        System.out.println("[ " + actualResponse + " ]");
        assertEquals(ExpectedResponse, actualResponse);
    }
}
