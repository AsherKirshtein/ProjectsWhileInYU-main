package edu.yu.cs.com3800.stage1;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.FileHandler;

public class ClientImpl implements Client
{
    private Response response;  
    private HttpRequest request;
    private String hostName;
    private int hostPort;
    private HttpClient client;

    public ClientImpl(String hostName, int hostPort) throws MalformedURLException
    {
        this.hostName = hostName;
        this.hostPort = hostPort;

        client = HttpClient.newBuilder().build();

    }
    
    @Override
    public void sendCompileAndRunRequest(String src) throws IOException
    {    
        this.request = HttpRequest.newBuilder().uri(URI.create("http://" +this.hostName + ":" + this.hostPort + "/compileandrun"))
        .header("Content-Type", "text/x-java-source").POST(HttpRequest.BodyPublishers.ofString(src)).build();

        HttpResponse<String> clientResponse = null;

        try
        {
           clientResponse = this.client.send(this.request, HttpResponse.BodyHandlers.ofString());
        } 
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        String responseString = clientResponse.body();
        this.response = new Response(clientResponse.statusCode(), responseString);
    }

    @Override
    public Response getResponse() throws IOException 
    {
        return this.response;
    }
    
}
