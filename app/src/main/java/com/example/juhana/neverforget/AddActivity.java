package com.example.juhana.neverforget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    ListView CardListView;

    ArrayList<HashMap<String, String>> cards = new ArrayList<>();
    ArrayList<HashMap<String, String>> cardsToAdd = new ArrayList<>();
    ArrayList<HashMap<String, String>> cardsToUpdate = new ArrayList<>();
    SimpleAdapter simpleAdapter;
    Button button_add;
    Button button_cancel;
    Button button_delete;
    Button button_save;
    EditText questionEditText;
    EditText answerEditText;
    TextView cardCount;
    public int currentPos;
    int cardListId;
    int from;
    int list_id;
    String hidden_card_id;
    private ArrayList<Integer> cardsToDelete;
    int deletePos;
    int updatePos;
    String title2;


    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Intent intent = getIntent();
        from = intent.getIntExtra("FROM", from);
        title2 = intent.getStringExtra("EXTRA_MESSAGE");
        if (from == 1) {
            String title = getString(R.string.add_activity_title_edit, intent.getStringExtra("EXTRA_MESSAGE"));
            setTitle(title);
        } else {
            String title = getString(R.string.add_activity_title, intent.getStringExtra("EXTRA_MESSAGE"));
            setTitle(title);
        }

        cardsToDelete = new ArrayList<>();



        // db
        db = AppDatabase.getDatabase(getApplicationContext());

        if(from == 1) {
            list_id = db.cardListDao().getIdByCardListName(title2);
            List<Card> cardsInList = db.cardDao().getCardsByListId(list_id);
             for (int i = 0; i < cardsInList.size(); i++){
                 HashMap<String, String> cardData = new HashMap<>();
                 Log.d("test", cardsInList.get(i).getQuestion() );
                 cardData.put("Id", String.valueOf(cardsInList.get(i).getId()));
                 cardData.put("Question", cardsInList.get(i).getQuestion());
                 cardData.put("Answer", cardsInList.get(i).getAnswer());
                 cardData.put("Index", String.valueOf(i + 1));
                 cards.add(cardData);
            }
            for ( int i= 0; i < cards.size(); i++) {
                Log.d("size", String.valueOf(cards.size()));
                Log.d("cards", cards.get(i).get("Question"));
            }
        }

        // component init
        CardListView = (ListView)findViewById(R.id.lv1);
        questionEditText = (EditText)findViewById(R.id.et_sidea);
        answerEditText = (EditText)findViewById(R.id.et_sideb);
        button_add = (Button)findViewById(R.id.btn_add);
        button_cancel = (Button)findViewById(R.id.btn_cancel);
        button_delete = (Button)findViewById(R.id.btn_delete);
        button_save = (Button)findViewById(R.id.btn_save);
        cardCount = (TextView)findViewById(R.id.tvCardsInList);

        cardCount.setText(getString(R.string.add_activity_cards_in_list, cards.size()));
        cardListId = intent.getIntExtra("CARDLIST_ID", 0);

        // populate listView
        simpleAdapter = new SimpleAdapter(this, cards, R.layout.list_card_item,
                new String[] {"Question", "Answer"},
                new int[]{R.id.tvquestion, R.id.tvanswer});
        CardListView.setAdapter(simpleAdapter);

        /*
        * OnClick Listeners
        * */
        // button add
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validation
                if (!TextUtils.isEmpty(questionEditText.getText()) && !TextUtils.isEmpty(answerEditText.getText())) {
                    if (from == 1) {
                        HashMap<String, String> newCardData = new HashMap<>();
                        newCardData.put("Question", questionEditText.getText().toString());
                        newCardData.put("Answer", answerEditText.getText().toString());
                        newCardData.put("Index", String.valueOf(cardsToAdd.size() + 1));
                        cardsToAdd.add(newCardData);
                    }
                    HashMap<String, String> cardData = new HashMap<>();
                    cardData.put("Question", questionEditText.getText().toString());
                    cardData.put("Answer", answerEditText.getText().toString());
                    cardData.put("Index", String.valueOf(cards.size() + 1));
                    cards.add(cardData);
                    simpleAdapter.notifyDataSetChanged();

                    questionEditText.setText("");
                    answerEditText.setText("");
                    cardCount.setText(getString(R.string.add_activity_cards_in_list, cards.size()));
                    hideKeyboard(AddActivity.this, findViewById(R.id.addActivity));
                    questionEditText.clearFocus();
                    answerEditText.clearFocus();
                    Toast successMsgToast = Toast.makeText(AddActivity.this,
                            R.string.add_activity_card_add_success, Toast.LENGTH_LONG);
                    successMsgToast.show();
                }
                else{
                    Snackbar validationMsgSnackBar = Snackbar.make(findViewById(R.id.addActivity),
                            R.string.add_activity_edit_text_validation, Snackbar.LENGTH_SHORT);
                    validationMsgSnackBar.show();
                }
            }
        });

        // button cancel edit
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                questionEditText.setText("");
                answerEditText.setText("");
                button_add.setVisibility(View.VISIBLE);
                button_cancel.setVisibility(View.INVISIBLE);
                button_delete.setVisibility(View.INVISIBLE);
                button_save.setVisibility(View.GONE);

            }
        });

        // button delete card
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (from == 1 ) {
                    if (hidden_card_id == null) {
                        cardsToAdd.remove(deletePos);
                    } else {
                        cardsToDelete.add(Integer.parseInt(hidden_card_id));
                    }
                }

                cards.remove(currentPos);
                simpleAdapter.notifyDataSetChanged();
                questionEditText.setText("");
                answerEditText.setText("");
                cardCount.setText(getString(R.string.add_activity_cards_in_list, cards.size()));
                button_add.setVisibility(View.VISIBLE);
                button_cancel.setVisibility(View.INVISIBLE);
                button_delete.setVisibility(View.INVISIBLE);
                button_save.setVisibility(View.GONE);

            }
        });

        // button save edit
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from == 1) {
                    if (hidden_card_id == null) {
                        HashMap<String, String> newCardData = new HashMap<>();
                        newCardData.put("Question", questionEditText.getText().toString());
                        newCardData.put("Answer", answerEditText.getText().toString());
                        newCardData.put("Index", String.valueOf(cardsToUpdate.size() + 1));
                        cardsToAdd.set(updatePos, newCardData);
                    } else {
                        HashMap<String, String> newCardData = new HashMap<>();
                        newCardData.put("Question", questionEditText.getText().toString());
                        newCardData.put("Answer", answerEditText.getText().toString());
                        newCardData.put("Index", String.valueOf(cardsToUpdate.size() + 1));
                        newCardData.put("Id", hidden_card_id);
                        cardsToUpdate.add(newCardData);
                    }
                }
                HashMap<String, String> cardData = new HashMap<>();
                cardData.put("Question", questionEditText.getText().toString());
                cardData.put("Answer", answerEditText.getText().toString());
                cardData.put("Index", String.valueOf(currentPos + 1));



                cards.set(currentPos, cardData);
                simpleAdapter.notifyDataSetChanged();
                questionEditText.setText("");
                answerEditText.setText("");
                button_add.setVisibility(View.VISIBLE);
                button_cancel.setVisibility(View.INVISIBLE);
                button_delete.setVisibility(View.INVISIBLE);
                button_save.setVisibility(View.GONE);
            }
        });

        // list item click -> edit card
        CardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                deletePos = (cardsToAdd.size() - (cards.size() - pos));
                updatePos = (cardsToAdd.size() - (cards.size() - pos));
                currentPos = pos;
                HashMap<String, String> selectedItem = cards.get(pos);
                showEditCardDialog(selectedItem);
            }


        });
        // OnClick listeners end
    }

    // navbar buttons
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_activity, menu);
        return true;
    }

    // navbar button click listeners
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            // save cards and return to main activity
            case R.id.action_edit:
                showEditListDialog();
                return true;
            case R.id.action_done:
                saveCardsToCardList();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // edit question confirmation dialog
    public void showEditCardDialog(final HashMap<String, String> mapdata) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(R.string.dialog_edit_card_body);
        dialogBuilder.setPositiveButton(R.string.dialog_edit_positive_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                button_add.setVisibility(View.GONE);
                button_cancel.setVisibility(View.VISIBLE);
                button_delete.setVisibility(View.VISIBLE);
                button_save.setVisibility(View.VISIBLE);
                questionEditText.setText(mapdata.get("Question"));
                answerEditText.setText(mapdata.get("Answer"));
                if ( from == 1 ) {
                    hidden_card_id = mapdata.get("Id");
                }
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

    public static void hideKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    // insert cards to cardList
    private void saveCardsToCardList(){
        if (from == 1) {
           for (int i = 0; i < cardsToUpdate.size(); i++) {

                //Log.d("db", cards.get(i).get("Id"));
                db.cardDao().updateCardById(cardsToUpdate.get(i).get("Question"), cardsToUpdate.get(i).get("Answer"), Integer.parseInt(cardsToUpdate.get(i).get("Id")));
            }
            for (int i = 0; i < cardsToAdd.size(); i++) {
                db.cardDao().InsertCards(new Card(cardListId, cardsToAdd.get(i).get("Question"), cardsToAdd.get(i).get("Answer")));
            }
            for (int i = 0; i < cardsToDelete.size(); i++) {
                db.cardDao().Delete(db.cardDao().getCardById(cardsToDelete.get(i)));
            }
        } else {
            for (int i = 0; i < cards.size(); i++) {
                db.cardDao().InsertCards(new Card(cardListId, cards.get(i).get("Question"), cards.get(i).get("Answer")));
            }
        }
    }

    private boolean listNameValid(String name){
        return db.cardListDao().getIdByCardListName(name) == 0;
    }

    public void showEditListDialog() {

        // view
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        // dialog builder
        final AlertDialog d = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle(getString(R.string.dialog_edit_list_title, title2))
                .setMessage(R.string.dialog_create_list_message)
                .setPositiveButton(R.string.dialog_edit_positive_button, null)
                .setNegativeButton(R.string.dialog_edit_negative_button, null)
                .show();

        final EditText listNameEditText = (EditText) dialogView.findViewById(R.id.edit1);
        final Toast emptyName = Toast.makeText(AddActivity.this,
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
                    db.cardListDao().updateListById(newListName, db.cardListDao().getIdByCardListName(title2));
                    title2 = newListName;

                    if (from == 1) {
                        String title = getString(R.string.add_activity_title_edit, newListName);
                        setTitle(title);
                    } else {
                        String title = getString(R.string.add_activity_title, newListName);
                        setTitle(title);
                    }

                    d.cancel();
                    // open AddActivity
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

}
