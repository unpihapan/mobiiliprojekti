package com.example.juhana.neverforget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private TextView tv1;
    private ImageView arrow;
    ListView cardListView;
    public ArrayList<HashMap<String, String>> cardListArray = new ArrayList<>();

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // component init
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);
        cardListView = (ListView)findViewById(R.id.lvMain);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        // db
        db = AppDatabase.getDatabase(getApplicationContext());

        // fetch data
        List<CardList> cardLists = db.cardListDao().getCardLists();
        for (int i = 0; i < cardLists.size(); i++){
            HashMap<String, String> tempHashMap = new HashMap<>();
            tempHashMap.put("CardListName", (i + 1) + ". " + cardLists.get(i).getName());
            tempHashMap.put("CardCount", "Cards in List: " + db.cardDao().getCardsByListId(cardLists.get(i).getId()).size());
            cardListArray.add(tempHashMap);
            Log.d("qwerty", String.valueOf(cardLists.get(i).getId()));
        }

        // populate listView
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, cardListArray, R.layout.main_activity_list_item,
                new String[]{"CardListName", "CardCount"},
                new int[]{R.id.tvCardListName, R.id.tvCardCount});
        cardListView.setAdapter(simpleAdapter);

        // Jos ViewListissä on jotain, ei "apu nuolta" tarvita
        tv1 = (TextView)findViewById(R.id.tv1);
        arrow = (ImageView)findViewById(R.id.arrow);
        if (!cardListArray.isEmpty()){
            tv1.setVisibility(View.GONE);
            arrow.setVisibility(View.GONE);
        }
    }

    // floating action button click listeners
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:
                // show inner fabs
                animateFAB();
                break;
            case R.id.fab1:
                // start gameactivity
                Intent intent = new Intent(this, GameActivity.class);
                startActivity(intent);
                break;
            case R.id.fab2:
                // show create list dialog
                showCreateListDialog();
                break;
        }
    }

    // animation for floating action buttons
    public void animateFAB(){

        if(isFabOpen){
            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;

        } else {
            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }

    // Create list dialog
    public void showCreateListDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText listNameEditText = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle(R.string.dialog_create_list_title);
        dialogBuilder.setMessage(R.string.dialog_create_list_message);

        // done button
        dialogBuilder.setPositiveButton(R.string.action_done, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String listName = listNameEditText.getText().toString();

                // close floating action buttons
                animateFAB();

                // Luodaan cardList olio
                CardList cardList = new CardList(listName);

                // save cardList
                db.cardListDao().InsertCardLists(cardList);

                // populate listView
                HashMap<String, String> tempHashMap = new HashMap<>();
                List<CardList> cardLists = db.cardListDao().getCardLists();
                for (int i = 0; i < cardLists.size(); i++){
                    tempHashMap.put("CardListName", (i + 1) + ". " + cardLists.get(i).getName());
                    tempHashMap.put("CardCount", "Cards in List: " + db.cardDao().getCardsByListId(cardLists.get(i).getId()).size());
                }
                cardListArray.add(tempHashMap);

                // Piilotetaan "apunuoli"
                if (!cardListArray.isEmpty()){
                    tv1.setVisibility(View.GONE);
                    arrow.setVisibility(View.GONE);
                }

                // Avataan AddActivity
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                intent.putExtra("EXTRA_MESSAGE", listName);
                intent.putExtra("CARDLIST_ID", db.cardListDao().getIdByCardListName(listName));
                startActivity(intent);

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

}
