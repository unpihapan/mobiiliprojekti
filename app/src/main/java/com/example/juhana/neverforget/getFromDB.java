package com.example.juhana.neverforget;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Elmeri on 28.11.2017.
 */

public class getFromDB extends AppCompatActivity {

    ArrayList<HashMap<String, String>> lists = new ArrayList<>();
    ListView dbListView;
    SimpleAdapter simpleAdapter;
    private String url = "http://home.tamk.fi/~e4jpiesa/APIs/nfApi.php?action=";

    private String listName = null;
    private int id = 0;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getfromdb);
        String title = "Download cardlist";
        setTitle(title);


        dbListView = (ListView) findViewById(R.id.lv_db);
        db = AppDatabase.getDatabase(getApplicationContext());


        // populate listView
        simpleAdapter = new SimpleAdapter(this, lists, R.layout.getfromdb_activitylist_item,
                new String[]{"ListName", "CardCount", "Index"},
                new int[]{R.id.tvListName, R.id.tvCardCount, R.id.tvIndex});
        dbListView.setAdapter(simpleAdapter);
        dbListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                id = Integer.valueOf(lists.get(pos).get("List_id"));
                listName = lists.get(pos).get("ListName");

                showWarningDialog();

            }
        });

        new getListsFrom_db().execute();


    }

    private class getListsFrom_db extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            // background thread
            return webRequest.doWebRequest(url + "getCardlists");
        }

        protected void onPostExecute(String jsonResponse) {
            // UI thread
            // Parse data from JSON
            JSONArray json_array = null;
            JSONObject json_object = null;
            String name = null;
            int cardCount;
            int list_id;
            try {
                json_array = new JSONArray(jsonResponse);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            for (int i = 0; i < json_array.length(); i++) {
                try {
                    json_object = new JSONObject(String.valueOf(json_array.get(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                try {
                    name = json_object.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                try {
                    cardCount = json_object.getInt("cardcount");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                try {
                    list_id = json_object.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                HashMap<String, String> tempHashMap = new HashMap<>();
                tempHashMap.put("ListName", name);
                tempHashMap.put("Index", String.valueOf(i + 1));
                tempHashMap.put("CardCount", "Cards in List: " + String.valueOf(cardCount));
                tempHashMap.put("List_id", String.valueOf(list_id));
                lists.add(tempHashMap);
            }

            simpleAdapter.notifyDataSetChanged();
        }
    }


    private class getCardsFrom_List extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            // background thread
            return webRequest.doWebRequest(url + "getCardsOfCardlist&id=" + id);
        }

        protected void onPostExecute(String jsonResponse) {
            // UI thread
            // Parse card data from lists
            JSONArray json_array = null;
            JSONObject json_object = null;
            String question = null;
            String answer = null;
            String newListName = listName;
            ArrayList<HashMap<String, String>> cardsArrayList = new ArrayList<>();
            try {
                json_array = new JSONArray(jsonResponse);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            if (json_array.length() != 0) {
                for (int i = 0; i < json_array.length(); i++) {
                    try {
                        json_object = new JSONObject(String.valueOf(json_array.get(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }

                    try {
                        question = json_object.getString("question");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                    try {
                        answer = json_object.getString("answer");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                    HashMap<String, String> cardsHashMap = new HashMap<>();
                    cardsHashMap.put("Question", question);
                    cardsHashMap.put("Answer", answer);
                    cardsArrayList.add(cardsHashMap);

                }

                int invalidCount = 1;
                while (!listNameValid(newListName)) {
                    newListName = listName + " (" + invalidCount++ + ")";
                }
                CardList cardList = new CardList(newListName);
                db.cardListDao().InsertCardLists(cardList);
                int cardlistID = db.cardListDao().getIdByCardListName(newListName);

                for (int i = 0; i < cardsArrayList.size(); i++) {
                    db.cardDao().InsertCards(new Card(cardlistID, cardsArrayList.get(i).get("Question"), cardsArrayList.get(i).get("Answer")));
                }
            } else {
                CardList cardList = new CardList(newListName);
                db.cardListDao().InsertCardLists(cardList);
            }
            finish();
            return;
        }
    }

    // checks if given name is unique
    private boolean listNameValid(String name) {
        return db.cardListDao().getIdByCardListName(name) == 0;
    }


    // Create list dialog
    public void showWarningDialog() {



        // dialog builder
        final AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("Download " + listName + " cardlist?")
                .setMessage(listName + " cardlist will be added to your phone's memory")
                .setIcon(R.drawable.ic_download_black)
                .setPositiveButton(R.string.action_done, null)
                .setNegativeButton(R.string.action_cancel, null)
                .show();

        // done button
        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new getCardsFrom_List().execute();
                d.cancel();
            }
        });

        // cancel button
        d.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // close
                d.cancel();
            }
        });
    }
}