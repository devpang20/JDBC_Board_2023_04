package org.example;

import org.example.util.DBUtil;
import org.example.util.SecSql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            List<Article> articles = new ArrayList<>();

            SecSql sql = new SecSql();

            sql.append("SELECT *");
            sql.append(" FROM article");
            sql.append(" ORDER BY id DESC");

            List<Map<String, Object>> articleListMap = DBUtil.selectRows(conn, sql);


            for (Map<String, Object> articleMap : articleListMap) {
                articles.add(new Article(articleMap));
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


            SecSql sql = new SecSql();

            sql.append("UPDATE article");
            sql.append(" SET regDate = NOW()");
            sql.append(", UpdateDate = NOW()");
            sql.append(", title = ?", title);
            sql.append(", title = ?", body);
            sql.append(", where id = ?", id);

            DBUtil.update(conn, sql);

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
