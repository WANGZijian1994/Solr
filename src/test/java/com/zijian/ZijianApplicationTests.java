package com.zijian;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class ZijianApplicationTests {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	//1.
	@Autowired
	private SolrClient solrClient;

	@Test
	public void search() throws IOException, SolrServerException {
		/*
		//2.
		SolrQuery query = new SolrQuery();

		query.set("q","Domicile:Olympique_de_Marseille");

		try{
			QueryResponse response = solrClient.query(query);
			SolrDocumentList documents = response.getResults();
			long combien = documents.getNumFound();
			System.out.println(documents.getNumFound());
			/*
			for(int i = 0;i<combien;i++){
				logger.info(String.valueOf(documents.get(i).getFieldValues("Jour")));
			}

		}
		catch(Exception e){
			e.printStackTrace();
		}

		 */
		SolrQuery solrQuery = new SolrQuery();

		solrQuery.set("q","Domicile:Olympique_de_Marseille");

		solrQuery.set("fq", "Saison:2018_2019");

		//开启高亮
		solrQuery.setHighlight(true);

		//添加高亮字段，多个字段之间逗号隔开比如: A,B,C
		solrQuery.addHighlightField("Domicile,Note_Final,Visiteur");

		//设置高亮字段的前缀
		solrQuery.setHighlightSimplePre("<font color='blue'>");

		//设置高亮字段的后缀
		solrQuery.setHighlightSimplePost("</font>");

		QueryResponse response = solrClient.query("Football",solrQuery);
	}
}
