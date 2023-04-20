package org.example;

import java.util.Map;

public class Article {
    public int id;
    public String title;
    public String body;
    public String regDate;
    public String updateDate;

    public Article(int id, String regDate, String updateDate, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.updateDate = updateDate;
        this.regDate = regDate;
    }

    public Article(int id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public Article(Map<String, Object> articleMap) {
        this.id = (int) articleMap.get("id");
        this.title = (String) articleMap.get("title");
        this.body = (String) articleMap.get("body");
        this.body = (String) articleMap.get("regDate");
        this.body = (String) articleMap.get("updateDate");
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
