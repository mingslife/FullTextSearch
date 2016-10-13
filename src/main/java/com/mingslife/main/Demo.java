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
		String text = "jcseg��ʹ��Java������һ�Դ�����ķִ���, �������е�mmseg�㷨ʵ�֣��ִ�׼ȷ�ʸߴ�98.4%, ֧����������ʶ��, ͬ���ƥ��, ֹͣ�ʹ��˵ȡ������ṩ�����°汾��lucene,solr,elasticsearch�ִʽӿڡ�";
		// �����֪��ѡ���ĸ�Directory�����࣬��ô�Ƽ�ʹ��FSDirectory.open()��������Ŀ¼
		Analyzer analyzer = new JcsegAnalyzer5X(JcsegTaskConfig.COMPLEX_MODE);
		// �Ǳ���(�����޸�Ĭ������): ��ȡ�ִ���������ʵ��
		JcsegAnalyzer5X jcseg = (JcsegAnalyzer5X) analyzer;
		JcsegTaskConfig config = jcseg.getTaskConfig();
		// ׷��ͬ���, ��Ҫ�� jcseg.properties������jcseg.loadsyn=1
		config.setAppendCJKSyn(true);
		// ׷��ƴ��, ��Ҫ��jcseg.properties������jcseg.loadpinyin=1
		config.setAppendCJKPinyin(true);
		// ��������, ��鿴 org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig
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
		String key = "���ķִ���";
		QueryParser queryParser = new QueryParser("text", analyzer);
		queryParser.setDefaultOperator(QueryParser.Operator.AND);
		Query parse = queryParser.parse(key);
		System.out.println(parse);
		TopDocs search = indexSearcher.search(parse, 10);
		System.out.println("��������" + search.totalHits);
		ScoreDoc[] scoreDocs = search.scoreDocs;
		for (ScoreDoc sd : scoreDocs) {
			Document doc = indexSearcher.doc(sd.doc);
			System.out.println("�÷֣�" + sd.score);
			System.out.println(doc.get("text"));
		}
	}
}
