package org.yamcs.yarch;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.List;

import org.junit.Test;

public class EnumTest extends YarchTestCase {
    int n=10;

    private void populate(String tblname) throws Exception {
        ydb.execute("create table "+tblname+"(gentime timestamp, packetName enum, packet binary, primary key(gentime,packetName))");
        ydb.execute("create stream "+tblname+"_in(gentime timestamp, packetName enum, packet binary)");
        ydb.execute("insert into "+tblname+" select * from "+tblname+"_in");

        Stream s = ydb.getStream(tblname+"_in");
        TupleDefinition td=new TupleDefinition();
        td.addColumn(new ColumnDefinition("gentime", DataType.TIMESTAMP));
        td.addColumn(new ColumnDefinition("packetName", DataType.ENUM));
        td.addColumn(new ColumnDefinition("packet", DataType.BINARY));

        for (int i=0;i<n;i++) {
            ByteBuffer bb=ByteBuffer.allocate(2000);
            while(bb.remaining()>0) bb.putInt(i);
            Tuple t=new Tuple(td, new Object[]{
                    1000L*i, "pn"+(i%10), bb.array()
            });
            s.emitTuple(t);
        }
        execute("close stream "+tblname+"_in");

    }
    
    @Test
    public void test1() throws Exception {
        populate("testenum");
        ydb.execute("create stream testenum_out as select * from testenum");
        final List<Tuple> tuples= suckAll("testenum_out");
    
        for(int i=0;i<n;i++) {
            Tuple t=tuples.get(i);
            assertEquals(i*1000l, (long)(Long)t.getColumn(0));
            assertEquals("pn"+(i%10), (String)t.getColumn(1));
        }
    }
    
    
    @Test
    public void test2() throws Exception {
        populate("testenum2");
        ydb.execute("create stream testenum2_out as select * from testenum2 where packetName in ('pn1', 'invalid')");
        final List<Tuple> tuples= suckAll("testenum2_out");
        int i = 1;
        for(Tuple t:tuples) {
            assertEquals(i*1000l, (long)(Long)t.getColumn(0));
            assertEquals("pn"+(i%10), (String)t.getColumn(1));
            i+=10;
        }
    }
    
    @Test
    public void test3() throws Exception {
        populate("testenum3");
        ydb.execute("create stream testenum3_out as select * from testenum3 where packetName in ('invalid')");
        final List<Tuple> tuples= suckAll("testenum3_out");
        int i = 1;
        for(Tuple t:tuples) {
            assertEquals(i*1000l, (long)(Long)t.getColumn(0));
            assertEquals("pn"+(i%10), (String)t.getColumn(1));
            i+=10;
        }
    }
}
