
package com.bboniao.vsa.io.flv;

import com.bboniao.vsa.io.BufferReader;
import com.bboniao.vsa.rtmp.RtmpHeader;
import com.bboniao.vsa.rtmp.RtmpMessage;
import com.bboniao.vsa.rtmp.message.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlvAtom implements RtmpMessage {

    private static final Logger logger = LoggerFactory.getLogger(FlvAtom.class);

    private final RtmpHeader header;
    private ByteBuf data;

    public static ByteBuf flvHeader() {
        final ByteBuf out = Unpooled.buffer(13);
        out.writeByte((byte) 0x46); // F
        out.writeByte((byte) 0x4C); // L
        out.writeByte((byte) 0x56); // V
        out.writeByte((byte) 0x01); // version
        out.writeByte((byte) 0x05); // flags: audio + video
        out.writeInt(0x09); // header size = 9
        out.writeInt(0); // previous tag size, here = 0
        return out;
    }

    public FlvAtom(final ByteBuf in) {
        header = readHeader(in);
        data = in.readBytes(header.getSize());
        in.skipBytes(4); // prev offset
    }

    public FlvAtom(final BufferReader in) {
        header = readHeader(in.read(11));
        data = in.read(header.getSize());        
        in.position(in.position() + 4); // prev offset
    }

    public FlvAtom(final MessageType messageType, final int time, final ByteBuf in) {
        header = new RtmpHeader(messageType, time, in.readableBytes());
        data = in;
    }

    public ByteBuf write() {
        final ByteBuf out = Unpooled.buffer(15 + header.getSize());
        out.writeByte((byte) header.getMessageType().intValue());
        out.writeMedium(header.getSize());
        out.writeMedium(header.getTime());
        out.writeInt(0); // 4 bytes of zeros (reserved)
        out.writeBytes(data);
        out.writeInt(header.getSize() + 11); // previous tag size
        return out;
    }

    public static RtmpHeader readHeader(final ByteBuf in) {
        final MessageType messageType = MessageType.valueToEnum(in.readByte());
        final int size = in.readMedium();
        final int time = in.readMedium();
        in.skipBytes(4); // 0 - reserved
        return new RtmpHeader(messageType, time, size);
    }

    //============================ RtmpMessage =================================


    public RtmpHeader getHeader() {
        return header;
    }

    public ByteBuf getData() {
        return data;
    }


    public ByteBuf encode() {
        return data;
    }


    public void decode(final ByteBuf in) {
        data = in;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(header);
        sb.append(" data: ").append(data);        
        return sb.toString();
    }

}
