package com.liuqh.lucene;

import org.junit.Before;
import org.junit.Test;

public class LuceneTest {
	HelloLucene hl=null;
	
	@Before
	public void setup() {
		 hl=new HelloLucene();
	}
	
	@Test
	public void testCreateIndex(){
		hl.createIndex();
	}
	
	@Test
	public void testDelAndCreateIndex(){
		hl.delAndCreateIndex();
	}
	
	
	
	@Test
	public void testDel(){
		hl.del("aacc.txt");
	}
	
	
	@Test
	public void testForceMerge(){
		hl.forceMerge();
	}
	
	@Test
	public void testForceMergeDeletes(){
		hl.forceMergeDeletes();
	}
	
	@Test
	public void testSearch03() throws InterruptedException{
		for(int i=0;i<5;i++){
			hl.search("abc");
			Thread.sleep(10000);
		}
	}
	
	@Test
	public void testSearch(){
		hl.search("abc");
	}
	
	@Test
	public void testSearch02(){
		hl.search02("abc");
	}
	
	@Test
	public void testUnDel(){
		hl.unDel();
	}
	
	@Test
	public void testSearchByRange(){
		hl.searchByRange("content", "中国", "法", true, true);
	}
}
