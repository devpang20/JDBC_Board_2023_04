package org.example;

import java.util.Scanner;

public class App {

    public void run() {
        Scanner sc = Container.scanner;

        while (true) {
            System.out.printf("명령어) ");
            String cmd = sc.nextLine();

            if (cmd.equals("article list")) {
                System.out.println("=== 게시물 리스트===");
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
