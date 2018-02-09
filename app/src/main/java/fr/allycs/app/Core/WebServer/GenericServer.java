package fr.allycs.app.Core.WebServer;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class                    GenericServer extends NanoHTTPD {
    private String              TAG = "GenericServer";
    public enum whichPageSended { Default }
    private whichPageSended     conf = whichPageSended.Default;

    public                      GenericServer(int port) {
        super(port);
    }

    public                      GenericServer(String hostname, int port) {
        super(hostname, port);
    }

    public Response             serve(IHTTPSession session) {
        switch (conf) {
            case Default:
                return serveDefaultPage(session);
            default:
                return serveDefaultPage(session);
        }
    }

    private Response            serveDefaultPage(IHTTPSession session) {
        StringBuilder html = new StringBuilder("<html><body><h1>Man In The Middle HTTP REDIR</h1>\n");
        Map<String, String> sessionParams = session.getParms();
        if (sessionParams.get("username") == null) {
            html.append("<form action='?' method='get'>").append("\n");
            html.append("<p>Your name: <input type='text' name='username'></p>").append("\n");
            html.append("</form>").append("\n");
        } else {
            html.append("<p>Hello, ");
            html.append(sessionParams.get("username"));
            html.append("!</p>");
        }
        html.append("</body></html>\n");
        return newFixedLengthResponse(html.toString());
    }


}
