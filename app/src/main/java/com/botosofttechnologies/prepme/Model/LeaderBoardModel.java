package com.botosofttechnologies.prepme.Model;

public class LeaderBoardModel {

    private String star, name;

    public  LeaderBoardModel(){

    }

    public  LeaderBoardModel(String star, String name){
        this.star = star;
        this.name = name;
    }

    public String getStar() {
        return this.star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
