package com.example.juhana.neverforget;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class CardList {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;

    @Ignore
    CardList() { }

    public CardList(String name) {
        this.name = name;
    }

    // setters
    public void setId(int id) { this.id = id;}
    public void setName(String name) {this.name = name;}

    // getters
    public int getId() { return id;}
    public String getName() { return name;}

    /*
    public void newCard(String question, String ansver) {
        Card card = new Card();
        card.setId(cards.size());
        card.setQuestion(question);
        card.setAnswer(ansver);
        cards.add(card);
    }*/
}
