package com.example.moviezzzz.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
/**
 * Utility Class for checking internet status
 */
public class CheckInternetStatus {

    private Context mContext;
    private boolean is_internet_connected = false;
    private ConnectivityManager mConnectivityManager;

    public CheckInternetStatus(Context mContext){
        this.mContext = mContext;
    }

    //Method for checking internet connectivity
    public boolean isInternetConnected(){
        try{
            mConnectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            is_internet_connected  = mNetworkInfo!=null && mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            return  is_internet_connected;
        }catch (Exception ex){
            Log.e("error",ex.getLocalizedMessage());
        }
        return is_internet_connected;
    }
}