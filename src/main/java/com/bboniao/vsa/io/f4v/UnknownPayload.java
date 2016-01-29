
package com.bboniao.vsa.io.f4v;


import io.netty.buffer.ByteBuf;

public class UnknownPayload implements Payload {

    private BoxType type;
    private ByteBuf data;

    public UnknownPayload(ByteBuf in, BoxType type) {
        this.data = in;
        this.type = type;
    }

    public void read(ByteBuf in) {
        data = in;
    }

    public ByteBuf write() {
        return data;
    }

    @Override
    public String toString() {
        return "[" + type + " (unknown) " + data + "]";
    }

}
