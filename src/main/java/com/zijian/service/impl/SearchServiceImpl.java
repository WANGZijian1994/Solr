package com.zijian.service.impl;

import com.zijian.entity.Football;
import com.zijian.service.SearchService;
import com.zijian.utils.ResponseCode;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Service
@SuppressWarnings("all")
public class SearchServiceImpl implements SearchService{
    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * SolrClient实例，前提是solr的host地址配对后。
     */
    @Autowired
    private SolrClient solrClient;

    @Override
    public ResponseCode search(Map<String,Object> searchMap){
        Integer current = (Integer) searchMap.get("current");
        Integer rows = (Integer) searchMap.get("rows");
        String keyword = ((String) searchMap.get("keyword")).replace(" ","");
        return solrTemplateSearch(keyword,current,rows);
    }


    //注意这个方法是private的
    private ResponseCode solrTemplateSearch(String keyword,Integer current,Integer rows){
        //高亮设置
        HighlightQuery query = new SimpleHighlightQuery();
        String[] fieldNames = {"Domicile","Visiteur","Date"};
        HighlightOptions highlightOptions = new HighlightOptions().addField(fieldNames);//设置高亮域
        highlightOptions.setSimplePrefix("<em style = 'color : red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);//设置高亮选项

        if("".equals(keyword)){
            return new ResponseCode("请输入查询内容：");
        }

        try{
            /*
            通过Criteria构建查询过滤条件
             */

            //这里的keyword等价于solr core中的schema.xml配置的域，且keyword是复制域名
            Criteria criteria = new Criteria("keyword");
            //按照关键字查询
            if (keyword != null && !("".equals(keyword))){
                criteria.contains(keyword);
            }
            query.addCriteria(criteria);

            //默认第一页
            if(current == null){
                current = 1;
            }
            if(rows == null){
                rows = 20;//默认每次查询20条记录
            }
            query.setOffset((long) ((current - 1)*rows));//从第几条记录开始查询：= 当前页 * 每页的记录数
            query.setRows(rows);

            //通过solrTemplate接口接入数据
            HighlightPage<Football> page = solrTemplate.queryForHighlightPage("",query,Football.class);
            //循环高亮入口集合
            for(HighlightEntry<Football> f: page.getHighlighted()){//List<HighlightEntry<Football>>
                Football football = f.getEntity();//获取Entity
                if (f.getHighlights().size() > 0){
                    f.getHighlights().forEach(light -> {
                        if (football.getDomicile().contains(keyword)){
                            football.setDomicile(light.getSnipplets().get(0));//
                        }
                        if(football.getVisiteur().contains(keyword)){
                            football.setVisiteur(light.getSnipplets().get(0));
                        }
                        if(football.getDate().contains(keyword)){
                            football.setDate(light.getSnipplets().get(0));
                        }
                    });
                }
            }
            return new ResponseCode(page.getContent(),page.getTotalElements());
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseCode("Rien trouvé ! ");
        }
    }
}
