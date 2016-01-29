
package com.bboniao.vsa.rtmp;


import io.netty.buffer.ByteBuf;

public interface RtmpMessage {

    RtmpHeader getHeader();

    ByteBuf encode();

    void decode(ByteBuf in);

}
