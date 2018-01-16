/**
 * Create time: 15-Jan-2018
 */
package com.lixianling.rabbit.redis;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAOHandler;
import com.lixianling.rabbit.dao.redis.RedisDAO;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author hank
 */
public class Category extends DBObject {

    public int id;
    public int catId; // 1 movie 2 电视剧 3 综艺 4 动漫 5 小说 6 游戏 7 人物 8 汽车 9 生活 10 科技 11 热点
    public List<String> keywords;

    public static void testList(RedisDAO dao) {
        try {
            System.out.println("OBJECT LIST:");
            List<Category> list = dao.execute(new DAOHandler<List<Category>>() {
                @Override
                public List<Category> handle(Object con) throws DBException {
                    Jedis connection = (Jedis) con;
                    long totalNum = connection.zcount(RedisDAO.getTableIds("category"), "-inf", "+inf");
                    System.out.println("Total num:" + totalNum);
                    int si = 0;
                    int start = si * 20 + 1;
                    if (start == 1) start = 0;
                    int end = start + 20;
                    List<Category> result = new ArrayList<Category>();
                    Set<String> srs = connection.zrange(RedisDAO.getTableIds("category"), start, end);
                    for (String sr : srs) {
                        Category no = new Category();
                        String value = connection.get(sr);
                        if (value != null) {
                            no.JsonToObj(new JSONObject(value));
                            result.add(no);
                        }
                    }
                    return result;
                }
            });
            for (Category ro : list) {
                System.out.println(ro.id + "," + ro.catId);
                System.out.println(ro.keywords);
//                String tmp = "";
//                for (String content : ro.contents) {
////                    System.out.println(content);
//                    tmp = tmp + "\n" + content;
//                }

            }
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        RedisDAO dao = new RedisDAO();
//        Category cat = new Category();
//        cat.catId = 1;
//        cat.keywords = new ArrayList<String>();
//        cat.keywords.add("千术3决战澳门");
//        cat.keywords.add("终结者2");
//        cat.keywords.add("无问西东团购");
//        cat.keywords.add("超级大山炮之海");
//        cat.keywords.add("勇敢者游戏2");
//        cat.keywords.add("国民老公");
//        cat.keywords.add("芳华团购");
//        cat.keywords.add("战狼2");
//        cat.keywords.add("勇敢者的游戏");
//        dao.insert(cat);

        testList(dao);
    }
}
