package com.example.juhana.neverforget;

/**
 * Created by Juhana on 31.10.2017.
 */

public class CardList {

    private int id;
    private String name;
    private int cardCount = 0;

    CardList() { }

    public CardList(String name) {
        this.name = name;
    }

    public int getId() { return id;}
    public void setId(int id) { this.id = id;}

    public String getName() { return name;}
    public void setName(String name) {this.name = name;}

    public int getCardCount(){return cardCount;}

    public void newCard(String question, String ansver){
        Card card = new Card();
        card.setId(cardCount);
        card.setQuestion(question);
        card.setAnswer(ansver);
        cardCount++;
    }
}
