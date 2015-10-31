package com.litewaveinc.litewave.util;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;

import com.litewaveinc.litewave.R;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.UUID;

import cz.msebera.android.httpclient.conn.util.InetAddressUtils;

/**
 * Created by jonathan on 10/31/15.
 */
public class DeviceHelper {

    public static String getLocalIpAddress() {
        String ipv4 = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    System.out.println("ip1--:" + inetAddress);
                    System.out.println("ip2--:" + inetAddress.getHostAddress());

                    // for getting IPV4 format
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = inetAddress.getHostAddress())) {

                        String ip = inetAddress.getHostAddress().toString();
                        System.out.println("ip---::" + ip);
                        // return inetAddress.getHostAddress().toString();
                        return ipv4;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return null;
    }

    public static String getDeviceUUID()
    {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String getDeviceID(Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
