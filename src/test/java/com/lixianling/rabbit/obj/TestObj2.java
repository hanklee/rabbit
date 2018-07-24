/**
 * Create time: 2018-07-19
 */
package com.lixianling.rabbit.obj;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JavaBeanSerializer;
import com.alibaba.fastjson.serializer.SerializeBeanInfo;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import com.lixianling.rabbit.DBObject;
import junit.framework.TestCase;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

/**
 * @author Xianling Li
 */
public class TestObj2 extends TestCase {

    public void test_cast() {
        MyObject mObj = new MyObject();
        String jsonData = "{\"id2\":100,\"id1\":10,\"name\":\"test\"}";
        mObj.JsonToObj(JSON.parseObject(jsonData));
//        mObj.id1 = 10;
//        mObj.id2 = 100;
//        mObj.name = "test";
        System.out.println(mObj.toString());
        System.out.println(mObj.toJson());

        MyObject2 mObj2 = new MyObject2();
        mObj2.JsonToObj(JSON.parseObject(jsonData));
        System.out.println(mObj2.toString());
        System.out.println(mObj2.toJson());

        JavaBeanSerializer serializer = new JavaBeanSerializer(MyObject2.class);
        List<FieldInfo> fieldInfoList = TypeUtils.computeGetters(MyObject2.class, null);
        printFieldInfos(fieldInfoList);

        System.out.println("---------------------");
        // 获取对象里 是getter或者是public 的field
        fieldInfoList = TypeUtils.computeGetters(MyObject.class, null);
        printFieldInfos(fieldInfoList);
//        serializer.
    }

    public static void printFieldInfos(List<FieldInfo> fieldInfoList) {
        for (FieldInfo info : fieldInfoList) {
            if (info.field != null) {
                if (Modifier.isFinal(info.field.getModifiers())
                        || Modifier.isStatic(info.field.getModifiers())
                        || info.field.getType().isArray()) {
                    continue;
                }
                if ((Modifier.isPublic(info.field.getModifiers()))) {
                    System.out.println(info.name + "," + info.fieldAccess + "," + info.field);
                } else if (Modifier.isPublic(info.method.getModifiers()) && !info.name.equals("allFields")){
//                    System.out.println(info.name + "," + info.method + "," + info.field);
                    System.out.println(info.method + "," + info.field +","+info.name);
                }
            }
        }
    }

    public static class MyObject extends DBObject {
        public String table_name = "test1";
        public long id1;
        public int id2;
        public String name;
    }

    public static class MyObject2 extends DBObject {
        public String table_name = "test2";
        private long id1;
        private int id2;
        private String name;

        public long getId1() {
            return id1;
        }

        public void setId1(long id1) {
            this.id1 = id1;
        }

        public int getId2() {
            return id2;
        }

        public void setId2(int id2) {
            this.id2 = id2;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
