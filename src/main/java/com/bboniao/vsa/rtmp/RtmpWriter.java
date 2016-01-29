
package com.bboniao.vsa.rtmp;

public interface RtmpWriter {

    void write(RtmpMessage message);

    void close();

}
