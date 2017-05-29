package com.liuqh.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class HelloLucene {
	private static DirectoryReader directoryReader = null;
	private static Directory directory = null;

	public HelloLucene() {
		try {
			directory = FSDirectory.open(FileSystems.getDefault().getPath("d:/lucene/index01"));
			directoryReader = DirectoryReader.open(directory);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public DirectoryReader getDirectoryReader() {

		if (directoryReader != null) {
			try {
				// 如果index有变化则返回新的DirectoryReader，否则返回null
				DirectoryReader dr = DirectoryReader.openIfChanged(directoryReader);
				if (dr != null) {
					directoryReader = dr;
				}
				return directoryReader;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}

	}

	public IndexSearcher getIndexSearch() {
		if (directoryReader != null) {
			try {
				DirectoryReader dr = DirectoryReader.openIfChanged(directoryReader);
				if (dr != null) {
					directoryReader = dr;
				}
				return new IndexSearcher(directoryReader);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}

	}

	public void createIndex() {
		IndexWriter indexWriter = null;
		try {
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			indexWriter = new IndexWriter(directory, config);
			File dir = new File("d:/lucenefile");
			for (File f : dir.listFiles()) {
				Document doc = new Document();
				doc.add(new Field("filename", f.getName(), TextField.TYPE_STORED));
				doc.add(new Field("filepath", f.getAbsolutePath(), TextField.TYPE_STORED));
				doc.add(new Field("content", new FileReader(f), TextField.TYPE_NOT_STORED));
				indexWriter.addDocument(doc);
				indexWriter.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				indexWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void delAndCreateIndex() {
		IndexWriter indexWriter = null;
		try {
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			indexWriter = new IndexWriter(directory, config);
			indexWriter.deleteAll();// 清除以前的index
			File dir = new File("d:/lucenefile");
			for (File f : dir.listFiles()) {
				Document doc = new Document();
				doc.add(new Field("filename", f.getName(), TextField.TYPE_STORED));
				doc.add(new Field("filepath", f.getAbsolutePath(), TextField.TYPE_STORED));
				doc.add(new Field("content", new FileReader(f), TextField.TYPE_NOT_STORED));
				indexWriter.addDocument(doc);
				indexWriter.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				indexWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void del(String filename) {
		IndexWriter indexWriter = null;
		try {
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			indexWriter = new IndexWriter(directory, config);
			indexWriter.deleteDocuments(new Term("filename", filename));
			indexWriter.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				indexWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void forceMerge() {
		IndexWriter indexWriter = null;
		try {
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			indexWriter = new IndexWriter(directory, config);
			indexWriter.forceMerge(1, true);
			indexWriter.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				indexWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void forceMergeDeletes() {
		IndexWriter indexWriter = null;
		try {
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			indexWriter = new IndexWriter(directory, config);
			indexWriter.forceMergeDeletes();
			indexWriter.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				indexWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 单例IndexReader
	 * 
	 * @param keyWord
	 */
	public void search(String keyWord) {
		try {
			System.out.println("maxDoc->" + getDirectoryReader().maxDoc());
			System.out.println("numDeletedDocs->" + getDirectoryReader().numDeletedDocs());
			System.out.println("numDocs->" + getDirectoryReader().numDocs());
			// 3、根据IndexReader创建IndexSearch
			IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

			// 4、创建搜索的Query
			Analyzer analyzer = new StandardAnalyzer();
			// 创建parser来确定要搜索文件的内容，第一个参数为搜索的域
			QueryParser queryParser = new QueryParser("content", analyzer);
			// 创建Query表示搜索域为content包含UIMA的文档
			Query query = queryParser.parse(keyWord);

			// 5、根据searcher搜索并且返回TopDocs
			TopDocs topDocs = indexSearcher.search(query, 10);
			System.out.println("查找到的文档总共有：" + topDocs.totalHits);

			// 6、根据TopDocs获取ScoreDoc对象
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (ScoreDoc scoreDoc : scoreDocs) {

				// 7、根据searcher和ScoreDoc对象获取具体的Document对象
				Document document = indexSearcher.doc(scoreDoc.doc);

				// 8、根据Document对象获取需要的值
				System.out.println(document.get("filename") + " " + document.get("filepath"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * try { if (directoryReader != null) { directoryReader.close(); } }
			 * catch (Exception e) { e.printStackTrace(); }
			 */
		}
	}

	public void search02(String keyWord) {
		DirectoryReader directoryReader = null;
		try {
			Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("d:/lucene/index01/"));
			directoryReader = DirectoryReader.open(directory);
			System.out.println("maxDoc->" + getDirectoryReader().maxDoc());
			System.out.println("numDeletedDocs->" + getDirectoryReader().numDeletedDocs());
			System.out.println("numDocs->" + getDirectoryReader().numDocs());
			// 3、根据IndexReader创建IndexSearch
			IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

			// 4、创建搜索的Query
			Analyzer analyzer = new StandardAnalyzer();
			// 创建parser来确定要搜索文件的内容，第一个参数为搜索的域
			QueryParser queryParser = new QueryParser("content", analyzer);
			// 创建Query表示搜索域为content包含UIMA的文档
			Query query = queryParser.parse(keyWord);

			// 5、根据searcher搜索并且返回TopDocs
			TopDocs topDocs = indexSearcher.search(query, 10);
			System.out.println("查找到的文档总共有：" + topDocs.totalHits);

			// 6、根据TopDocs获取ScoreDoc对象
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (ScoreDoc scoreDoc : scoreDocs) {

				// 7、根据searcher和ScoreDoc对象获取具体的Document对象
				Document document = indexSearcher.doc(scoreDoc.doc);

				// 8、根据Document对象获取需要的值
				System.out.println(document.get("filename") + " " + document.get("filepath"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (directoryReader != null) {
					directoryReader.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	
	public void unDel(){
		IndexWriter indexWriter = null;
		try {
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			indexWriter = new IndexWriter(directory, config);
			indexWriter.rollback();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				indexWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 单例IndexReader
	 * @param keyWord
	 */
	public void searchByRange(String field,String lowerTerm,String upperTerm,boolean includeLower,boolean includeUpper) {
		try {
			System.out.println("maxDoc->" + getDirectoryReader().maxDoc());
			System.out.println("numDeletedDocs->" + getDirectoryReader().numDeletedDocs());
			System.out.println("numDocs->" + getDirectoryReader().numDocs());
			// 3、根据IndexReader创建IndexSearch
			IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

			// 4、创建搜索的Query
			Analyzer analyzer = new StandardAnalyzer();
			TermRangeQuery query=new TermRangeQuery(field, new BytesRef(lowerTerm.getBytes("UTF-8")), new BytesRef(upperTerm.getBytes("UTF-8")), includeLower, includeUpper);

			// 5、根据searcher搜索并且返回TopDocs
			TopDocs topDocs = indexSearcher.search(query, 10);
			System.out.println("查找到的文档总共有：" + topDocs.totalHits);

			// 6、根据TopDocs获取ScoreDoc对象
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (ScoreDoc scoreDoc : scoreDocs) {

				// 7、根据searcher和ScoreDoc对象获取具体的Document对象
				Document document = indexSearcher.doc(scoreDoc.doc);

				// 8、根据Document对象获取需要的值
				System.out.println(document.get("filename") + " " + document.get("filepath"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * try { if (directoryReader != null) { directoryReader.close(); } }
			 * catch (Exception e) { e.printStackTrace(); }
			 */
		}
	}

}
	