package com.example.csc311hw1;

public class Grade {

    String name;
    String category;
    int score;

    public Grade(String name, String category, int score) {
        this.name = name;
        this.category = category;
        this.score = score;
    }


    public void setName(String name) {this.name = name;}
    public void setCategory(String category) {
        this.category = category;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {return name;}
    public String getCategory() { return category;}
    public int getScore() {
        return score;
    }

}
