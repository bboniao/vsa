
package com.bboniao.vsa.rtmp.message;

import com.bboniao.vsa.amf.Amf0Object;
import com.bboniao.vsa.amf.Amf0Value;
import com.bboniao.vsa.rtmp.RtmpHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.List;

public class CommandAmf0 extends Command {    

    public CommandAmf0(RtmpHeader header, ByteBuf in) {
        super(header, in);        
    }

    public CommandAmf0(int transactionId, String name, Amf0Object object, Object ... args) {
        super(transactionId, name, object, args);
    }

    public CommandAmf0(String name, Amf0Object object, Object ... args) {
        super(name, object, args);
    }

    @Override
    MessageType getMessageType() {
        return MessageType.COMMAND_AMF0;
    }


    public ByteBuf encode() {
        ByteBuf out = Unpooled.buffer();
        Amf0Value.encode(out, name, transactionId, object);
        if(args != null) {
            for(Object o : args) {
                Amf0Value.encode(out, o);
            }
        }
        return out;
    }

    public void decode(ByteBuf in) {
        name = (String) Amf0Value.decode(in);
        transactionId = ((Double) Amf0Value.decode(in)).intValue();
        object = (Amf0Object) Amf0Value.decode(in);
        List<Object> list = new ArrayList<Object>();
        while(in.isReadable()) {
            list.add(Amf0Value.decode(in));
        }
        args = list.toArray();
    }

}
