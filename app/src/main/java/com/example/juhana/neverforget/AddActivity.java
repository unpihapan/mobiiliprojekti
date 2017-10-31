package com.example.juhana.neverforget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class AddActivity extends AppCompatActivity {

    ListView CardListView;
    ArrayList<HashMap<String, String>> cardList = new ArrayList<>();
    SimpleAdapter simpleAdapter;
    Button button;
    EditText edit1;
    EditText edit2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Intent intent = getIntent();
        setTitle(intent.getStringExtra("EXTRA_MESSAGE"));

        CardListView = (ListView)findViewById(R.id.lv1);
        edit1 = (EditText)findViewById(R.id.et_sidea);
        edit2 = (EditText)findViewById(R.id.et_sideb);
        button = (Button)findViewById(R.id.btn_add);


        HashMap<String, String> testidata = new HashMap<>();
        testidata.put("Diana", "3214 Broadway Avenue");
        testidata.put("Tyga", "343 Rack City Drive");
        testidata.put("Rich Homie Quan", "111 Everything Gold Way");
        testidata.put("Donna", "789 Escort St");
        testidata.put("Bartholomew", "332 Dunkin St");
        testidata.put("Eden", "421 Angelic Blvd");

        HashMap<String, String> testidata2 = new HashMap<>();
        testidata2.put("Diana", "3214 Broadway Avenue");
        testidata2.put("Tyga", "343 Rack City Drive");

       // cardList.add(testidata);
       // cardList.add(testidata2);

        simpleAdapter = new SimpleAdapter(this, cardList, R.layout.list_card_item,
                new String[] {"Question", "Answer"},
                new int[]{R.id.tvquestion, R.id.tvanswer});
        CardListView.setAdapter(simpleAdapter);


        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                if (!TextUtils.isEmpty(edit1.getText()) && !TextUtils.isEmpty(edit2.getText())) {
                    HashMap<String, String> carddata = new HashMap<>();
                    carddata.put("Question", edit1.getText().toString());
                    carddata.put("Answer", edit2.getText().toString());
                    cardList.add(carddata);
                    simpleAdapter.notifyDataSetChanged();
                    edit1.setText("");
                    edit2.setText("");

                }



            }
        });

        CardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                HashMap selectedItem = cardList.get(pos);
                showEditCardDialog(selectedItem);
            }


        });




    }
    public void showEditCardDialog(final HashMap<String, String> mapdata) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
       // LayoutInflater inflater = this.getLayoutInflater();
       // final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
      //  dialogBuilder.setView(dialogView);

      //  final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setMessage(R.string.dialog_edit_card_body);
        dialogBuilder.setPositiveButton(R.string.dialog_edit_positive_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                edit1.setText(mapdata.get("Question"));
                edit2.setText(mapdata.get("Answer"));

                //do something with edt.getText().toString();
            }
        });
        dialogBuilder.setNegativeButton(R.string.dialog_edit_negative_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}
