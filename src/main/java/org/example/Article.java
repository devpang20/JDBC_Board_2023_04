package org.example;

public class Article {
    public int id;
    public String title;
    private String body;
    private String regDate;
    private String updateDate;

    public Article(int id, String regDate, String updateDate, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public Article(int id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
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
