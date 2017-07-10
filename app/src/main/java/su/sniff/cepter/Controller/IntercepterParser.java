package su.sniff.cepter.Controller;

import android.app.Activity;
import su.sniff.cepter.globalVariable;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by the-maux on 10/07/17.
 */
public class                            IntercepterParser {
    public static void                  parseCookie(BufferedReader reader, Activity activity) throws IOException {
        final String domain = reader.readLine();
        final String ip = reader.readLine();
        final String getreq = reader.readLine();
        final String coo = reader.readLine();
        String z = reader.readLine();
        if (!ip.equals(globalVariable.own_ip)) {
            int dub = 0;
            for (int i = 0; i < globalVariable.cookies_c; i++) {
                if (((String) globalVariable.cookies_value.get(i)).equals(coo)) {
                    dub = 1;
                    break;
                }
            }
            if (dub != 1) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (globalVariable.lock == 0) {
                            globalVariable.lock = 1;
                        } else {
                            while (globalVariable.lock == 1) {
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            globalVariable.lock = 1;
                        }
                        globalVariable.cookies_domain.add(globalVariable.cookies_c, domain + " : " + ip);
                        globalVariable.cookies_domain2.add(globalVariable.cookies_c, "<font color=\"#00aa00\"><b>" + domain + " : " + ip + "</b></font><br>" + "<font color=\"#397E7E\">" + coo + "</font>");
                        globalVariable.adapter.notifyDataSetChanged();
                        globalVariable.adapter2.notifyDataSetChanged();
                        globalVariable.cookies_getreq.add(globalVariable.cookies_c, getreq);
                        globalVariable.cookies_value.add(globalVariable.cookies_c, coo);
                        globalVariable.cookies_ip.add(globalVariable.cookies_c, ip);
                        globalVariable.cookies_getreq2.add(globalVariable.cookies_c, getreq);
                        globalVariable.cookies_value2.add(globalVariable.cookies_c, coo);
                        globalVariable.cookies_ip2.add(globalVariable.cookies_c, ip);
                        globalVariable.cookies_c++;
                        globalVariable.lock = 0;
                    }
                });
            }
        }
    }
}
