package com.chenfu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class OperatorPrecedenceAnalysis {

    private static ArrayList<Production> productions = new ArrayList<>();
    private static HashMap<Character, Integer> pMap = new HashMap<>();
    private static HashMap<Character, Integer> lMap = new HashMap<>();
    private static boolean[][] table;
    public static void main(String[] args) {
        String path = "sources/5.txt";
        initExpression(path);
        initTable();
        getFirstVT();
//        showTable();
        showFirstVT();
    }

    private static void initExpression(String path) {
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String s = "";
            while ((s = bufferedReader.readLine()) != null) {
                int i = s.indexOf('-');
                Production p = new Production(s.charAt(0), s.substring(i + 2));
                productions.add(p);
                System.out.println(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initTable() {
        int n =0,m=productions.size();
        for(int i=0;i<productions.size();i++){
            Production production = productions.get(i);
            pMap.put(production.P,i);
            String L = production.L;
            char[] chars = L.toCharArray();
            for (char c : chars) {
                if (!Character.isUpperCase(c) && c != '@' && c != '|') {
                    lMap.put(c, n);
                    n += 1;
                }
            }
        }
        table = new boolean[m][n];
    }

    private static void insert(Stack stack,Production production){
        int i = pMap.get(production.P);
        int j = lMap.get(production.L.charAt(0));
        if (!table[i][j]) {
            table[i][j]=true;
            stack.push(production);
        }
    }

    private static void getFirstVT(){
        Stack<Production> stack = new Stack<>();
        for (int i = 0; i < productions.size(); i++) {
            Production p = productions.get(i);
            char P = p.P;
            String L = p.L;
            String[] strings = L.split("\\|");
            for (String s : strings) {
                char c1 = s.charAt(0);
                if (!Character.isUpperCase(c1)) {
                    Production production = new Production(P,c1+"");
                    insert(stack,production);
                }else {
                    if (s.length() > 1) {
                        char c2 = s.charAt(1);
                        if (!Character.isUpperCase(c2)) {
                            Production production = new Production(P,c2+"");
                            insert(stack,production);
                        }
                    }
                }
            }
        }
        while (!stack.empty()) {
            Production Q = stack.pop();
            char q = Q.P;
            String a = Q.L;
            for (int i = 0; i < productions.size(); i++) {
                Production production = productions.get(i);
                char P = production.P;
                String L = production.L;
                String[] strings = L.split("\\|");
                for (String s : strings) {
                    if (q==s.charAt(0)) {
                        Production p2 = new Production(P,a);
                        insert(stack,p2);
                    }
                }
            }
        }
    }

    private static void showTable(){
        for(int i=0;i<table.length;i++){
            for (int j = 0; j < table[i].length; j++) {
                System.out.print(table[i][j]+"  ");
            }
            System.out.println();
        }
    }

    private static void showFirstVT() {
        for (int i = 0; i < table.length; i++) {
            for (Map.Entry<Character, Integer> entry1 : pMap.entrySet()) {
                if(entry1.getValue()==i){
                    System.out.print(entry1.getKey()+"{ ");
                    for (int j = 0; j < table[i].length; j++) {
                        for (Map.Entry<Character, Integer> entry2 : lMap.entrySet()) {
                            if (entry2.getValue() == j && table[i][j]) {
                                System.out.print(entry2.getKey()+" 、");
                            }
                        }
                    }
                    System.out.println(" }");
                }
            }
        }
    }
}
