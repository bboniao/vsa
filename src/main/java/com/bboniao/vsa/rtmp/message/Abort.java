package com.bboniao.vsa.rtmp.message;


import com.bboniao.vsa.rtmp.RtmpHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Abort extends AbstractMessage {

    private int streamId;

    public Abort(final int streamId) {
        this.streamId = streamId;
    }

    public Abort(final RtmpHeader header, final ByteBuf in) {
        super(header, in);
    }

    public int getStreamId() {
        return streamId;
    }

    @Override
    MessageType getMessageType() {
        return MessageType.ABORT;
    }

    public ByteBuf encode() {
        final ByteBuf out = Unpooled.buffer(4);
        out.writeInt(streamId);
        return out;
    }

    public void decode(ByteBuf in) {
        streamId = in.readInt();
    }

    @Override
    public String toString() {
        return super.toString() + "streamId: " + streamId;
    }

}
