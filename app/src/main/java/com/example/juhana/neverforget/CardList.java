package com.example.juhana.neverforget;

import java.util.ArrayList;
import java.util.Objects;


/**
 * Created by Juhana on 31.10.2017.
 */

public class CardList {

    private int id;
    private String name;
    private ArrayList<Object> cards = new ArrayList<Object>();
    private int cardCount = cards.size();

    CardList() { }

    public CardList(String name) {
        this.name = name;
    }

    public int getId() { return id;}
    public void setId(int id) { this.id = id;}

    public String getName() { return name;}
    public void setName(String name) {this.name = name;}

    int getCardCount(){return cardCount;}

    public void newCard(String question, String ansver) {
        Card card = new Card();
        card.setId(cards.size());
        card.setQuestion(question);
        card.setAnswer(ansver);
        cards.add(card);
        cardCount = cards.size();
    }
}
