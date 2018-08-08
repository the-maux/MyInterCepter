package fr.dao.app.Core.Scan;

import fr.dao.app.Model.Target.Host;

public class        HostCleverScan {
    Host            mHost;
    private boolean mIsRunning = false;

    public          HostCleverScan(Host host) {
        this.mHost = host;
        new Thread(new Runnable() {
            public void run() {
                mIsRunning = true;
                scan();
            }
        });
    }

    private void    scan() {
        if (mHost.getPorts().isPortOpen(1900)) {
            /* If upnp doesnt work, try to go to know url with typic port*/

        }
    }
}
