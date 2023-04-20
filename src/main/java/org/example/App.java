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

                doAction(conn, sc, rq, cmd);

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

    private void doAction(Connection conn, Scanner sc, Rq rq, String cmd) {
        if (rq.getUrlPath().equals("/usr/article/join")) {
            String loginId;
            String loginPw;
            String loginPwConfirm;
            String name;

            System.out.println("== 회원가입 ==");

            // 회원가입 아이디 입력
            while (true) {
                System.out.printf("로그인 아이디 : ");
                loginId = sc.nextLine().trim();

                if (loginId.length() == 0) {
                    System.out.println("로그인 아이디를 입력해 주세요.");
                    continue;
                }

                SecSql sql = new SecSql();

                sql.append("SELECT COUNT(*) > 0");
                sql.append("FROM `member`");
                sql.append("WHERE loginId = ?", loginId);

                boolean isLoginIdDup = DBUtil.selectRowBooleanValue(conn, sql);

                if (isLoginIdDup) {
                    System.out.printf("%s은(는) 이미 사용중인 로그인 아이디입니다. \n", loginId);
                    continue;
                }

                break;
            }
            // 회원가입 비밀번호 입력

            while (true) {
                System.out.printf("로그인 비밀번호 : ");
                loginPw = sc.nextLine().trim();

                if (loginPw.length() == 0) {
                    System.out.println("로그인 비밀번호를 입력해 주세요.");
                    continue;
                }

                boolean loginPwConfirmIsSame = true;

                while (true) {
                    System.out.printf("로그인 비밀번호 확인: ");
                    loginPwConfirm = sc.nextLine().trim();
                    if (loginPwConfirm.length() == 0) {
                        System.out.println("로그인 비밀번호를 입력해 주세요.");
                        continue;
                    }

                    if (loginPw.equals(loginPwConfirm) == false) {
                        System.out.println("로그인 비밀번호를 입력해 주세요.");
                        loginPwConfirmIsSame = false;
                        break;
                    }
                    break;
                }

                if (loginPwConfirmIsSame) {
                    break;
                }
            }

            while (true) {
                System.out.println("이름 : ");
                name = sc.nextLine().trim();
                if (name.length() == 0) {
                    System.out.println("이름을 입력해주세요.");
                    continue;
                }
                break;
            }

            SecSql sql = new SecSql();

            sql.append("INSERT INTO member");
            sql.append("SET regDate = NOW()");
            sql.append(", updateDate = NOW()");
            sql.append(", loginId = ?", loginId);
            sql.append(", loginPw = ?", loginPw);
            sql.append(", name = ?", name);

            int id = DBUtil.insert(conn, sql);

            System.out.printf("%d번 회원이 등록되었습니다.\n", id);
        }  else if (rq.getUrlPath().equals("/usr/article/write")) {
            System.out.println("== 게시물 등록 ==");
            System.out.printf("제목 : ");
            String title = sc.nextLine();
            System.out.printf("내용 : ");
            String body = sc.nextLine();

            SecSql sql = new SecSql();

            sql.append("INSERT INTO article");
            sql.append("SET regDate = NOW()");
            sql.append(", updateDate = NOW()");
            sql.append(", title = ?", title);
            sql.append(", `body` = ?", body);

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
                return;
            }

            for (Article article : articles) {
                System.out.printf("%d / %s \n", article.id, article.title);
            }
        } else if (rq.getUrlPath().equals("/usr/article/modify")) {
            int id = rq.getIntParam("id", 0);

            if (id == 0) {
                System.out.println("id를 올바르게 입력해주세요.");
                return;
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

        } else if (rq.getUrlPath().equals("/usr/article/delete")) {
            int id = rq.getIntParam("id", 0);

            if (id == 0) {
                System.out.println("id를 올바르게 입력해주세요.");
                return;
            }

            SecSql sql = new SecSql();

            sql.append("SELECT COUNT(*) AS cnt");
            sql.append("FROM article");
            sql.append("WHERE id = ?", id);

            int articleCount = DBUtil.selectRowIntValue(conn, sql);

            if (articleCount == 0) {
                System.out.printf("%d 번 글은 존재하지 않습니다.", id);
                return;
            }

            sql = new SecSql();

            sql.append("DELETE FROM article");
            sql.append("WHERE id = ?", id);

            DBUtil.update(conn, sql);

            System.out.printf("%d번 게시물이 삭제되었습니다.\n", id);

        } else if (rq.getUrlPath().equals("/usr/article/detail")) {
            int id = rq.getIntParam("id", 0);

            if (id == 0) {
                System.out.println("id를 올바르게 입력해주세요.");
                return;
            }

            SecSql sql = new SecSql();

            sql.append("SELECT * ");
            sql.append("FROM article");
            sql.append("WHERE id = ?", id);

            Map<String, Object> articleMap = DBUtil.selectRow(conn, sql);

            if (articleMap.isEmpty()) {
                System.out.printf("%d 번 글은 존재하지 않습니다.", id);
                return;
            }

            Article article = new Article(articleMap);

            System.out.printf("번호 : %d\n", article.id);
            System.out.printf("제목 : %s\n", article.title);
            System.out.printf("내용 : %s\n", article.body);
            System.out.printf("등록일 : %s\n", article.regDate);
            System.out.printf("수정일 : %s\n", article.updateDate);

        } else if (cmd.equals("system exit")) {
            System.out.println("시스템 종료");
            System.exit(0);
        } else {
            System.out.println("명령어를 확인해 주세요.");
        }
        return;
    }
}
