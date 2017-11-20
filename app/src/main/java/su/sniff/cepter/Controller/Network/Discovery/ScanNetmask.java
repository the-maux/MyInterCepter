package su.sniff.cepter.Controller.Network.Discovery;

import android.util.Log;

import su.sniff.cepter.Controller.Core.Conf.Singleton;
import su.sniff.cepter.Controller.Network.IPv4CIDR;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Scan Netmask check the rechability of devices in file hostfile
 */
public class                        ScanNetmask {
    private String                  TAG = "ScanNetmask";
    private ExecutorService         service;
    private Integer                 NumberOfHosts;
    private volatile int            nbrHostScanned = 0;
    private volatile boolean        alreadySend = false;
    private ArrayList<String>       ipReachable = new ArrayList<>();
    private boolean                 debuglog = Singleton.getInstance().DebugMode;
    private HostDiscoveryScan mScanner;

    public                          ScanNetmask(IPv4CIDR iPv4CIDR, HostDiscoveryScan scanner) {
        service = Executors.newCachedThreadPool();
        reachableLoop(iPv4CIDR, service);
        this.mScanner = scanner;
    }


    private void                    reachableLoop(IPv4CIDR iPv4CIDR, ExecutorService service) {
        NumberOfHosts = iPv4CIDR.getNumberOfHosts() - 2;
        List<String> availableIPs = iPv4CIDR.getAvailableIPs(NumberOfHosts);
        Log.i(TAG, "NumberOfHosts:" + NumberOfHosts + " ipAvailable:" + availableIPs.size());
        for (final String ip : availableIPs) {
            nbrHostScanned = nbrHostScanned + 1;
            runnableReachable(service, ip, nbrHostScanned);
        }
    }

    private void                    ScanOver() {
        alreadySend = true;
        mScanner.onReachableScanOver(ipReachable);
    }
    public boolean                  ping(String host) {
        try {
//            RootProcess pingProces = new RootProcess("ICMP SCAN")
            Process  mIpAddrProcess = Runtime.getRuntime().exec(Singleton.getInstance().BinaryPath + "/busybox ping -c 1 " + host);
            if( mIpAddrProcess.waitFor() == 0){
                Log.d(TAG, "ping " + host + " TRUE");
                mIpAddrProcess.destroy();
                return true;
            } else {
                mIpAddrProcess.destroy();
                return false;
            }
        }
        catch (InterruptedException ignore)        {
            Log.w("MapNetwork", host + " not reachable interupted ");
            ignore.printStackTrace();
        }
        catch (IOException e)        {
            Log.w("MapNetwork", host + " not reachable IO ");
            e.printStackTrace();
        }
        return false;
    }

    private void                    runnableReachable(ExecutorService service, final String ip, final int nbrHostScanned) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    InetAddress host = InetAddress.getByName(ip);
                    ping(ip);
                    if (InetAddress.getByName(ip).isReachable(1000)) {//Timeout 10s
                        if (debuglog)
                            Log.d(TAG, ip + " is reachable (" + nbrHostScanned + "/" + NumberOfHosts + ")");
                        ipReachable.add(ip + ":");
                        //NetworkInterface ni = NetworkInterface.getByInetAddress(host);// send always null
                        /*if (ni != null) {
                            byte[] mac = ni.getHardwareAddress();
                            String MAC = "";
                            if (mac != null) {
                                for (int i = 0; i < mac.length; i++) {
                                    MAC += String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : "") + "";
                                }
                                Log.d(TAG, ip + ":" + MAC.replace("-", ":").toLowerCase());
                                ipReachable.add(ip + ":" + MAC.replace("-", ":").toLowerCase());
                            } else {
                                Log.e(TAG, ip + " doesn't exist or is not accessible. (" + nbrHostScanned + "/" + NumberOfHosts + ")");
                            }
                        } else {
                            if (debuglog)
                                Log.e(TAG, "PersistanteConfiguration Interface for " + ip + " is null (" + nbrHostScanned + "/" + NumberOfHosts + ")");
                        }*/
                    }
                }  catch (UnknownHostException e) {
                    Log.e(TAG, "UnknownHostException: " + ip + " (" + nbrHostScanned + "/" + NumberOfHosts + ")");
                    e.printStackTrace();
                } catch (SocketException e) {
                    Log.e(TAG, "SocketException: " + ip + " (" + nbrHostScanned + "/" + NumberOfHosts + ")");
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "IOException: " + ip + " (" + nbrHostScanned + "/" + NumberOfHosts + ")");
                } finally {
                    if (nbrHostScanned >= (NumberOfHosts-1) && nbrHostScanned < NumberOfHosts && !alreadySend) {//TODO: -1 you sure ?
                        Log.e(TAG, "ScanOver: " + ip + " (" + nbrHostScanned + "/" + NumberOfHosts + ") with " + ipReachable.size() + " host reached");
                        ScanOver();
                    }
                }
            }
        }).start();
    }


}
