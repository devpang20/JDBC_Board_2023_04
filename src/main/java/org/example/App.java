package org.example;

import org.example.util.DBUtil;
import org.example.util.SecSql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    int articleListId = 0;

    public void run() {
        Scanner sc = Container.scanner;

        while (true) {
            System.out.printf("명령어) ");
            String cmd = sc.nextLine();

            Rq rq = new Rq(cmd);
            Connection conn = null;
            
            //DB 연결
            try {
                Class.forName("com.mysql.jdbc.Driver");

                String url = "jdbc:mysql://127.0.0.1:3306/text_board?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul&useOldAliasMetadataBehavior=true&zeroDateTimeNehavior=convertToNull";

                conn = DriverManager.getConnection(url, "root", "");

                int actionResult = doAction(conn, sc, rq, cmd);

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
            // 연결 끝

        }
    }

    private int doAction(Connection conn, Scanner sc, Rq rq, String cmd) {
        if (rq.getUrlPath().equals("/usr/article/write")) {
            System.out.println("=== 게시물 등록 ===");
            System.out.printf("제목 : ");
            String title = sc.nextLine();

            System.out.printf("내용 : ");
            String body = sc.nextLine();

            SecSql sql = new SecSql();

            sql.append("INSERT INTO article");
            sql.append(" SET regDate = NOW()");
            sql.append(", UpdateDate = NOW()");
            sql.append(", title = ?", title);
            sql.append(", title = ?", body);

            int id = DBUtil.insert(conn, sql);

            System.out.printf("%d번 게시물이 등록되었습니다.\n", id);

        } else if (rq.getUrlPath().equals("article list")) {

            PreparedStatement pstmt = null;
            ResultSet rs = null;

            List<Article> articles = new ArrayList<>();

            try {
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

            } catch (SQLException e) {
                System.out.println("에러 : " + e);
            } finally {
                try {
                    if (rs != null && !rs.isClosed()) {
                        rs.close();
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
            System.out.println("=== 게시물 리스트===");

            System.out.println("번호 / 제목");

            if (articles.isEmpty()) {
                System.out.println("게시물이 존재 하지 않습니다.");
                return -1;
            }

            for (Article article : articles) {
                System.out.printf("%d / %s \n", article.id, article.title);
            }
        } else if (rq.getUrlPath().equals("/usr/article/modify")) {
            int id = rq.getIntParam("id", 0);

            if (id == 0) {
                System.out.println("id를 올바르게 입력해주세요.");
                return -1;
            }

            System.out.printf("새 제목 : ");
            String title = sc.nextLine();
            System.out.printf("새 내용 : ");
            String body = sc.nextLine();


            PreparedStatement pstmt = null;

            try {
                String sql = "UPDATE article";
                sql += " SET updateDate = NOW()";
                sql += ", title = \"" + title + "\"";
                sql += ", `body` = \"" + body + "\"";
                sql += " WHERE id = " + id;

                pstmt = conn.prepareStatement(sql);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("에러 : " + e);
            } finally {
                try {
                    if (pstmt != null && !pstmt.isClosed()) {
                        pstmt.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            System.out.printf("%d번 게시물이 수정되었습니다.\n", id);

        } else if (cmd.equals("system exit")) {
            System.out.println("시스템 종료");
            System.exit(0);
        } else {
            System.out.println("명령어를 확인해 주세요.");
        }
        return 0;
    }
}
