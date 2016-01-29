
package com.bboniao.vsa.rtmp.message;

import com.bboniao.vsa.amf.Amf0Value;
import com.bboniao.vsa.rtmp.RtmpHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.List;

public class MetadataAmf0 extends Metadata {

    public MetadataAmf0(String name, Object... data) {
        super(name, data);
    }

    public MetadataAmf0(RtmpHeader header, ByteBuf in) {
        super(header, in);
    }

    @Override
    MessageType getMessageType() {
        return MessageType.METADATA_AMF0;
    }


    public ByteBuf encode() {
        ByteBuf out = Unpooled.buffer();
        Amf0Value.encode(out, name);
        Amf0Value.encode(out, data);
        return out;
    }


    public void decode(ByteBuf in) {
        name = (String) Amf0Value.decode(in);
        List<Object> list = new ArrayList<Object>();
        while(in.isReadable()) {
            list.add(Amf0Value.decode(in));
        }
        data = list.toArray();
    }

}
