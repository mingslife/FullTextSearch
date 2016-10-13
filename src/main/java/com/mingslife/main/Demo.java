package com.mingslife.main;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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

public class Demo {
	public static void main(String[] args) throws IOException, ParseException {
		String text = "jcseg是使用Java开发的一款开源的中文分词器, 基于流行的mmseg算法实现，分词准确率高达98.4%, 支持中文人名识别, 同义词匹配, 停止词过滤等。并且提供了最新版本的lucene,solr,elasticsearch分词接口。";
		// 如果不知道选择哪个Directory的子类，那么推荐使用FSDirectory.open()方法来打开目录
		Analyzer analyzer = new JcsegAnalyzer5X(JcsegTaskConfig.COMPLEX_MODE);
		// 非必须(用于修改默认配置): 获取分词任务配置实例
		JcsegAnalyzer5X jcseg = (JcsegAnalyzer5X) analyzer;
		JcsegTaskConfig config = jcseg.getTaskConfig();
		// 追加同义词, 需要在 jcseg.properties中配置jcseg.loadsyn=1
		config.setAppendCJKSyn(true);
		// 追加拼音, 需要在jcseg.properties中配置jcseg.loadpinyin=1
		config.setAppendCJKPinyin(true);
		// 更多配置, 请查看 org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig
		Directory directory = new RAMDirectory();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		Document document = new Document();
		document.add(new StringField("id", "1000", Field.Store.YES));
		document.add(new TextField("text", text, Field.Store.YES));
		indexWriter.addDocument(document);
		indexWriter.commit();
		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		String key = "中文分词器";
		QueryParser queryParser = new QueryParser("text", analyzer);
		queryParser.setDefaultOperator(QueryParser.Operator.AND);
		Query parse = queryParser.parse(key);
		System.out.println(parse);
		TopDocs search = indexSearcher.search(parse, 10);
		System.out.println("命中数：" + search.totalHits);
		ScoreDoc[] scoreDocs = search.scoreDocs;
		for (ScoreDoc sd : scoreDocs) {
			Document doc = indexSearcher.doc(sd.doc);
			System.out.println("得分：" + sd.score);
			System.out.println(doc.get("text"));
		}
	}
}
