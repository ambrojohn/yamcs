package org.yamcs.parameterarchive;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.yamcs.protobuf.Yamcs.Value;
import org.yamcs.utils.DecodingException;
import org.yamcs.utils.ValueUtility;

public class FloatValueSegmentTest {
	@Test
	public void test() throws IOException, DecodingException {
		FloatValueSegment fvs = FloatValueSegment.consolidate(Arrays.asList(ValueUtility.getFloatValue(1.2f), ValueUtility.getFloatValue(2.3f), ValueUtility.getFloatValue((float) 3)));
		assertEquals(18, fvs.getMaxSerializedSize());

		ByteBuffer bb = ByteBuffer.allocate(24);
		fvs.writeTo(bb);

		bb.rewind();
		FloatValueSegment fvs1 = new FloatValueSegment();
		fvs1.parseFrom(bb);

		assertEquals(ValueUtility.getFloatValue(1.2f), fvs1.get(0));
		assertEquals(ValueUtility.getFloatValue(2.3f), fvs1.get(1));
		assertEquals(ValueUtility.getFloatValue(3f), fvs1.get(2));

		assertArrayEquals(new float[]{1.2f, 2.3f,3}, fvs1.getRange(0, 3, true), 1e-10f);
		assertArrayEquals(new float[]{3, 2.3f}, fvs1.getRange(0, 2, false), 1e-10f);
	}


	@Test
	public void test2() throws IOException, DecodingException {
		List<Value> values = new ArrayList<>(200);
		for(int i=0;i<200;i++) {
			values.add(ValueUtility.getFloatValue(1.2f));
		}
		FloatValueSegment fvs = FloatValueSegment.consolidate(values);

		ByteBuffer bb = ByteBuffer.allocate(fvs.getMaxSerializedSize());
		fvs.writeTo(bb);
		int length = bb.position();
		System.out.println("length: "+length);
		ByteBuffer bb1 = ByteBuffer.allocate(length);
		bb1.put(bb.array(),0,length);

		FloatValueSegment fvs1 = new FloatValueSegment();
		bb1.rewind();
		fvs1.parseFrom(bb1);
		assertEquals(200, fvs1.size());
	}


	@Test
	public void test3() throws Exception {
		float[] x= FloatCompress.readFile("/tmp/aces_PDU_COPS_TEMP2.replay");

		List<Value> values = new ArrayList<>(x.length);
		for(int i=0; i<x.length; i++) {
			values.add(ValueUtility.getFloatValue(x[i]));
		}

		FloatValueSegment fvs = FloatValueSegment.consolidate(values);

		ByteBuffer bb = ByteBuffer.allocate(fvs.getMaxSerializedSize());
		fvs.writeTo(bb);
		int length = bb.position();
		System.out.println("length: "+length);
		
		ByteBuffer bb1 = ByteBuffer.allocate(length);
		bb1.put(bb.array(), 0, length);

		FloatValueSegment fvs1 = new FloatValueSegment();
		bb1.rewind();
		fvs1.parseFrom(bb1);
		assertArrayEquals(x, fvs1.floats, 1e-10f);
	}

}
