package com.example.juhana.neverforget;

/**
 * Created by Juhana on 31.10.2017.
 */

public class Card {

    private int id;
    private CardList cardList;
    private int list_id;
    private String question;
    private String answer;

    Card() { }

    public Card(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public int getId() { return id;}
    public void setId(int id) { this.id = id;}

    public String getQuestion() { return question;}
    public void setQuestion(String question) {this.question = question;}

    public String getAnswer() { return answer;}
    public void setAnswer(String answer) {this.answer = answer;}


    public CardList getCardlist() {
        return cardList;
    }

    public void setCardList(CardList cardList) {
        this.cardList = cardList;
    }
}
