
package com.bboniao.vsa.rtmp.message;

import com.bboniao.vsa.rtmp.RtmpHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ChunkSize extends AbstractMessage {

    private int chunkSize;

    public ChunkSize(RtmpHeader header, ByteBuf in) {
        super(header, in);
    }

    public ChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override
    MessageType getMessageType() {
        return MessageType.CHUNK_SIZE;
    }

    public int getChunkSize() {
        return chunkSize;
    }


    public ByteBuf encode() {
        ByteBuf out = Unpooled.buffer(4);
        out.writeInt(chunkSize);
        return out;
    }


    public void decode(ByteBuf in) {
        chunkSize = in.readInt();
    }

    @Override
    public String toString() {
        return super.toString() + chunkSize;
    }

}
