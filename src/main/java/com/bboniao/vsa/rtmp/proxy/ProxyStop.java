

package com.bboniao.vsa.rtmp.proxy;

import com.bboniao.vsa.rtmp.RtmpConfig;
import com.bboniao.vsa.util.Utils;

public class ProxyStop {

    public static void main(String[] args) {
        Utils.sendStopSignal(RtmpConfig.configureProxyStop());
    }

}
