package com.chenfu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class LexicalAnalysis {


    private static String[] reserveWords = {"begin","if","while","do","end"};
    private static char[] operatorOrDelimiter = {'=','+','-','*','/',';','(',')','#'};

    public static void main(String[] args) throws Exception {
        String path="C:\\Users\\chenfu\\Documents\\1.txt";
        char[] chars = LexicalAnalysis.fileRead(path);
        System.out.println("********************************************************");
        chars = LexicalAnalysis.filterResource(chars);
        System.out.println(chars);
        LexicalAnalysis.scanner(chars);
    }


    public static char[] fileRead(String path) throws Exception {
        File file = new File(path);
        FileReader reader = new FileReader(file);
        BufferedReader bReader = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String s = "";
        while ((s =bReader.readLine()) != null) {
            sb.append(s + "\n");
        }
        bReader.close();
        String str = sb.toString();
        System.out.println(str);
        String string = sb.toString();
        return string.toCharArray();
    }

    public static char[] filterResource(char[] chars){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<chars.length;i++){
            if (chars[i] == '/'&&chars[i + 1] == '/'){
                while(chars[i]!='\n'){
                    i++;
                }
            }
            if (chars[i] == '/'&&chars[i + 1] == '*'){
                i +=2;
                while(chars[i]!='*' || chars[i]!= '/'){
                    i++;
                }
                i+=2;
            }
            if (chars[i] != '\n'&&chars[i] != '\t'&&chars[i] != '\r'){
                sb.append(chars[i]);
            }else {
                sb.append(' ');
            }
        }
        return sb.toString().toCharArray();
    }

    public static boolean isReserveWord(String s){
        for (String reserveword : reserveWords) {
            if(reserveword.equals(s)){
                return true;
            }
        }
        return false;
    }

    public static boolean isOperatorOrDelimiter(char c){
        for (char ch : operatorOrDelimiter) {
            if(c==ch){
                return true;
            }
        }
        return false;
    }

    public static void scanner(char[] chars){
        StringBuilder token = new StringBuilder();
        int i = 0;
        while(i<chars.length){
            char c = chars[i];
            if(isOperatorOrDelimiter(c)){
                i++;
                System.out.println(c+" is a isOperatorOrDelimiter");
            }
            else if(Character.isDigit(c)){
                token.append(c);
                i++;
                while(i<chars.length && chars[i]!=' ' && Character.isDigit(chars[i])){
                    token.append(chars[i]);
                    i++;
                }
                System.out.println(token.toString()+" is a int num");
                token.setLength(0);
            }else if(Character.isLetter(c)){
                token.append(c);
                i++;
                while(i<chars.length && chars[i]!=' ' && Character.isLetterOrDigit(chars[i])){
                    token.append(chars[i]);
                    i++;
                }
                String stoken = token.toString();
                if(isReserveWord(stoken)){
                    System.out.println(stoken+" is a reserveword");
                }else{
                    System.out.println(stoken+" is identifier");
                }
                token.setLength(0);
            }else if(c=='<'){
                i++;
                if(i<chars.length && chars[i]=='='){
                    System.out.println("<= is a isOperatorOrDelimiter");
                    i++;
                }
                else{
                    System.out.println("< is a isOperatorOrDelimiter");
                }
            }else if(c=='>'){
                i++;
                if(i<chars.length && chars[i]=='='){
                    System.out.println(">= is a isOperatorOrDelimiter");
                    i++;
                }
                else{
                    System.out.println("> is a isOperatorOrDelimiter");
                }
            }else if(c==':'){
                i++;
                if(i<chars.length && chars[i]=='='){
                    System.out.println(":= is a isOperatorOrDelimiter");
                    i++;
                }
            }else {
                i++;
            }
        }
    }

}
