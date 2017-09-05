package su.sniff.cepter.View;

import android.os.Bundle;

import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.R;

/**
 * TODO:
 *      + Read ./files/dnsSpoof.conf
 *      + init in List
 *      + displayt in RV
 *      + add to file
 * Good luck bra
 */
public class                            DNSSpoofingActivity extends MyActivity {
    private String                      TAG = "DNSSpoofingActivity";
    private DNSSpoofingActivity         mInstance = this;

    public void                         onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnsspoofing);
        initXml();
    }

    private void                        initXml() {
    }

}
