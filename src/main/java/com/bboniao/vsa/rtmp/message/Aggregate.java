
package com.bboniao.vsa.rtmp.message;


import com.bboniao.vsa.rtmp.RtmpHeader;
import io.netty.buffer.ByteBuf;

public class Aggregate extends DataMessage {

    public Aggregate(RtmpHeader header, ByteBuf in) {
        super(header, in);
    }

    public Aggregate(int time, ByteBuf in) {
        super();
        header.setTime(time);
        data = in;
        header.setSize(data.readableBytes());
    }

    @Override
    MessageType getMessageType() {
        return MessageType.AGGREGATE;
    }

    @Override
    public boolean isConfig() {
        return false;
    }

}
