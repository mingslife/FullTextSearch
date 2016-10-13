package com.mingslife.main;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.lionsoul.jcseg.analyzer.v5x.JcsegAnalyzer5X;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

import com.mingslife.entity.Article;

public class Main {
	public static void main(String[] args) throws IOException, ParseException {
		Analyzer analyzer = new JcsegAnalyzer5X(JcsegTaskConfig.COMPLEX_MODE);
		JcsegAnalyzer5X jcseg = (JcsegAnalyzer5X) analyzer;
		JcsegTaskConfig config = jcseg.getTaskConfig();
		config.setAppendCJKSyn(true);
		config.setAppendCJKPinyin(true);
		Directory directory = new RAMDirectory();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		Article article1 = new Article(1, "测试中文分词", "黄则鸣", "这是一篇测试文章。", new Date(1994, 5, 3));
		Article article2 = new Article(2, "Test", "Ming", "Hello world!", new Date(1970, 1, 1));
		Article article3 = new Article(3, "测试中英文混合", "鸣", "这是一篇中文和英文混合的文章。全文检索功能采用Lucene+Jcseg实现。", new Date(2016, 10, 4));
		addDocument(indexWriter, article1);
		addDocument(indexWriter, article2);
		addDocument(indexWriter, article3);
		deleteDocument(indexWriter, article1);
		article3.setContent("这是一篇中文和英文混合的文章。全文检索功能采用Lucene+Jcseg实现。测试一下更新索引。");
		updateDocument(indexWriter, article3);
		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		String key = "这是一篇文章。";
		
//		QueryParser queryParser = new QueryParser("content", analyzer);
//		queryParser.setDefaultOperator(QueryParser.Operator.AND);
//		Query query = queryParser.parse(key);
		
//		FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term("content", key));
//		Query query = fuzzyQuery.rewrite(indexReader);
		
//		BooleanClause.Occur[] clauses = {BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD};
//		Query query = MultiFieldQueryParser.parse(key, new String[] {"title", "content"}, clauses, analyzer);
		
		// Special
		QueryParser queryParser = new QueryParser("content", analyzer);
		queryParser.setDefaultOperator(QueryParser.Operator.OR);
		Query query = queryParser.parse(key);
		
		System.out.println(query);
		TopDocs search = indexSearcher.search(query, 10);
		System.out.println("命中数: " + search.totalHits);
		ScoreDoc[] scoreDocs = search.scoreDocs;
		for (ScoreDoc sd : scoreDocs) {
			Document doc = indexSearcher.doc(sd.doc);
			System.out.println("------------------------------------------------------------");
			System.out.println("得分: " + sd.score);
			System.out.println("标题: " + doc.get("title"));
			System.out.println("内容:\n" + doc.get("content"));
		}
	}

	private static void addDocument(IndexWriter indexWriter, Article article) throws IOException {
		Document document = new Document();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		document.add(new StringField("id", String.valueOf(article.getId()), Field.Store.YES));
		document.add(new TextField("title", article.getTitle(), Field.Store.YES));
		document.add(new StringField("author", article.getAuthor(), Field.Store.YES));
		document.add(new TextField("content", article.getContent(), Field.Store.YES));
		document.add(new TextField("text", article.getTitle() + "\n" + article.getContent(), Field.Store.YES));
		document.add(new StringField("publishDate", simpleDateFormat.format(article.getPublishDate()), Field.Store.YES));
		indexWriter.addDocument(document);
		indexWriter.commit();
	}

	private static void deleteDocument(IndexWriter indexWriter, Article article) throws IOException {
		indexWriter.deleteDocuments(new Term("id", String.valueOf(article.getId())));
		indexWriter.commit();
	}

	private static void updateDocument(IndexWriter indexWriter, Article article) throws IOException {
		Document document = new Document();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		document.add(new StringField("id", String.valueOf(article.getId()), Field.Store.YES));
		document.add(new TextField("title", article.getTitle(), Field.Store.YES));
		document.add(new StringField("author", article.getAuthor(), Field.Store.YES));
		document.add(new TextField("content", article.getContent(), Field.Store.YES));
		document.add(new TextField("text", article.getTitle() + "\n" + article.getContent(), Field.Store.YES));
		document.add(new StringField("publishDate", simpleDateFormat.format(article.getPublishDate()), Field.Store.YES));
		indexWriter.updateDocument(new Term("id", String.valueOf(article.getId())), document);
		indexWriter.commit();
	}
}
