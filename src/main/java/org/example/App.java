package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

    public void run() {
        Scanner sc = Container.scanner;
        int articleListId = 0;

        while (true) {
            System.out.printf("명령어) ");
            String cmd = sc.nextLine();

            if (cmd.equals("/usr/article/write")) {
                System.out.println("=== 게시물 등록 ===");
                System.out.printf("제목 : ");
                String title = sc.nextLine();

                System.out.printf("내용 : ");
                String body = sc.nextLine();
                int id = ++articleListId;

                Connection conn = null;
                PreparedStatement pstmt = null;

                try {
                    Class.forName("com.mysql.jdbc.Driver");

                    String url = "jdbc:mysql://127.0.0.1:3306/text_board?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul&useOldAliasMetadataBehavior=true&zeroDateTimeNehavior=convertToNull";

                    conn = DriverManager.getConnection(url, "root", "");

                    String sql = "INSERT INTO article";
                    sql += " SET regDate = NOW()";
                    sql += ", updateDate = NOW()";
                    sql += ", title = \"" + title + "\"";
                    sql += ", `body` = \"" + body + "\"";

                    pstmt = conn.prepareStatement(sql);
                    int affectedRows = pstmt.executeUpdate();

                    System.out.println("affectedRows : " + affectedRows);
                } catch (ClassNotFoundException e) {
                    System.out.println("드라이버 로딩 실패");
                } catch (SQLException e) {
                    System.out.println("에러: " + e);
                } finally {
                    try {
                        if (conn != null && !conn.isClosed()) {
                            conn.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (pstmt != null && !pstmt.isClosed()) {
                            pstmt.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            } else if (cmd.equals("article list")) {
                System.out.println("=== 게시물 리스트===");

                Connection conn = null;
                PreparedStatement pstmt = null;
                ResultSet rs = null;

                List<Article> articles = new ArrayList<>();

                try {
                    Class.forName("com.mysql.jdbc.Driver");

                    String url = "jdbc:mysql://127.0.0.1:3306/text_board?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul&useOldAliasMetadataBehavior=true&zeroDateTimeNehavior=convertToNull";

                    conn = DriverManager.getConnection(url, "root", "");

                    String sql = "SELECT *";
                    sql += " FROM article";
                    sql += " ORDER BY id DESC";

                    pstmt = conn.prepareStatement(sql);
                    rs = pstmt.executeQuery(sql);


                    while (rs.next()) {
                        int id = rs.getInt("id");

                        String regDate = rs.getString("regDate");
                        String updateDate = rs.getString("updateDate");
                        String title = rs.getString("title");
                        String body = rs.getString("body");

                        Article article = new Article(id, regDate, updateDate, title, body);
                        articles.add(article);
                    }

                    System.out.println("결과 : " + articles);

                    System.out.println("연결 성공");
                } catch (ClassNotFoundException e) {
                    System.out.println("드라이버 로딩 실패");
                } catch (SQLException e) {
                    System.out.println("에러: " + e);
                } finally {
                    try {
                        if (conn != null && !conn.isClosed()) {
                            conn.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("번호 / 제목");

                if (articles.isEmpty()) {
                    System.out.println("게시물이 존재 하지 않습니다.");
                    continue;
                }

                for (Article article : articles) {
                    System.out.printf("%d / %s \n", article.id, article.title);
                }

            } else if (cmd.equals("system exit")) {
                System.out.println("시스템 종료");
                break;
            } else {
                System.out.println("명령어를 확인해 주세요.");
            }
        }
        sc.close();
    }
}
