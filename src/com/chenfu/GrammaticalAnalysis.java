package com.chenfu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GrammaticalAnalysis {

    private static ArrayList<Character> productions = new ArrayList<>();
    private static ArrayList<String> candidates = new ArrayList<>();
    private static Map<Character, Set<Character>> firstmap = new HashMap<>();
    private static Map<Character, Set<Character>> followmap = new HashMap<>();
    private static HashMap<Character, Integer> productionMap = new HashMap<>();
    private static HashMap<Character, Integer> candidateMap = new HashMap<>();
    private static String[][] table;

    public static void main(String[] args) throws Exception {
        String path = "C:\\Users\\chenfu\\Documents\\2.txt";
        String expression = "i*i*i";
        initExpression(path);
        eliminateRecursion(productions, candidates);
        initFirstMap();
        initFollowMap();
        initTable();
        showTable(table);
        analysis(expression);
    }

    private static void initFirstMap() {
        for (int i = 0; i < productions.size(); i++) {
            HashSet<Character> characters = new HashSet<>();
            firstmap.put(productions.get(i), characters);
        }
        for (int i = 0; i < productions.size(); i++) {
            getFirstMap(productions.get(i));
        }
        for (Map.Entry entry : firstmap.entrySet()) {
            System.out.println("first(" + entry.getKey() + ")=" + entry.getValue());
        }
    }

    private static void initFollowMap() {
        for (int i = 0; i < productions.size(); i++) {
            HashSet<Character> characters = new HashSet<>();
            followmap.put(productions.get(i), characters);
        }
        followmap.get('E').add('#');
        for (int i = 0; i < productions.size(); i++) {
            getFollowMap(productions.get(i));
        }
        for (Map.Entry entry : followmap.entrySet()) {
            System.out.println("follow(" + entry.getKey() + ")=" + entry.getValue());
        }
    }

    private static void initExpression(String path) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String s = "";
            while ((s = bufferedReader.readLine()) != null) {
                int i = s.indexOf('-');
                productions.add(s.charAt(0));
                candidates.add(s.substring(i + 2));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void eliminateRecursion(ArrayList<Character> productions, ArrayList<String> candidates) {
        String chars = "QWERTYUUIOPASDFGHJKLZXCVBNM";
        char[] charsarray = chars.toCharArray();
        ArrayList<Character> characters = new ArrayList<>();
        for (char c : charsarray) {
            characters.add(c);
        }
        characters.removeAll(productions);
        for (int i = 0; i < productions.size(); i++) {
            Character production = productions.get(i);
            String candidate = candidates.get(i);
            Character sc = candidate.charAt(0);
            if (sc.equals(production)) {
                int index = candidate.indexOf('|');
                String s1 = candidate.substring(index + 1);
                Character character = characters.get(0);
                characters.remove(0);
                String s2 = character.toString();
                String s3 = candidate.substring(1, index) + s2 + "|@";
                candidates.set(i, s1 + s2);
                productions.add(character);
                candidates.add(s3);

            }
        }
        for (int i = 0; i < productions.size(); i++) {
            System.out.println(productions.get(i) + "->" + candidates.get(i));
        }
    }

    private static boolean addFirstMap(Set<Character> characters, char c) {
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

    private static void getFirstMap(char c) {
        Set<Character> characters = firstmap.get(c);
        int i = productions.indexOf(c);
        String candidate = candidates.get(i);
        String[] strings = candidate.split("\\|");
        for (String s : strings) {
            char[] chars = s.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                char production = chars[j];
                if (Character.isUpperCase(production)) {
                    getFirstMap(production);
                    if (!addFirstMap(characters, production)) {
                        break;
                    }
                } else {
                    characters.add(production);
                    break;
                }
            }
        }
    }

    private static void getFollowMap(char c) {
        Set<Character> characters = followmap.get(c);
        String sc = String.valueOf(c);
        for (int i = 0; i < candidates.size(); i++) {
            String candidates = GrammaticalAnalysis.candidates.get(i);
            String[] strings = candidates.split("\\|");
            for (String candidate : strings) {
                int index = 0;
                while ((index = candidate.indexOf(sc, index)) != -1) {
                    index += 1;
                    while (index < candidate.length()) {
                        char followChar = candidate.charAt(index);
                        if (!Character.isUpperCase(followChar)) {
                            characters.add(followChar);
                            break;
                        } else {
                            if (!addFirstMap(characters, followChar)) {
                                break;
                            }
                            index += 1;
                        }
                    }
                    if (index == candidate.length()) {
                        Character production = productions.get(i);
                        if (c != production) {
                            getFollowMap(production);
                            Set<Character> fet = followmap.get(production);
                            characters.addAll(fet);
                        }
                    }
                }
            }
        }
    }

    private static void initTable() {
        int n = 0, m = productions.size();
        for (int i = 0; i < productions.size(); i++) {
            String candidate = candidates.get(i);
            Character production = productions.get(i);
            productionMap.put(production, i);
            char[] chars = candidate.toCharArray();
            for (char c : chars) {
                if (!Character.isUpperCase(c) && c != '@' && c != '|') {
                    candidateMap.put(c, n);
                    n += 1;
                }
            }
        }
        candidateMap.put('#', n);
        n = n + 1;
        System.out.println(productionMap);
        System.out.println(candidateMap);
        table = new String[m][n];
        for (int i = 0; i < m; i++) {
            Character production = productions.get(i);
            String strcandidates = candidates.get(i);
            String[] strings = strcandidates.split("\\|");
            Set<Character> firsts = firstmap.get(production);
            if (firsts.contains('@')) {
                Set<Character> follows = followmap.get(production);
                for (Character character : follows) {
                    int j = candidateMap.get(character);
                    table[i][j] = production + "->@";
                }
            }
            for (String s : strings) {
                char c = s.charAt(0);
                if (c == '@') {
                    continue;
                }
                if (Character.isUpperCase(c)) {
                    for (Character character : firsts) {
                        int j = candidateMap.get(character);
                        table[i][j] = production + "->" + s;
                    }
                } else if (firsts.contains(c)) {
                    int j = candidateMap.get(c);
                    table[i][j] = production + "->" + s;
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
            for (Map.Entry<Character, Integer> entry : candidateMap.entrySet()) {
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
        stack.push('E');
        boolean flag = true;
        while (flag) {
            Character c = stack.pop();
            if(c=='#') {
                if (c == queue.poll()) {
                    flag = false;
                } else {
                    System.out.println("ERROR");
                    return;
                }
            } else if (!Character.isUpperCase(c)) {
                if (c != queue.poll()) {
                    System.out.println("ERROR");
                    return;
                }
            }else {
                int i = productionMap.get(c);
                int j = candidateMap.get(queue.peek());
                String s = table[i][j];
                if (s == null) {
                    System.out.println("ERROR");
                    return;
                }
                s=s.substring(s.indexOf('>')+1);
                if(!s.equals("@")){
                    char[] toCharArray = s.toCharArray();
                    for(int k = toCharArray.length-1;k>=0;k--){
                        stack.push(toCharArray[k]);
                    }
                }
            }
        }
        System.out.println("SUCCESS");
    }
}
