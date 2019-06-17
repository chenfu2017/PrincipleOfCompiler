package com.chenfu;

public class Production {

    private char L;
    private String R;


    public Production(char l, String r) {
        L = l;
        R = r;
    }

    public char getL() {
        return L;
    }

    public void setL(char l) {
        L = l;
    }

    public String getR() {
        return R;
    }

    public void setR(String r) {
        R = r;
    }

    @Override
    public String toString() {
        return  L +
                "->" + R ;
    }
}
