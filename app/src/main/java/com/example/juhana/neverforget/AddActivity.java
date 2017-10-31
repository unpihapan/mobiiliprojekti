package com.example.juhana.neverforget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    ArrayList<HashMap<String, String>> card = new ArrayList<>();
   // ArrayList<HashMap<String, HashMap<String, String>>> cardList = new ArrayList<>();
    SimpleAdapter simpleAdapter;
    Button button_add;
    Button button_cancel;
    Button button_delete;
    Button button_save;
    EditText edit1;
    EditText edit2;
    public int currentPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Intent intent = getIntent();
        setTitle(intent.getStringExtra("EXTRA_MESSAGE"));

        CardListView = (ListView)findViewById(R.id.lv1);
        edit1 = (EditText)findViewById(R.id.et_sidea);
        edit2 = (EditText)findViewById(R.id.et_sideb);
        button_add = (Button)findViewById(R.id.btn_add);
        button_cancel = (Button)findViewById(R.id.btn_cancel);
        button_delete = (Button)findViewById(R.id.btn_delete);
        button_save = (Button)findViewById(R.id.btn_save);


        simpleAdapter = new SimpleAdapter(this, card, R.layout.list_card_item,
                new String[] {"Question", "Answer"},
                new int[]{R.id.tvquestion, R.id.tvanswer});
        CardListView.setAdapter(simpleAdapter);

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                if (!TextUtils.isEmpty(edit1.getText()) && !TextUtils.isEmpty(edit2.getText())) {
                    HashMap<String, String> carddata = new HashMap<>();
                    carddata.put("Question", edit1.getText().toString());
                    carddata.put("Answer", edit2.getText().toString());

                    card.add(carddata);
                    simpleAdapter.notifyDataSetChanged();
                    edit1.setText("");
                    edit2.setText("");

                }



            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                edit1.setText("");
                edit2.setText("");
                button_add.setVisibility(View.VISIBLE);
                button_cancel.setVisibility(View.INVISIBLE);
                button_delete.setVisibility(View.INVISIBLE);
                button_save.setVisibility(View.GONE);

            }
        });

        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                card.remove(currentPos);
                simpleAdapter.notifyDataSetChanged();
                edit1.setText("");
                edit2.setText("");
                button_add.setVisibility(View.VISIBLE);
                button_cancel.setVisibility(View.INVISIBLE);
                button_delete.setVisibility(View.INVISIBLE);
                button_save.setVisibility(View.GONE);

            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                HashMap<String, String> carddata = new HashMap<>();
                carddata.put("Question", edit1.getText().toString());
                carddata.put("Answer", edit2.getText().toString());

                card.set(currentPos, carddata);
                simpleAdapter.notifyDataSetChanged();
                edit1.setText("");
                edit2.setText("");
                button_add.setVisibility(View.VISIBLE);
                button_cancel.setVisibility(View.INVISIBLE);
                button_delete.setVisibility(View.INVISIBLE);
                button_save.setVisibility(View.GONE);


            }
        });

        CardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                currentPos = pos;
                HashMap selectedItem = card.get(pos);
                showEditCardDialog(selectedItem);
            }


        });




    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_done:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

                button_add.setVisibility(View.GONE);
                button_cancel.setVisibility(View.VISIBLE);
                button_delete.setVisibility(View.VISIBLE);
                button_save.setVisibility(View.VISIBLE);
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
