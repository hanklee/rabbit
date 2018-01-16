/**
 * Create time: 15-Jan-2018
 */
package com.lixianling.rabbit.elastic;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.CRF.CRFSegment;
import com.hankcs.hanlp.seg.Segment;
import com.lixianling.rabbit.DBException;
import com.lixianling.rabbit.DBObject;
import com.lixianling.rabbit.dao.DAOHandler;
import com.lixianling.rabbit.dao.elastic.ElasticDAO;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hank
 */
public class Qiqu extends DBObject {
    public String _id;

    public String title;
    public String des;
    public String bs;
    public List<String> answers;
    public List<Integer> tags;

    private static void testList() throws DBException {
        ElasticDAO dao = new ElasticDAO();
        List<Qiqu> qiqus = dao.execute(new DAOHandler<List<Qiqu>>() {
            @Override
            public List<Qiqu> handle(Object con) throws DBException {
                TransportClient client = (TransportClient) con;
                SearchResponse response = client.prepareSearch("qiqu_index").setTypes("qiqu")
                        .setQuery(QueryBuilders.matchAllQuery()).setFrom(0).setSize(60).get();
                SearchHits shits = response.getHits();
                System.out.println("total num:" + shits.totalHits);
                List<Qiqu> all = new ArrayList<Qiqu>();
                SearchHit[] hits = shits.getHits();
                for (SearchHit hit: hits) {
                    Map<String, Object> source = hit.getSourceAsMap();
                    Qiqu clone = new Qiqu();
                    clone.MapToObj(source);
                    clone._id = hit.getId();
                    all.add(clone);
                }
                return all;
            }
        });
        for (Qiqu qiqu : qiqus) {
            StringBuilder sb = new StringBuilder();
            sb.append(qiqu.des).append(qiqu.bs);
            for (String tmp : qiqu.answers) {
                sb.append(tmp);
            }
            List<String> keywordList = HanLP.extractKeyword(sb.toString(), 5);
            System.out.println(" keyword: " + keywordList);
            Segment segment = new CRFSegment();
            System.out.println(qiqu.title);
            System.out.println(segment.seg(qiqu.title));
        }
    }


    public static void main(String[] args) throws Exception{
        HanLP.Config.ShowTermNature = false;
        testList();
    }
}
