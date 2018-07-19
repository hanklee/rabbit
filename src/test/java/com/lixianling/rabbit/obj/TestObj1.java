/**
 * Create time: 2018-07-19
 */
package com.lixianling.rabbit.obj;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;

/**
 * @author Xianling Li
 */
public class TestObj1 extends TestCase {

    public void test_cast() {
        String s
                = "{\"period\":{\"label\":\"最近30天\",\"value\":\"30d\"},\"data\":{\"gmv\":{\"min\":-2},\"id\":3712312925}}";
        Param param;
        param = JSON.parseObject(s, Param.class); // 从字符串直接转化，OK
        assertNotNull(param);

        JSONObject jobj = JSON.parseObject(s);
        System.out.println(jobj.toString());
        System.out.println(jobj.toJSONString());
        param = jobj.toJavaObject(Param.class);
    }

    public static class Param extends BaseObject {

        private static final long serialVersionUID = 5180807854744861824L;

        public TradeParam<Long, Double, Long, Long> data;

        public Pair<String> period;
    }

    public static class TradeParam<ID, G, O, C> extends BaseObject {

        private static final long serialVersionUID = 3201881995156974305L;

        public ID id;

        public Range<G> gmv;

        public Range<O> ordCnt;

        public Range<C> cspu;

    }

    public static class Range<T> extends BaseObject {

        private static final long serialVersionUID = 669395861117027110L;

        public T min;
        public T max;

    }

    public static class BaseObject {}

    public static class Pair<T> extends BaseObject {

        private static final long serialVersionUID = 2840564531670241284L;

        public String label;
        public T value;

    }
}
