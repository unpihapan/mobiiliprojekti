package com.example.juhana.neverforget;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = CardList.class, parentColumns = "id", childColumns = "list_id"))
public class Card {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int list_id;
    private String question;
    private String answer;

    @Ignore
    Card() { }

    public Card(int list_id, String question, String answer) {
        this.list_id = list_id;
        this.question = question;
        this.answer = answer;
    }

    // setters
    public void setId(int id) { this.id = id;}
    void setQuestion(String question) {this.question = question;}
    void setAnswer(String answer) {this.answer = answer;}
    public void setList_id(int list_id) {this.list_id = list_id;}

    // getters
    public int getId() { return id;}
    String getQuestion() { return question;}
    String getAnswer() { return answer;}
    int getList_id(){return list_id;}
}
