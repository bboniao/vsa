
package com.bboniao.vsa.io.f4v.box;

import com.bboniao.vsa.io.f4v.Payload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class STSS implements Payload {

    private static final Logger logger = LoggerFactory.getLogger(STSS.class);
    private List<Integer> sampleNumbers;
    
    public STSS(ByteBuf in) {
        read(in);
    }

    public List<Integer> getSampleNumbers() {
        return sampleNumbers;
    }

    public void setSampleNumbers(List<Integer> sampleNumbers) {
        this.sampleNumbers = sampleNumbers;
    }

    public void read(ByteBuf in) {
        in.readInt(); // UI8 version + UI24 flags
        final int count = in.readInt();
        logger.debug("no of sample sync records: {}", count);
        sampleNumbers = new ArrayList<Integer>(count);
        for (int i = 0; i < count; i++) {
            final Integer sampleNumber = in.readInt();
            // logger.debug("#{} sampleNumber: {}", new Object[]{i, sampleNumber});
            sampleNumbers.add(sampleNumber);
        }
    }

    public ByteBuf write() {
        ByteBuf out = Unpooled.buffer();
        out.writeInt(0); // UI8 version + UI24 flags
        out.writeInt(sampleNumbers.size());
        for (Integer sampleNumber : sampleNumbers) {
            out.writeInt(sampleNumber);
        }
        return out;
    }
    
}
