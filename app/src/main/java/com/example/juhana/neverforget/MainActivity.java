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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private TextView tv1;
    private ImageView arrow;
    //cardListArray
    public ArrayList<HashMap<String, String>> cardListArray = new ArrayList<>();
    private SimpleAdapter simpleAdapter;
    public int counter = 0; // Laskee listojen määrän


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        // Jos ViewListissä on jotain, ei "apu nuolta" tarvita
        tv1 = (TextView)findViewById(R.id.tv1);
        arrow = (ImageView)findViewById(R.id.arrow);
        if (cardListArray.isEmpty() == false){
            tv1.setVisibility(View.GONE);
            arrow.setVisibility(View.GONE);
        }


        ListView cardListView;
        cardListView = (ListView)findViewById(R.id.lvMain);

        simpleAdapter = new SimpleAdapter(this, cardListArray, R.layout.main_activity_list_item,
                new String[] {"CardListName", "CardCount"},
                new int[]{R.id.tvCardListName, R.id.tvCardCount});
        cardListView.setAdapter(simpleAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:

                animateFAB();
                break;
            case R.id.fab1:

                Log.d("Raj", "Fab 1");
                break;
            case R.id.fab2:
                showChangeLangDialog();
                Log.d("Raj", "Fab 2");
                break;
        }
    }

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
            Log.d("Raj","open");

        }
    }

    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("Enter name");
        dialogBuilder.setMessage("Enter text above");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String message = edt.getText().toString();

                // Luodaan cardList olio
                CardList cardList = new CardList();
                counter++;
                cardList.setName(message);

                HashMap<String, String> tempHashMap = new HashMap<>();
                tempHashMap.put("CardListName", counter + ". " + cardList.getName());
                tempHashMap.put("CardCount", "Cards in List: " + cardList.getCardCount());
                cardListArray.add(tempHashMap);

                if (cardListArray.isEmpty() == false){
                    tv1.setVisibility(View.GONE);
                    arrow.setVisibility(View.GONE);
                }

                // Avataan AddActivity
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                intent.putExtra("EXTRA_MESSAGE", message);
                startActivity(intent);
                //do something with edt.getText().toString();

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
