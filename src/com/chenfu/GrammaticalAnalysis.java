package com.chenfu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GrammaticalAnalysis {

    private static ArrayList<Production> productions = new ArrayList<>();
    private static Map<Character, Set<Character>> firstmap = new HashMap<>();
    private static Map<Character, Set<Character>> followmap = new HashMap<>();
    private static HashMap<Character, Integer> lmap = new HashMap<>();
    private static HashMap<Character, Integer> rmap = new HashMap<>();
    private static String[][] table;

    public static void main(String[] args) throws Exception {
        String path = "sources/2.txt";
        String expression = "(i*i*i)";
        initExpression(path);
        eliminateRecursion(productions);
        Utils.initProductionMap(productions, lmap, rmap);
        getFirstMapAndFollowMap();
        initTable();
        showTable(table);
        analysis(expression);
    }


    private static Production getProduction(char L) {
        for (Production production : productions) {
            if (production.getL() == L) {
                return production;
            }
        }
        return null;
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void eliminateRecursion(ArrayList<Production> productions) {
        String chars = "QWERTYUUIOPASDFGHJKLZXCVBNM";
        char[] charsarray = chars.toCharArray();
        ArrayList<Character> characters = new ArrayList<>();
        for (char c : charsarray) {
            characters.add(c);
        }
        characters.removeAll(productions);
        for (int i = 0; i < productions.size(); i++) {
            Production production = productions.get(i);
            char L = production.getL();
            String R = production.getR();
            if (L == R.charAt(0)) {
                int index = R.indexOf('|');
                String s1 = R.substring(index + 1);
                Character character = characters.get(0);
                characters.remove(0);
                char s2 = character;
                String s3 = R.substring(1, index) + s2 + "|@";
                production.setR(s1 + s2);
                Production p = new Production(character, s3);
                productions.add(p);
            }
        }
        for (Production production : productions) {
            System.out.println(production);
        }
    }

    private static void getFirstMapAndFollowMap() {
        for (Production production : productions) {
            char L = production.getL();
            Set<Character> firstSet = new HashSet<>();
            firstmap.put(L, firstSet);
            Set<Character> followSet = new HashSet<>();
            followmap.put(L, followSet);
        }
        for (Production production : productions) {
            getFirstMap(production);
        }
        for (Map.Entry entry : firstmap.entrySet()) {
            System.out.println("first(" + entry.getKey() + ")=" + entry.getValue());
        }
        followmap.get(productions.get(0).getL()).add('#');
        for (Production production : productions) {
            getFollowMap(production.getL());
        }
        for (Map.Entry entry : followmap.entrySet()) {
            System.out.println("follow(" + entry.getKey() + ")=" + entry.getValue());
        }
    }

    private static boolean hasEepsilonAndMergeMap(Set<Character> characters, char c) {
        boolean hasEepsilon = false;
        Set<Character> set = firstmap.get(c);
        for (Character f : set) {
            if (f == '@') {
                hasEepsilon = true;
                continue;
            }
            characters.add(f);
        }
        return hasEepsilon;
    }

    private static void getFirstMap(Production production) {
        char L = production.getL();
        String R = production.getR();
        Set<Character> firstSet = firstmap.get(L);
        String[] strings = R.split("\\|");
        for (String s : strings) {
            char[] chars = s.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                char c = chars[j];
                if (Utils.isNonterminal(c)) {
                    Production p = getProduction(c);
                    getFirstMap(p);
                    if (hasEepsilonAndMergeMap(firstSet, p.getL())) {
                        if (j == chars.length - 1) {
                            firstSet.add('@');
                        }
                    } else {
                        break;
                    }
                } else {
                    firstSet.add(c);
                    break;
                }
            }
        }
    }

    private static void getFollowMap(char c) {
        Set<Character> followSet = followmap.get(c);
        String sc = String.valueOf(c);
        for (Production production : productions) {
            char L = production.getL();
            String R = production.getR();
            String[] strings = R.split("\\|");
            for (String s : strings) {
                int index = 0;
                while ((index = s.indexOf(sc, index)) != -1) {
                    index += 1;
                    while (index < s.length()) {
                        char followChar = s.charAt(index);
                        if (Utils.isTerminal(followChar)) {
                            followSet.add(followChar);
                            break;
                        } else {
                            if (!hasEepsilonAndMergeMap(followSet, followChar)) {
                                break;
                            }
                            index += 1;
                        }
                    }
                    if (index == s.length()) {
                        if (c != L) {
                            getFollowMap(L);
                            Set<Character> characterSet = followmap.get(L);
                            followSet.addAll(characterSet);
                        }
                    }
                }
            }
        }
    }

    private static void addToTable(char L, char c, String R) {
        int row = lmap.get(L);
        int col = rmap.get(c);
        table[row][col] = L + "->" + R;
    }

    private static boolean batchAddToTable(char L, Set<Character> characters, String s) {
        boolean hasEepsilon = false;
        for (Character character : characters) {
            if (character == '@') {
                hasEepsilon = true;
            } else {
                addToTable(L, character, s);
            }
        }
        return hasEepsilon;
    }

    private static void initTable() {
        table = new String[lmap.size()][rmap.size()];
        for (Production production : productions) {
            char L = production.getL();
            String R = production.getR();
            String[] split = R.split("\\|");
            for (String s : split) {
                char[] chars = s.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    char c = chars[i];
                    if (c == '@') {
                        Set<Character> followSet = followmap.get(L);
                        batchAddToTable(L, followSet, s);
                    } else if (Utils.isTerminal(c)) {
                        addToTable(L, c, s);
                        break;
                    } else {
                        Set<Character> firstSet = firstmap.get(c);
                        if (batchAddToTable(L, firstSet, s)) {
                            if (i == chars.length - 1) {
                                Set<Character> followSet = followmap.get(L);
                                batchAddToTable(L, followSet, s);
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void showTable(String[][] table) {
        if (table == null) {
            System.out.println("table is null");
            return;
        }
        for (int i = 0; i < table[0].length; i++) {
            for (Map.Entry<Character, Integer> entry : rmap.entrySet()) {
                if (entry.getValue() == i) {
                    System.out.print(entry.getKey() + "      ");
                }
            }
        }
        System.out.println();
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                System.out.print(table[i][j] + "  ");
            }
            System.out.println();
        }
    }

    private static void analysis(String expression) {
        expression = expression + "#";
        Stack<Character> stack = new Stack<>();
        Queue<Character> queue = new ArrayDeque<Character>();
        for (int i = 0; i < expression.length(); i++) {
            queue.add(expression.charAt(i));
        }
        stack.push('#');
        stack.push(productions.get(0).getL());
        boolean flag = true;
        int count = 0;
        while (flag) {
            count++;
            System.out.println(String.format("%-12d%-36s%24s", count, stack, queue));
            Character c = stack.pop();
            if (c == '#') {
                if (c == queue.poll()) {
                    flag = false;
                } else {
                    System.out.println("ERROR");
                    return;
                }
            } else if (Utils.isTerminal(c)) {
                if (c != queue.poll()) {
                    System.out.println("ERROR");
                    return;
                }
            } else {
                String s = null;
                try {
                    int i = lmap.get(c);
                    int j = rmap.get(queue.peek());
                    s = table[i][j];
                }catch (NullPointerException e){
                    System.out.println("ERROR");
                    return;
                }
                if (s == null) {
                    System.out.println("ERROR");
                    return;
                }
                s = s.substring(s.indexOf('>') + 1);
                if (!s.equals("@")) {
                    char[] toCharArray = s.toCharArray();
                    for (int k = toCharArray.length - 1; k >= 0; k--) {
                        stack.push(toCharArray[k]);
                    }
                }
            }
        }
        System.out.println("SUCCESS");
    }
}