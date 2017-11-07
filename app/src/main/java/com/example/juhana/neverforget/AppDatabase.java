package com.example.juhana.neverforget;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Card.class, CardList.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract CardDao cardDao();
    public abstract CardListDao cardListDao();

    public static AppDatabase getDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class,
                    "never-forget-db").allowMainThreadQueries()
                    .fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }
}
