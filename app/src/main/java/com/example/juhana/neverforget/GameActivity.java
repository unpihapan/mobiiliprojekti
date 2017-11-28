package com.example.juhana.neverforget;


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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
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
    private SwipeStack mSwipeStack;
    private SwipeStackAdapter mAdapter;
    TextView textView;

    private String title;
    private AppDatabase db;
    private int list_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mSwipeStack = (SwipeStack) findViewById(R.id.swipeStack);
        mButtonLeft = (Button) findViewById(R.id.buttonSwipeLeft);
        mButtonRight = (Button) findViewById(R.id.buttonSwipeRight);
        mFab = (FloatingActionButton) findViewById(R.id.fabAdd);
        textView = (TextView)findViewById(R.id.textViewCard);

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
        }

    }


    @Override
    public void onClick(View v) {
        if (v.equals(mButtonLeft)) {
            mSwipeStack.swipeTopViewToLeft();
        } else if (v.equals(mButtonRight)) {
            mSwipeStack.swipeTopViewToRight();
        } else if (v.equals(mFab)) {
            //mData.add(getString(R.string.dummy_fab));
            //mAdapter.notifyDataSetChanged();
            //mAdapter.getItem(mSwipeStack.getCurrentPosition());
            //System.out.println(mSwipeStack.getCurrentPosition());
            //mData.set(mSwipeStack.getCurrentPosition(),"hei");
            //View mView = mSwipeStack.getTopView();
            //mViewFlipper.setAutoStart(true);
            //mViewFlipper.setFlipInterval(1000);
            //mViewFlipper.startFlipping();
            //mSwipeStack.setVisibility(View.GONE);
            System.out.println(mSwipeStack.getChildCount());
            //mSwipeStack.getChildAt(2).setVisibility(View.GONE);
            //mSwipeStack.getChildAt(2).setActivated(true);
            //mView.
            textView.setText("moi");






            mAdapter.notifyDataSetChanged();

        }
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
                return true;
            case R.id.action_upload:
                Snackbar.make(mFab, "UPLOAD", Snackbar.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewSwipedToRight(int position) {
        String swipedElement = mAdapter.getItem(position);
        Toast.makeText(this, getString(R.string.view_swiped_right, swipedElement),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewSwipedToLeft(int position) {
        String swipedElement = mAdapter.getItem(position);
        Toast.makeText(this, getString(R.string.view_swiped_left, swipedElement),
                Toast.LENGTH_SHORT).show();
    }





    @Override
    public void onStackEmpty() {
        Toast.makeText(this, R.string.stack_empty, Toast.LENGTH_SHORT).show();
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

            //TextView textViewCard = (TextView) convertView.findViewById(R.id.textViewCard);
            //TextView textViewCard2 = (TextView) convertView.findViewById(R.id.textViewCard);

            //textViewCard2.setText(mData.get(position));

            //textViewCard.setText(mData.get(position));
            System.out.println("DATA SET");
            //mViewFlipper = (ViewFlipper) convertView.findViewById(R.id.simpleViewFlipper);


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
}

