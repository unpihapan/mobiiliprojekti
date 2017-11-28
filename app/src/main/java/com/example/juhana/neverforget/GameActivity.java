package com.example.juhana.neverforget;


import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import link.fls.swipestack.SwipeStack;

public class GameActivity extends AppCompatActivity implements SwipeStack.SwipeStackListener, View.OnClickListener {

    private Button mButtonLeft, mButtonRight;
    private FloatingActionButton mFab;
    private ViewFlipper mViewFlipper;


    private ArrayList<String> mData;
    private ArrayList<String> mData2;
    private SwipeStack mSwipeStack;
    private SwipeStackAdapter mAdapter;
    TextView textView;
    TextView currentCard;

    private String title;
    private AppDatabase db;
    private int list_id;
    private Animation turn_1, turn_2;
    private boolean isTurned;

    private int answersRight;
    private int totalCards;
    private int cardPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mSwipeStack = (SwipeStack) findViewById(R.id.swipeStack);
        mButtonLeft = (Button) findViewById(R.id.buttonSwipeLeft);
        mButtonRight = (Button) findViewById(R.id.buttonSwipeRight);
        mFab = (FloatingActionButton) findViewById(R.id.fabAdd);
        textView = (TextView)findViewById(R.id.textViewCard);
        currentCard = (TextView)findViewById(R.id.textView);
        isTurned = false;
        answersRight = 0;
        cardPosition = 1;
        turn_1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.turn_1);
        turn_2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.turn_2);





        // db
        db = AppDatabase.getDatabase(getApplicationContext());


        // Muutetaan otsikoksi klikatun korttipinon nimi
        Intent intent = getIntent();
        title = intent.getStringExtra("EXTRA_MESSAGE");
        setTitle(title);



        mButtonLeft.setOnClickListener(this);
        mButtonRight.setOnClickListener(this);
        mFab.setOnClickListener(this);


        mData = new ArrayList<>();
        mData2 = new ArrayList<>();


        mAdapter = new SwipeStackAdapter(mData);
        mSwipeStack.setAdapter(mAdapter);
        mSwipeStack.setListener(this);




        //fillWithTestData();
        getCardsFromList();

    }

    private void fillWithTestData() {
        for (int x = 0; x < 10; x++) {
            mData.add(getString(R.string.dummy_text) + " " + (x + 1));
        }
    }

    // haetaan kortit listan nimen perusteella
    private void getCardsFromList() {
        list_id = db.cardListDao().getIdByCardListName(title);
        List<Card> cardsInList = db.cardDao().getCardsByListId(list_id);
        for (int i = 0; i < cardsInList.size(); i++){
            mData.add(cardsInList.get(i).getQuestion());
            mData2.add(cardsInList.get(i).getAnswer());

        }
        totalCards = mData.size();
        currentCard.setText(getString(R.string.current_card, cardPosition, totalCards));

    }


    @Override
    public void onClick(View v) {


        if (v.equals(mButtonLeft)) {
            mSwipeStack.swipeTopViewToLeft();
        } else if (v.equals(mButtonRight)) {
            mSwipeStack.swipeTopViewToRight();
        } else if (v.equals(mFab)) {
            mSwipeStack.startAnimation(turn_1);
            turn_1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (isTurned) {
                        textView.setText(mData.get(mSwipeStack.getCurrentPosition()));
                        isTurned = false;
                    } else if (!isTurned) {
                        textView.setText(mData2.get(mSwipeStack.getCurrentPosition()));
                        isTurned = true;
                    }
                    mSwipeStack.startAnimation(turn_2);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


            //mSwipeStack.startAnimation(turn_2);






            mAdapter.notifyDataSetChanged();

        }
    }


    public void onAnimationEnd(Animation animation) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_game_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete:
                showConfirmDeleteDialog();
                return true;
            case R.id.action_edit:
                Snackbar.make(mFab, "EDIT", Snackbar.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                intent.putExtra("EXTRA_MESSAGE", title);
                intent.putExtra("CARDLIST_ID", db.cardListDao().getIdByCardListName(title));
                intent.putExtra("FROM", 1);
                startActivity(intent);
                //return true;
            case R.id.action_upload:
                showUploadConfirmationDalog();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showCreateListDialog() {

        // view
        //LayoutInflater inflater = this.getLayoutInflater();
        //final View dialogView = inflater.inflate(R.layout.custom_dialog, null);


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
                Intent mIntent = getIntent();
                finish();
                startActivity(mIntent);
            }
        });

        // cancel button
        d.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
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
        String swipedElement = mAdapter.getItem(position);
        currentCard.setText(getString(R.string.current_card, cardPosition, totalCards));
        Toast.makeText(this, getString(R.string.view_swiped_right, swipedElement),
                Toast.LENGTH_SHORT).show();
        if(mData.size() - 1 == position) {
            Toast.makeText(this, R.string.stack_empty, Toast.LENGTH_SHORT).show();
            showCreateListDialog();
        }
    }

    @Override
    public void onViewSwipedToLeft(int position) {
        if (cardPosition < totalCards) {
            cardPosition++;
        }
        isTurned = false;
        String swipedElement = mAdapter.getItem(position);
        currentCard.setText(getString(R.string.current_card, cardPosition, totalCards));
        Toast.makeText(this, getString(R.string.view_swiped_left, swipedElement),
                Toast.LENGTH_SHORT).show();
        if(mData.size() - 1 == position) {
            Toast.makeText(this, R.string.stack_empty, Toast.LENGTH_SHORT).show();
            showCreateListDialog();
        }
    }





    @Override
    public void onStackEmpty() {
        //Toast.makeText(this, R.string.stack_empty, Toast.LENGTH_SHORT).show();
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
    public void showUploadConfirmationDalog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.dialog_upload_list_title, title));
        dialogBuilder.setMessage(R.string.dialog_upload_list_message);
        dialogBuilder.setIcon(R.drawable.ic_upload_black);
        dialogBuilder.setPositiveButton(R.string.action_upload, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // TODO: upload function
                finish();
            }
        });
        dialogBuilder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}

