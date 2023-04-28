package edu.yu.cs.com3800.stage4;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.FileHandler;

public class SimpleClient
{
        HttpRequest request;
        String hostName;
        int hostPort;
        HttpClient client;
        String responseString;
    
        protected SimpleClient(String hostName, int hostPort)
        {
            this.hostName = hostName;
            this.hostPort = hostPort;
    
            client = HttpClient.newBuilder().build();
            System.out.println("Client created");
        }

        public void sendCompileAndRunRequest(String src) throws IOException
        {    
            System.out.println("Sending compile and run request with " + src);
            this.request = HttpRequest.newBuilder()
            .uri(URI.create("http://" +this.hostName + ":" + this.hostPort + "/compileandrun"))
            .header("Content-Type", "text/x-java-source")
            .POST(HttpRequest.BodyPublishers.ofString(src))
            .build();
    
            HttpResponse<String> clientResponse = null;
    
            try
            {
               clientResponse = this.client.send(this.request, HttpResponse.BodyHandlers.ofString());
            } 
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            this.responseString = clientResponse.body();
            System.out.println("Client: "+this.responseString);
        }
        public String getResponseString()
        {
            return this.responseString;
        }
}
