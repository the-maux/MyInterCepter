package fr.allycs.app.View.Widget;


import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class                    MyWebViewClient extends WebViewClient {
        private String          TAG  = "MyWebViewClient";
        private Toolbar         mToolbar = null;

        public                  MyWebViewClient(Toolbar toolbar) {
            this.mToolbar = toolbar;
        }

        public boolean          shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "shouldOverrideUrlLoading(" + url + ")");
/*            if (Uri.parse(url).getHost().equals("www.example.com")) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }*/
            return true;
        }

        public void             onPageFinished(WebView view, String url) {
            if (mToolbar != null)
                mToolbar.setTitle(view.getTitle());
     }
}
