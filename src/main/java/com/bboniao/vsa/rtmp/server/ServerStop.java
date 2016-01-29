
package com.bboniao.vsa.rtmp.server;


import com.bboniao.vsa.rtmp.RtmpConfig;
import com.bboniao.vsa.util.Utils;

public class ServerStop {

    public static void main(String[] args) {
        Utils.sendStopSignal(RtmpConfig.configureServerStop());
    }

}
