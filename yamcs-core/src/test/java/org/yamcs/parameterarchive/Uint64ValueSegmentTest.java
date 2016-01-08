package org.yamcs.parameterarchive;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;
import org.yamcs.utils.DecodingException;
import org.yamcs.utils.ValueUtility;

public class Uint64ValueSegmentTest {
    @Test
    public void test() throws IOException, DecodingException {
        UInt64ValueSegment fvs = UInt64ValueSegment.consolidate(Arrays.asList(ValueUtility.getUint64Value(1), ValueUtility.getUint64Value(2), ValueUtility.getUint64Value(3)));
        assertEquals(28, fvs.getMaxSerializedSize());
        
        ByteBuffer bb = ByteBuffer.allocate(28);
        fvs.writeTo(bb);
        
        bb.rewind();
        UInt64ValueSegment fvs1 = new UInt64ValueSegment();
        fvs1.parseFrom(bb);
        
        assertEquals(ValueUtility.getUint64Value(1), fvs1.get(0));
        assertEquals(ValueUtility.getUint64Value(2), fvs1.get(1));
        assertEquals(ValueUtility.getUint64Value(3), fvs1.get(2));
        
        assertArrayEquals(new long[]{1, 2,3}, fvs1.getRange(0, 3, true));
        assertArrayEquals(new long[]{3, 2}, fvs1.getRange(0, 2, false));
    }
}
