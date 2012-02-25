package com.senseidb.search.req.mapred;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.senseidb.search.req.SenseiHit;
import com.senseidb.search.req.SenseiRequest;
import com.senseidb.search.req.SenseiResult;
import com.senseidb.search.req.mapred.obsolete.MapReduceBroker;
import com.senseidb.search.req.mapred.obsolete.MapReduceRequest;
import com.senseidb.search.req.mapred.obsolete.SenseiMapReduceResult;
import com.senseidb.svc.api.SenseiService;
import com.senseidb.test.SenseiStarter;
import com.senseidb.test.TestSensei;

public class TestMapReduce extends TestCase {

    private static final Logger logger = Logger.getLogger(TestMapReduce.class);

    private static MapReduceBroker mapReduceBroker;
    private static SenseiService httpRestSenseiService;
    static {
      SenseiStarter.start("test-conf/node1","test-conf/node2");
      mapReduceBroker = SenseiStarter.mapReduceBroker;
      httpRestSenseiService = SenseiStarter.httpRestSenseiService;
    }
    public void test2GroupByColorAndGroupId() throws Exception { 
      String req = "{\"size\":0,\"filter\":{\"terms\":{\"color\":{\"includes\":[],\"excludes\":[\"gold\"],\"operator\":\"or\"}}}" +
          ", \"mapReduce\":{\"function\":\"com.senseidb.search.req.mapred.CountGroupByMapReduce\",\"parameters\":{\"columns\":[\"groupid\", \"color\"]}}}";
      JSONObject reqJson = new JSONObject(req);
      System.out.println(reqJson.toString(1));
      JSONObject res = TestSensei.search(reqJson);
      System.out.println(res.toString(1));
      JSONObject highestResult = res.getJSONObject("mapReduceResult").getJSONArray("groupedCounts").getJSONObject(0);
      assertEquals(8, highestResult.getInt(highestResult.keys().next().toString()));
    }
    public void test1Max() throws Exception {
      SenseiMapReduceResult result = mapReduceBroker.browse(new MapReduceRequest(new MaxTestMapReduce("groupid")));
      assertEquals(14990L, (long)(Long)result.getReduceResult());
      System.out.println("!!!" + result.getTime());
    }
   
   
    public void test4MaxMapReduce() throws Exception {      
      String req = "{\"filter\":{\"term\":{\"color\":\"red\"}}"
          +", \"mapReduce\":{\"function\":\"sensei.max\",\"parameters\":{\"column\":\"groupid\"}}}";
      JSONObject res = TestSensei.search(new JSONObject(req));
      JSONObject mapReduceResult = res.getJSONObject("mapReduceResult");
      assertEquals(14990, mapReduceResult.getLong("max"));
      assertEquals(14994, mapReduceResult.getLong("uid"));
       req = "{\"filter\":{\"term\":{\"color\":\"red\"}}"
          +  ",\"selections\":[{\"terms\":{\"groupid\":{\"excludes\":[14990],\"operator\":\"or\"}}}]"
          + ", \"mapReduce\":{\"function\":\"sensei.max\",\"parameters\":{\"column\":\"groupid\"}}}";
       res = TestSensei.search(new JSONObject(req));
       mapReduceResult = res.getJSONObject("mapReduceResult");
      assertEquals(14980, mapReduceResult.getLong("max"));
      //assertEquals(14989, mapReduceResult.getLong("uid"));
      
    }
    public void test5DistinctCount() throws Exception {      
      String req = "{\"filter\":{\"term\":{\"color\":\"red\"}}"
          +", \"mapReduce\":{\"function\":\"sensei.distinctCount\",\"parameters\":{\"column\":\"groupid\"}}}";
      JSONObject res = TestSensei.search(new JSONObject(req));
      JSONObject mapReduceResult = res.getJSONObject("mapReduceResult");
      assertEquals(964, mapReduceResult.getLong("distinctCount"));
     
       req = "{"
          +" \"mapReduce\":{\"function\":\"sensei.distinctCountHashSet\",\"parameters\":{\"column\":\"groupid\"}}}";
       res = TestSensei.search(new JSONObject(req));
       mapReduceResult = res.getJSONObject("mapReduceResult");
      assertEquals(1509, mapReduceResult.getLong("distinctCount"));      
    }
    public void test6MinMapReduce() throws Exception {      
      String req = "{\"filter\":{\"term\":{\"tags\":\"reliable\"}}"
          +", \"mapReduce\":{\"function\":\"sensei.min\",\"parameters\":{\"column\":\"groupid\"}}}";
      JSONObject res = TestSensei.search(new JSONObject(req));
      JSONObject mapReduceResult = res.getJSONObject("mapReduceResult");
      assertEquals(-15000L, mapReduceResult.getLong("min"));
      assertEquals(0L, mapReduceResult.getLong("uid"));      
       req = "{\"filter\":{\"term\":{\"tags\":\"reliable\"}}"
          +", \"mapReduce\":{\"function\":\"sensei.min\",\"parameters\":{\"column\":\"year\"}}}";
       res = TestSensei.search(new JSONObject(req));
       mapReduceResult = res.getJSONObject("mapReduceResult");
      assertEquals(1993L, mapReduceResult.getLong("min"));          
    }
    public void test7SumMapReduce() throws Exception {      
      String req = "{\"filter\":{\"term\":{\"color\":\"red\"}}, "
          +" \"mapReduce\":{\"function\":\"sensei.sum\",\"parameters\":{\"column\":\"groupid\"}}}";
      JSONObject res = TestSensei.search(new JSONObject(req));
      JSONObject mapReduceResult = res.getJSONObject("mapReduceResult");
      assertEquals(16036500, mapReduceResult.getLong("sum"));      
    }
    public void ntest8AvgMapReduce() throws Exception {      
      String req = "{\"filter\":{\"term\":{\"color\":\"red\"}}, "
          +" \"mapReduce\":{\"function\":\"sensei.avg\",\"parameters\":{\"column\":\"groupid\"}}}";
      JSONObject res = TestSensei.search(new JSONObject(req));
      JSONObject mapReduceResult = res.getJSONObject("mapReduceResult");
      assertEquals(7424, mapReduceResult.getLong("avg"));
      assertEquals(2160, mapReduceResult.getLong("count")); 
    }
    
      
}
