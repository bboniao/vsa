
package com.bboniao.vsa.rtmp.message;

import com.bboniao.vsa.rtmp.RtmpHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

public abstract class DataMessage extends AbstractMessage {

    private boolean encoded;
    protected ByteBuf data;

    public DataMessage() {
        super();
    }

    public DataMessage(final byte[] ... bytes) {
        data = Unpooled.wrappedBuffer(bytes);
        header.setSize(data.readableBytes());
    }

    public DataMessage(final RtmpHeader header, final ByteBuf in) {
        super(header, in);
    }

    public DataMessage(final int time, final ByteBuf in) {
        header.setTime(time);
        header.setSize(in.readableBytes());
        data = in;
    }


    public ByteBuf encode() {
        if(encoded) {
            // in case used multiple times e.g. broadcast
            data.resetReaderIndex();            
        } else {
            encoded = true;
        }
        return data;
    }


    public void decode(ByteBuf in) {
        data = in;
    }

    @Override
    public String toString() {
        return super.toString() + ByteBufUtil.hexDump(data);
    }

    public abstract boolean isConfig(); // TODO abstraction for audio / video ?

}
