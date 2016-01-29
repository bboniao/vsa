
package com.bboniao.vsa.rtmp.message;

import com.bboniao.vsa.rtmp.RtmpHeader;
import com.bboniao.vsa.util.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Video extends DataMessage {

    @Override
    public boolean isConfig() { // TODO now hard coded for avc1
        return data.readableBytes() > 3 && data.getInt(0) == 0x17000000;
    }

    public Video(final RtmpHeader header, final ByteBuf in) {
        super(header, in);
    }

    public Video(final byte[] ... bytes) {
        super(bytes);
    }

    public Video(final int time, final byte[] prefix, final int compositionOffset, final byte[] videoData) {
        header.setTime(time);
        data = Unpooled.wrappedBuffer(prefix, Utils.toInt24(compositionOffset), videoData);
        header.setSize(data.readableBytes());
    }

    public Video(final int time, final ByteBuf in) {
        super(time, in);
    }

    public static Video empty() {
        Video empty = new Video();
        empty.data = Unpooled.wrappedBuffer(new byte[2]);
        return empty;
    }

    @Override
    MessageType getMessageType() {
        return MessageType.VIDEO;
    }

}
