package com.example.juhana.neverforget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
    private SimpleAdapter simpleAdapter;

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

        // populate listView
        simpleAdapter = new SimpleAdapter(this, cardListArray, R.layout.main_activity_list_item,
                new String[]{"ListIndex", "CardListName", "CardCount"},
                new int[]{R.id.tvIndex_main, R.id.tvCardListName, R.id.tvCardCount});
        cardListView.setAdapter(simpleAdapter);

        // list item click -> edit card
        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                String name = cardListArray.get(pos).get("CardListName");
                if (isFabOpen){
                    animateFAB();
                }
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("EXTRA_MESSAGE", name);
                startActivity(intent);
            }


        });
    }
    @Override
    public void onResume(){
        super.onResume();
        refresh();
        // Nautetaan/piilotetaan apunuoli
        tv1 = (TextView)findViewById(R.id.tv1);
        arrow = (ImageView)findViewById(R.id.arrow);
        if (!cardListArray.isEmpty()){
            tv1.setVisibility(View.GONE);
            arrow.setVisibility(View.GONE);
        }
        else {
            tv1.setVisibility(View.VISIBLE);
            arrow.setVisibility(View.VISIBLE);
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

        // view
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        // dialog builder
        final AlertDialog d = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle(R.string.dialog_create_list_title)
                .setMessage(R.string.dialog_create_list_message)
                .setPositiveButton(R.string.action_done, null)
                .setNegativeButton(R.string.action_cancel, null)
                .show();

        final EditText listNameEditText = (EditText) dialogView.findViewById(R.id.edit1);
        final Toast emptyName = Toast.makeText(MainActivity.this,
                R.string.dialog_create_list_validation_text, Toast.LENGTH_SHORT);

        // done button
        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String listName = listNameEditText.getText().toString();

                // require name if left empty
                String newListName = listName;
                if (newListName.isEmpty()){
                    emptyName.show();
                }

                // if list name is already taken, rename as <listName (n)>
                else{
                    int invalidCount = 1;
                    while (!listNameValid(newListName)){
                        newListName = listName + " (" + invalidCount++ + ")";
                    }

                    // save cardList
                    CardList cardList = new CardList(newListName);
                    db.cardListDao().InsertCardLists(cardList);

                    if (isFabOpen){
                        animateFAB();
                    }


                    // open AddActivity
                    Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                    intent.putExtra("EXTRA_MESSAGE", newListName);
                    intent.putExtra("CARDLIST_ID", db.cardListDao().getIdByCardListName(newListName));
                    startActivity(intent);

                    d.cancel();
                }
            }
        });

        // cancel button
        d.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // close
                d.cancel();
            }
        });
    }

    public void refresh (){
        cardListArray.clear();
        simpleAdapter.notifyDataSetChanged();
        List<CardList> cardLists = db.cardListDao().getCardLists();
        for (int i = 0; i < cardLists.size(); i++){
            HashMap<String, String> tempHashMap = new HashMap<>();
            tempHashMap.put("ListIndex", String.valueOf(i + 1));
            tempHashMap.put("CardListName", cardLists.get(i).getName());
            tempHashMap.put("CardCount", "Cards in List: " + db.cardDao().getCardsByListId(cardLists.get(i).getId()).size());
            cardListArray.add(tempHashMap);

        }

    }

    // checks if given name is unique
    private boolean listNameValid(String name){
        return db.cardListDao().getIdByCardListName(name) == 0;
    }
}
