package com.example.juhana.neverforget;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CardListDao {

    @Query("Select * FROM CardList")
    List<CardList> getCardLists();

    @Query("Select * FROM CardList WHERE id = :id")
    CardList getCardListById(int id);

    @Query("Select id FROM CardList WHERE name = :name")
    int getIdByCardListName(String name);

    @Insert
    void InsertCardLists(CardList... cardlists);

    @Update
    void UpdateCardLists(CardList... cardlists);

    @Delete
    void Delete(CardList cardlists);
}
