package fr.dao.app.Core.Network.Proxy;

import android.util.Log;

import java.io.*;
import java.net.*;

import fr.dao.app.View.Proxy.ProxyActivity;

public class                    HTTPProxy {
    private String              TAG = "HTTPProxy";
    private String              host = "192.168.0.1";
    private ServerSocket        server;
    public boolean              stopOnNext = false, isRunning = false;
    private int                 remoteport = 80, localport = 8082;

    public                      HTTPProxy(ProxyActivity proxyActivity) { }

    public boolean              start() {
        try {
            server = new ServerSocket(localport);
            new Thread(new Runnable() {
                public void run() {
                    Log.i(TAG, "Starting proxy for " + host + ":" + remoteport + " on port " + localport);
                    isRunning = true;
                    while (!stopOnNext) {
                        try {
                            new ThreadProxy(server.accept(), host, remoteport);
                        } catch (IOException e) {
                            isRunning = false;
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean              stop() {
        isRunning = false;
        if (server != null) {
            try {
                stopOnNext = true;
                server.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
