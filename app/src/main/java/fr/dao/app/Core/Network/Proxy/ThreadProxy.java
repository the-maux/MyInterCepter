package fr.dao.app.Core.Network.Proxy;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class                    ThreadProxy extends Thread {
    private String              TAG = "ThreadProxy";
    private Socket              sClient;
    private final String        SERVER_URL;
    private final int           SERVER_PORT;

    ThreadProxy(Socket sClient, String ServerUrl, int ServerPort) {
        this.SERVER_URL = ServerUrl;
        this.SERVER_PORT = ServerPort;
        this.sClient = sClient;
        Log.i(TAG, "ThreadProxy for " + SERVER_URL + ":" + SERVER_PORT + " on port " + sClient.getPort());
        this.start();
    }

    public void                 run() {
        try {
            final byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            final InputStream inFromClient = sClient.getInputStream();
            final OutputStream outToClient = sClient.getOutputStream();
            Socket client = null, server = null;
            try {
                server = new Socket(SERVER_URL, SERVER_PORT);// connects a socket to the server
            } catch (IOException e) {
                e.printStackTrace();
                PrintWriter out = new PrintWriter(new OutputStreamWriter(outToClient));
                out.flush();
            }
            Log.i(TAG, "connected");
            // a new thread to manage streams from server to client (DOWNLOAD)
            final InputStream inFromServer = server.getInputStream();
            final OutputStream outToServer = server.getOutputStream();
            // a new thread for uploading to the server
            new Thread() {
                public void run() {
                    int bytes_read;
                    try {
                        while ((bytes_read = inFromClient.read(request)) != -1) {
                            outToServer.write(request, 0, bytes_read);
                            outToServer.flush();
                            Log.i(TAG, "outToServer::flush:: " + bytes_read + "bytes");
                            //TODO: LOGIC HERE
                        }
                    } catch (IOException e) {
                    }
                    try {
                        outToServer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            // current thread manages streams from server to client (DOWNLOAD)
            int bytes_read;
            try {
                while ((bytes_read = inFromServer.read(reply)) != -1) {
                    outToClient.write(reply, 0, bytes_read);
                    Log.i(TAG, "outToClient::flush:: " + bytes_read + "bytes");
                    outToClient.flush();
                    //TODO CREATE YOUR LOGIC HERE
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Log.i(TAG, "Ending Connection");
                try {
                    if (server != null)
                        server.close();
                    if (client != null)
                        client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            outToClient.close();
            sClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
