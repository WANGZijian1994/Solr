package com.zijian.controller;

import com.zijian.entity.Football;
import com.zijian.service.SearchService;
import com.zijian.utils.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.util.*;


@Controller
@Slf4j
public class FootballController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 装载SolrClient实例，前提是solr的host地址配对后
     */

    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        return "Bonjour";
    }

    @Autowired
    private SearchService searchService;

    @RequestMapping("/solr/ligue1")
    public ResponseCode search(@RequestBody Map<String,Object> searchMap){
        ResponseCode responseCode = searchService.search(searchMap);
        return searchService.search(searchMap);
    }


    @RequestMapping(value = "/solr/query/{year}/{team}",method = RequestMethod.GET)
    public String Query(ModelMap map, @PathVariable String team,@PathVariable String year) {

        SolrClient solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr/Football").build();

        //System.out.println(solrQuery.toString());
        Map<String, Object> m;
        //执行查询
        try{
            SolrQuery solrQuery = new SolrQuery();

            solrQuery.set("q","Domicile:"+team);

            solrQuery.set("fq", "Saison:"+year);

            solrQuery.setStart(0);
            solrQuery.setRows(50);

            //开启高亮
            solrQuery.setHighlight(true);

            //solrQuery.setHighlightRequireFieldMatch(true);

            //添加高亮字段，多个字段之间逗号隔开比如: A,B,C
            solrQuery.addHighlightField("Domicile");

            //设置高亮字段的前缀
            solrQuery.setHighlightSimplePre("<font color='blue'>");

            //设置高亮字段的后缀
            solrQuery.setHighlightSimplePost("</font>");

            QueryResponse response = solrClient.query(solrQuery);

        //文档结果集
        SolrDocumentList docs = response.getResults();
        System.out.println(docs.getNumFound());

        logger.info("-------------------高亮效果部分展示-------------------------");

        //高亮高亮显示的返回结果
        Map<String, Map<String, List<String>>> maplist = response.getHighlighting();
        System.out.println(response.getHighlighting());
        /**
         * 静态html资源里面的对象 -- ${list}
         */

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        int combien_De_Buts = 0;
        int concession = 0;
        // 返回高亮之后的结果..
        for (SolrDocument solrDocument : docs) {
            //String domicile = solrDocument.getFirstValue("Domicile").toString();
            String notes = solrDocument.getFirstValue("Note_Final").toString();
            String visiteur = solrDocument.getFieldValue("Visiteur").toString();
            visiteur = visiteur.replace("[","");
            visiteur = visiteur.replace("]","");
            String date = solrDocument.getFieldValue("Date").toString().substring(1,11);
            String buts =solrDocument.getFieldValue("Buts_Domicile").toString();
            String conceder = solrDocument.getFieldValue(("Buts_Visiteur")).toString();
            buts = buts.replace("[","");
            buts = buts.replace("]","");
            conceder = conceder.replace("[","");
            conceder = conceder.replace("]","");
            combien_De_Buts += Integer.valueOf(buts);
            concession += Integer.valueOf(conceder);
            Map<String,List<String>>fieldMap = maplist.get(solrDocument.get("id"));
            List<String>gras = fieldMap.get("Domicile");
            String item = gras.get(0);
            logger.info(date);
            logger.info(item);
            logger.info(item);
            logger.info(visiteur);

            m = new HashMap<String, Object>();
            m.put("Date",date);
            m.put("Domicile", item);
            m.put("Note_Final", notes);
            m.put("Visiteur", visiteur);
            list.add(m);
        }
        long nombre = docs.getNumFound();
        String resume =  "Pendant la saison "+ year +", " + team+" a joué "+nombre+" matchs";
        String resume1 = team+" a marqué "+combien_De_Buts+" de buts pendant les matchs à domicile.";
        String resume2 = "Et a encaissé "+concession+ " de buts";
        //List<String>cv = new ArrayList<String>();
        //cv.add(resume);cv.add(resume1);cv.add(resume2);
        //logger.info(resume);
        List<Map<String, String>> list1 = new ArrayList<Map<String, String>>();
        Map<String,String>bref = new HashMap<String,String>();
        bref.put("Matchs",resume);
        bref.put("Goals",resume1);
        bref.put("Lost",resume2);
        list1.add(bref);
        System.out.println(list);
        System.out.println(list1);
        map.addAttribute("list",list);
        map.addAttribute("list1",list1);
        //return "view";
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return "view";
    }

    public static void main(String[] args){
        FootballController footballController = new FootballController();
        try{
            String model = footballController.Query(new ModelMap(),"Olympique de Marseille","2018 2019");
            //System.out.println(model);
        }
        catch(Exception e){e.printStackTrace();}
    }


    @RequestMapping(value = "/solr/buts_domicile",method = RequestMethod.GET)
    public String Buts_Domicile_20182019 (ModelMap modelMap) throws Exception{
        SolrClient solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr/Football").build();
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.set("q","Domicile:*");
        solrQuery.set("fq", "Saison:2018_2019");
        solrQuery.setStart(0);
        solrQuery.setRows(500);
        solrQuery.setHighlight(true);

        //添加高亮字段，多个字段之间逗号隔开比如: A,B,C
        solrQuery.addHighlightField("Buts_Domicile");

        //设置高亮字段的前缀
        solrQuery.setHighlightSimplePre("<font color='red'>");

        //设置高亮字段的后缀
        solrQuery.setHighlightSimplePost("</font>");

        QueryResponse response = solrClient.query(solrQuery);


        //文档结果集
        SolrDocumentList docs = response.getResults();
        System.out.println(docs.getNumFound());
        //System.out.println(docs.toString());
        int combien_De_Buts = 0;
        //int concession = 0;
        Map<String,Integer>stats = new HashMap<String,Integer>();
        List<Map<String,Integer>> list = new ArrayList<>();
        for(SolrDocument solrDocument : docs){
            String domicile = solrDocument.getFieldValue("Domicile").toString();
            domicile = domicile.replace("[","");
            domicile = domicile.replace("]","");
            String buts = solrDocument.getFieldValue("Buts_Domicile").toString();
            buts = buts.replace("[","");
            buts = buts.replace("]","");
            combien_De_Buts=Integer.valueOf(buts);
            if(!stats.containsKey(domicile)){
                stats.put(domicile,combien_De_Buts);
            }
            else{
                int tmp = Integer.valueOf(stats.get(domicile));
                stats.put(domicile,tmp+combien_De_Buts);
            }
        }
        List<Map.Entry<String,Integer>>infoIds = new ArrayList<Map.Entry<String,Integer>>(stats.entrySet());
        Collections.sort(infoIds,new Comparator<Map.Entry<String,Integer>>(){
            public int compare(Map.Entry<String,Integer> o1 ,Map.Entry<String,Integer> o2){
                return (o2.getValue() - o1.getValue());
            }
        });
        //System.out.println(infoIds.toString());
        //Map<String,List<Map.Entry<String,Integer>>>
        //list.add(stats);
        modelMap.addAttribute("domicile",infoIds);
        return "Buts_Domicile_20182019";
    }
}
