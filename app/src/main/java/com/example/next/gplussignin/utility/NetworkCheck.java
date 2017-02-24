package com.example.next.gplussignin.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by next on 23/2/17.
 */
public class NetworkCheck {
    Context mContext;

    public NetworkCheck(Context context) {
        this.mContext = context;
    }

    //check the internet connection
    public boolean checkInternetConn() {
        ConnectivityManager connection = (ConnectivityManager)mContext .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connection.getActiveNetworkInfo();
        if (info !=null && info.isConnected()) {
            return  true;
        }
        else {

            Toast.makeText(mContext, "check the internet connection", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
