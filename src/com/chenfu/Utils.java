package com.chenfu;

import java.util.ArrayList;
import java.util.HashMap;

public class Utils {

    public static void initProductionMap(ArrayList<Production> productions,HashMap<Character, Integer> lmap,HashMap<Character, Integer> rmap){
        int n=0;
        for(int i=0;i<productions.size();i++){
            Production production = productions.get(i);
            lmap.put(production.getL(),i);
            String L = production.getR();
            char[] chars = L.toCharArray();
            for (char c : chars) {
                if (isTerminal(c) && c != '@' && c != '|') {
                    if (!rmap.containsKey(c)) {
                        rmap.put(c, n);
                        n += 1;
                    }
                }
            }
        }
        rmap.put('#',n);
    }

    public static boolean isTerminal(char c){
        return !Character.isUpperCase(c);
    }

    public static boolean isNonterminal(char c) {
        return Character.isUpperCase(c);
    }
}
