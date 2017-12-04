package com.example.juhana.neverforget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import link.fls.swipestack.SwipeStack;

public class GameActivity extends AppCompatActivity implements SwipeStack.SwipeStackListener, View.OnClickListener {

    private FloatingActionButton mFab;
    private ArrayList<String> questions;
    private ArrayList<String> answers;
    private SwipeStack mSwipeStack;
    private SwipeStackAdapter mAdapter;
    TextView textView;
    TextView currentCard;

    private String title;
    private AppDatabase db;
    private int list_id;
    private Animation turn_1, turn_2;
    private boolean isTurned;
    boolean shouldExecuteOnResume;


    private int answersRight;
    private int totalCards;
    private int cardPosition;
    List<Card> cardsInList;
    Toast answerResultToast;

    long seed = System.nanoTime();

    String url = "http://home.tamk.fi/~e4jpiesa/APIs/nfApi.php?action=saveCardlistAndQuestions&";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //component init
        mSwipeStack = (SwipeStack) findViewById(R.id.swipeStack);
        mFab = (FloatingActionButton) findViewById(R.id.fabAdd);
        textView = (TextView)findViewById(R.id.textViewCard);
        currentCard = (TextView)findViewById(R.id.textView);
        isTurned = false;
        shouldExecuteOnResume = false;
        answersRight = 0;
        cardPosition = 1;
        turn_1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.turn_1);
        turn_2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.turn_2);


        // db
        db = AppDatabase.getDatabase(getApplicationContext());

        // set title based on tapped cardlist
        Intent intent = getIntent();
        title = intent.getStringExtra("LIST_NAME");
        setTitle(title);

        mFab.setOnClickListener(this);


        questions = new ArrayList<>();
        answers = new ArrayList<>();

        answerResultToast = Toast.makeText(GameActivity.this, null, Toast.LENGTH_SHORT);


        mAdapter = new SwipeStackAdapter(questions);
        mSwipeStack.setAdapter(mAdapter);
        mSwipeStack.setListener(this);
        getCardsFromList();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(shouldExecuteOnResume){
            getUpdatedCardsFromList(false);
        } else {
            shouldExecuteOnResume = true;
        }

    }

    // get cards based on the name of the selected list
    private void getCardsFromList() {
        list_id = db.cardListDao().getIdByCardListName(title);
        setTitle(db.cardListDao().getCardListById(list_id).getName());
        cardsInList = db.cardDao().getCardsByListId(list_id);
        Collections.shuffle(cardsInList,new Random(System.nanoTime()));
        if (cardsInList.size() == 0) {
            currentCard.setText(R.string.no_cards_in_list);


        } else {
            for (int i = 0; i < cardsInList.size(); i++) {
                questions.add(cardsInList.get(i).getQuestion());
                answers.add(cardsInList.get(i).getAnswer());

            }
            totalCards = questions.size();
            currentCard.setText(getString(R.string.current_card, cardPosition, totalCards));

        }
    }

    //updating cards, when coming back from edit mode
    private void getUpdatedCardsFromList(boolean shuffle) {
        questions.clear();
        answers.clear();
        title = db.cardListDao().getCardListById(list_id).getName();
        setTitle(title);
        cardsInList = db.cardDao().getCardsByListId(list_id);
        if (shuffle) Collections.shuffle(cardsInList, new Random(System.nanoTime()));
        if ( cardsInList.size() == 0 ) {
            currentCard.setText(R.string.no_cards_in_list);
        } else {
            for (int i = 0; i < cardsInList.size(); i++) {
                questions.add(cardsInList.get(i).getQuestion());
                answers.add(cardsInList.get(i).getAnswer());

            }
            totalCards = questions.size();
            currentCard.setText(getString(R.string.current_card, cardPosition, totalCards));

            if (!isTurned) {
                setCardFontSize(questions.get(mSwipeStack.getCurrentPosition()).length());
                textView.setText(questions.get(mSwipeStack.getCurrentPosition()));
            } else if (isTurned) {
                setCardFontSize(answers.get(mSwipeStack.getCurrentPosition()).length());
                textView.setText(answers.get(mSwipeStack.getCurrentPosition()));
            }
        }

    }

    //handle card turn when button is pressed
    @Override
    public void onClick(View v) {
        if (v.equals(mFab)) {
            mSwipeStack.startAnimation(turn_1);
            turn_1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (isTurned) {
                        setCardFontSize(questions.get(mSwipeStack.getCurrentPosition()).length());
                        if (questions.get(mSwipeStack.getCurrentPosition()).length() < 25) {
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);
                        } else if ( questions.get(mSwipeStack.getCurrentPosition()).length() < 50) {
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f);
                        }
                        textView.setText(questions.get(mSwipeStack.getCurrentPosition()));
                        isTurned = false;
                    } else if (!isTurned) {
                        setCardFontSize(answers.get(mSwipeStack.getCurrentPosition()).length());
                        if (answers.get(mSwipeStack.getCurrentPosition()).length() < 25) {
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);
                        } else if ( answers.get(mSwipeStack.getCurrentPosition()).length() < 50) {
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f);
                        }
                        textView.setText(answers.get(mSwipeStack.getCurrentPosition()));

                        isTurned = true;
                    }
                    mSwipeStack.startAnimation(turn_2);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_game_activity, menu);
        return true;
    }


    //menu buttons actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete:
                showConfirmDeleteDialog();
                return true;
            case R.id.action_edit:
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                intent.putExtra("LIST_NAME", title);
                intent.putExtra("LIST_ID", db.cardListDao().getIdByCardListName(title));
                intent.putExtra("EDIT_MODE", true);
                startActivity(intent);
                return true;
            case R.id.action_upload:
                showUploadConfirmationDialog(totalCards > 0);
                createUrlParamsStr();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setCardFontSize(int length) {
        if (length <= 25) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);
        } else if ( length > 25  && length <= 50) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35f);
        } else if ( length > 50  && length <= 100) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f);
        } else if ( length > 100 ) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f);

        }
    }

    //show results dialog
    public void showResultsDialog() {

        // dialog builder
        final AlertDialog d = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_end_title)
                .setMessage(getString(R.string.dialog_end_message, answersRight, totalCards))
                .setPositiveButton(R.string.dialog_end_positive_button, null)
                .setNegativeButton(R.string.dialog_end_negative_button, null)
                .show();
        d.setCanceledOnTouchOutside(false);

        // done button
        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardPosition = 1;
                currentCard.setText(getString(R.string.current_card, cardPosition, totalCards));
                mSwipeStack.resetStack();

                //Shuffletaan kyssärit ja vastaukset
                getUpdatedCardsFromList(true);
                answersRight = 0;
                d.cancel();
            }
        });

        // cancel button
        d.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                d.cancel();
                finish();
            }
        });
    }

    @Override
    public void onViewSwipedToRight(int position) {
        if (cardPosition < totalCards) {
            cardPosition++;
        }
        answersRight++;
        isTurned = false;
        currentCard.setText(getString(R.string.current_card, cardPosition, totalCards));
        answerResultToast.setText(R.string.view_swiped_right);
        answerResultToast.show();
        if(questions.size() - 1 == position) {
            showResultsDialog();
        }
    }

    @Override
    public void onViewSwipedToLeft(int position) {
        if (cardPosition < totalCards) {
            cardPosition++;
        }
        isTurned = false;
        currentCard.setText(getString(R.string.current_card, cardPosition, totalCards));
        answerResultToast.setText(R.string.view_swiped_left);
        answerResultToast.show();
        if(questions.size() - 1 == position) {
            showResultsDialog();
        }
    }

    @Override
    public void onStackEmpty() {
    }

    public class SwipeStackAdapter extends BaseAdapter {

        private List<String> mData;

        public SwipeStackAdapter(List<String> data) {
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.card, parent, false);
            }

            System.out.println("DATA SET");

            textView = (TextView) convertView.findViewById(R.id.textViewCard);
            setCardFontSize(mData.get(position).length());
            textView.setText(mData.get(position));
            return convertView;
        }
    }

    // edit question confirmation dialog
    public void showConfirmDeleteDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.dialog_delete_list_title, title));
        dialogBuilder.setMessage(R.string.dialog_delete_list_message);
        dialogBuilder.setIcon(R.drawable.ic_delete_black);
        dialogBuilder.setPositiveButton(R.string.dialog_delete_list_positive_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                db.cardListDao().Delete(db.cardListDao().getCardListById(list_id));
                finish();
            }
        });
        dialogBuilder.setNegativeButton(R.string.dialog_delete_list_negative_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    // upload dialog
    public void showUploadConfirmationDialog(boolean enoughCards){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        if (enoughCards){
            dialogBuilder.setTitle(getString(R.string.dialog_upload_list_title, title));
            dialogBuilder.setMessage(R.string.dialog_upload_list_message);
            dialogBuilder.setIcon(R.drawable.ic_upload_black);

            dialogBuilder.setPositiveButton(R.string.action_upload, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    new UploadList().execute();
                }
            });
            dialogBuilder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //pass
                }
            });
        }
        else {
            dialogBuilder.setTitle(R.string.dialog_upload_list_title_rejected);
            dialogBuilder.setMessage(R.string.dialog_upload_list_message_rejected);
            dialogBuilder.setIcon(R.drawable.ic_info_outline_black);

            dialogBuilder.setNegativeButton(R.string.action_close, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //pass
                }
            });
        }
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


    private void createUrlParamsStr(){
        StringBuilder urlParams = new StringBuilder();
        urlParams.append(url);
        urlParams.append("listname='").append(title).append("'&values=");
        for (int i = 0; i < cardsInList.size(); i++){
            urlParams.append("((SELECT+LAST_INSERT_ID()),'")
                    .append(cardsInList.get(i).getQuestion())
                    .append("','")
                    .append(cardsInList.get(i).getAnswer())
                    .append("')");
            if (i < cardsInList.size()-1){
                urlParams.append(",");
            }
        }
        url = urlParams.toString().replace(" ", "+");
    }

    // card list upload async task
    private class UploadList extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params){
            // background thread
            return webRequest.doWebRequest(url);
        }
        protected void onPostExecute(String jsonResponse){
            // UI thread
            String response = jsonResponse.trim().equals("\"OK\"") ? "List uploaded successfully" : "List upload failed";
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
        } 
    }
}
