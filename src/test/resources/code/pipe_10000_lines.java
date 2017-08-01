package com.test;

import java.util.Scanner;

public class Test {
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        for (int i = 0; i < 10000; ++i) {
            System.out.println(s.nextLine());
        }
    }
}
