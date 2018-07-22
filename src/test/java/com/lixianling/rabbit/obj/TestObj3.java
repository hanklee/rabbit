/**
 * Create time: 21-Jul-2018
 */
package com.lixianling.rabbit.obj;

import com.lixianling.rabbit.DBObject;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 *
 * @author hank
 */
public class TestObj3 extends TestCase{

    public void test_v1(){
        String jsonData = "{\"id2\":100,\"id1\":10,\"name\":\"test\"}";
        DataObject3 d1 = new DataObject3();

        DataObject3 d2 = (DataObject3) d1.clone();
        Assert.assertEquals(d2.getId1(),0);

        DataObject3 d3 = d1.cloneObj(jsonData);
        Assert.assertEquals(d3.getId1(),10);
    }



    public static class DataObject3 extends DBObject{
        private int id1;
        private int id2;
        private String id3;

        public int getId1() {
            return id1;
        }

        public int getId2() {
            return id2;
        }

        public String getId3() {
            return id3;
        }
    }
}
