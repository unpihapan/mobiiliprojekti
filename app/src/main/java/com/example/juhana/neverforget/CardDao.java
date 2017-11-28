package com.example.juhana.neverforget;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CardDao {

    @Query("SELECT * FROM Card")
    List<Card> getCards();

    @Query("SELECT * FROM Card WHERE id = :id")
    List<Card> getCardById(int id);

    @Query("UPDATE Card SET question = :question, answer = :answer WHERE id = :id")
    void updateCardById(String question, String answer, int id);

    @Query("SELECT * FROM Card WHERE list_id = :list_id")
    List<Card> getCardsByListId(int list_id);

    @Insert
    void InsertCards(Card... cards);

    @Update
    void UpdateCards(Card... cards);

    @Delete
    void Delete(Card card);
}
