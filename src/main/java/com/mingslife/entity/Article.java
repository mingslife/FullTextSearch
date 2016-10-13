package com.mingslife.entity;

import java.io.Serializable;
import java.util.Date;

public class Article implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String title;
	private String author;
	private String content;
	private Date publishDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public Article() {
	}

	public Article(Integer id, String title, String author, String content, Date publishDate) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.content = content;
		this.publishDate = publishDate;
	}
}
