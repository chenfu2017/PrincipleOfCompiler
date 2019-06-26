package com.chenfu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class OperatorPrecedenceAnalysis {

    private static ArrayList<Production> productions = new ArrayList<>();
    private static HashMap<Character, Integer> lmap = new HashMap<>();
    private static HashMap<Character, Integer> rmap = new HashMap<>();
    private static boolean[][] firstTable;
    private static boolean[][] lastTable;
    private static char[][] priorityTable;

    public static void main(String[] args) {
        String path = "sources/5.txt";
        initExpression(path);
        Utils.initProductionMap(productions, lmap, rmap);
        getVT(StrEnum.FIRSTVT);
        getVT(StrEnum.LASTVT);
        initPriorityTable();
        showPriorityTable();
        analysis("i+i+i*(i+i)");
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

    private static void insert(Stack stack, boolean[][] table, Production production) {
        int i = lmap.get(production.getL());
        int j = rmap.get(production.getR().charAt(0));
        if (!table[i][j]) {
            table[i][j] = true;
            stack.push(production);
        }
    }

    private static void getVT(StrEnum strEnum) {
        int n, m;
        boolean[][] table;
        if (strEnum == StrEnum.FIRSTVT) {
            firstTable = new boolean[productions.size()][rmap.size() - 1];
            table = firstTable;
        } else {
            lastTable = new boolean[productions.size()][rmap.size() - 1];
            table = lastTable;
        }
        Stack<Production> stack = new Stack<>();
        for (int i = 0; i < productions.size(); i++) {
            Production p = productions.get(i);
            String L = p.getR();
            String[] strings = L.split("\\|");
            for (String s : strings) {
                if (strEnum == StrEnum.FIRSTVT) {
                    n = 0;
                    m = n + 1;
                } else {
                    n = s.length() - 1;
                    m = s.length() - 2;

                }
                char c1 = s.charAt(n);
                if (Utils.isTerminal(c1)) {
                    Production production = new Production(p.getL(), c1 + "");
                    insert(stack, table, production);
                } else {
                    if (s.length() > 1) {
                        char c2 = s.charAt(m);
                        if (Utils.isTerminal(c2)) {
                            Production production = new Production(p.getL(), c2 + "");
                            insert(stack, table, production);
                        }
                    }
                }
            }
        }
        while (!stack.empty()) {
            Production Q = stack.pop();
            char q = Q.getL();
            String a = Q.getR();
            for (int i = 0; i < productions.size(); i++) {
                Production production = productions.get(i);
                char P = production.getL();
                String L = production.getR();
                String[] strings = L.split("\\|");
                for (String s : strings) {
                    if (q == s.charAt(0)) {
                        Production p2 = new Production(P, a);
                        insert(stack, table, p2);
                    }
                }
            }
        }
        showVT(strEnum.getType(), table);
    }


    private static void showVT(String type, boolean[][] table) {
        for (int i = 0; i < table.length; i++) {
            for (Map.Entry<Character, Integer> entry1 : lmap.entrySet()) {
                if (entry1.getValue() == i) {
                    System.out.print(type + "(" + entry1.getKey() + ") ={ ");
                    for (int j = 0; j < table[i].length; j++) {
                        if (table[i][j]) {
                            for (Map.Entry<Character, Integer> entry2 : rmap.entrySet()) {
                                if (entry2.getValue() == j) {
                                    System.out.print(entry2.getKey() + " 、");
                                }
                            }
                        }
                    }
                    System.out.println(" }");
                }
            }
        }
    }

    private static void initPriorityTable() {
        priorityTable = new char[rmap.size()][rmap.size()];
        productions.add(new Production('E', "#E#"));
        for (int i = 0; i < productions.size(); i++) {
            Production production = productions.get(i);
            char L = production.getL();
            String R = production.getR();
            String[] split = R.split("\\|");
            for (String s : split) {
                char[] chars = s.toCharArray();
                int n = s.length() - 1;
                for (int j = 0; j < n; j++) {
                    if (Utils.isTerminal(chars[j]) && Utils.isTerminal(chars[j + 1])) {
                        priorityTable[rmap.get(chars[j])][rmap.get(chars[j + 1])] = '=';
                    }
                    if (j <= n - 2 && Utils.isTerminal(chars[j]) && Utils.isTerminal(chars[j + 2]) && Utils.isNonterminal(chars[j + 1])) {
                        priorityTable[rmap.get(chars[j])][rmap.get(chars[j + 2])] = '=';
                    }
                    if (Utils.isTerminal(chars[j]) && Utils.isNonterminal(chars[j + 1])) {
                        int row = lmap.get(chars[j + 1]);
                        for (int k = 0; k < firstTable[row].length; k++) {
                            if (firstTable[row][k]) {
                                priorityTable[rmap.get(chars[j])][k] = '<';
                            }
                        }
                    }
                    if (Utils.isNonterminal(chars[j]) && Utils.isTerminal(chars[j + 1])) {
                        int row = lmap.get(chars[j]);
                        for (int k = 0; k < lastTable[row].length; k++) {
                            if (lastTable[row][k]) {
                                priorityTable[k][rmap.get(chars[j + 1])] = '>';
                            }
                        }
                    }
                }
            }
        }
    }

    private static void showPriorityTable() {
        for (int i = 0; i < priorityTable.length; i++) {
            for (int j = 0; j < priorityTable[i].length; j++) {
                if (priorityTable[i][j] == 0) {
                    System.out.print("null ");
                } else {
                    System.out.print(priorityTable[i][j] + "    ");
                }
            }
            System.out.println();
        }
    }


    private static char getPriority(char a, char b) {
        try {
            int i = rmap.get(a);
            int j = rmap.get(b);
            return priorityTable[i][j];
        } catch (NullPointerException e) {
            return 0;
        }
    }

    private static char merge(char[] S, int i, int j) {
        String s = "";
        for (int k = i; k <= j; k++) {
            s += S[k];
        }
        for (int k = i + 1; k < S.length; k++) {
            S[k] = ' ';
        }
        for (Production production : productions) {
            String productionR = production.getR();
            String[] split = productionR.split("\\|");
            for (String r : split) {
                if (r.equals(s)) {
                    return production.getL();
                }
            }
        }
        return 'N';
    }

    private static void analysis(String expression) {
        expression = expression + "#";
        char[] chars = expression.toCharArray();
        char[] S = new char[20];
        int i = 1;
        int k = 0, j;
        S[i] = '#';
        do {
            char a = chars[k];
            if (Utils.isNonterminal(S[i])) {
                j = i - 1;
            } else {
                j = i;
            }
            if (getPriority(S[j], a) == 0) {
                System.out.println("ERROR");
                return;
            } else if (getPriority(S[j], a) == '>') {
                char tmp;
                do {
                    tmp = S[j];
                    if (Utils.isNonterminal(S[j - 1])) {
                        j = j - 2;
                    } else {
                        j = j - 1;
                    }
                } while (getPriority(S[j], tmp) != '<');
                char c = merge(S, j + 1, i);
                System.out.println(S);
                i = j + 1;
                S[i] = c;
                System.out.println(S);
            } else if (getPriority(S[j], a) == '<' || getPriority(S[j], a) == '=') {
                i = i + 1;
                S[i] = a;
                k++;
            } else {
                System.out.println("REEOR");
                return;
            }

        } while (chars[k] != '#' || i != 2);
        System.out.println("SUCCESS");
    }
}
