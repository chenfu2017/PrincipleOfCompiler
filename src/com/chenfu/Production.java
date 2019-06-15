package com.chenfu;

public class Production {

    public char P;
    public String L;

    public Production() {
    }

    public Production(char p, String l) {
        P = p;
        L = l;
    }

    public char getP() {
        return P;
    }

    public void setP(char p) {
        P = p;
    }

    public String getL() {
        return L;
    }

    public void setL(String l) {
        L = l;
    }

    @Override
    public String toString() {
        return "Production{ " + P +
                "->" + L+" }";
    }
}
