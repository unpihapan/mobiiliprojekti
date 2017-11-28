package com.example.juhana.neverforget;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private TextView tv1;
    private ImageView arrow;
    ListView cardListView;
    public ArrayList<HashMap<String, String>> cardListArray = new ArrayList<>();

    private AppDatabase db;
    private SimpleAdapter simpleAdapter;

    View headerView;
    ObjectAnimator fade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.main_activity_title));
            setSupportActionBar(toolbar);
        }

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

        // Inflate the header view and add to listview
        headerView = LayoutInflater.from(this)
                .inflate(R.layout.main_activity_header, cardListView, false);
        cardListView.addHeaderView(headerView, null ,false);

        // prepare the fade in/out animator
        final TextView tvHeaderTitle = (TextView)findViewById(R.id.headerTitle);
        fade =  ObjectAnimator.ofFloat(tvHeaderTitle, "alpha", 0f, 1f);
        fade.setInterpolator(new DecelerateInterpolator());
        fade.setDuration(400);

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
                String name =  cardListArray.get(pos-1).get("CardListName");
                if (isFabOpen){
                    animateFAB();
                }
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("EXTRA_MESSAGE", name);
                startActivity(intent);
            }


        });

        // list view scroll listener
        cardListView.setOnScrollListener(new AbsListView.OnScrollListener(){
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // we make sure the list is not null and empty, and the header is visible
                if (view != null && view.getChildCount() > 0 && firstVisibleItem == 0) {

                    // if we scrolled more than 16dps, we hide the content and display the title
                    if (view.getChildAt(0).getTop() < -dpToPx(headerView.getHeight())) {
                        toggleHeader(false, false);
                    } else {
                        toggleHeader(true, true);
                    }
                } else {
                    toggleHeader(false, false);
                }
            }
            public void onScrollStateChanged(AbsListView view, int scrollState) {

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
                Intent intent = new Intent(MainActivity.this, getFromDB.class );
                startActivity(intent);
                animateFAB();
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
                .setIcon(R.drawable.ic_add_black)
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
        TextView tvHeaderContent = (TextView)findViewById(R.id.headerContent);
        tvHeaderContent.setText(getString(R.string.main_activity_header_content, cardLists.size()));
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


    // header toggle
    private void toggleHeader(boolean visible, boolean force) {
        if ((force && visible) || (visible && headerView.getAlpha() == 0f)) {
            fade.setFloatValues(headerView.getAlpha(), 1f);
            fade.start();
            if (android.os.Build.VERSION.SDK_INT >= 21){
                findViewById(R.id.appBarLayout).setElevation(0);
            }
        } else if (force || (!visible && headerView.getAlpha() == 1f)){
            fade.setFloatValues(headerView.getAlpha(), 0f);
            fade.start();
            if (android.os.Build.VERSION.SDK_INT >= 21){
                findViewById(R.id.appBarLayout).setElevation(10);
            }
        }
        // Toggle the visibility of the title.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(!visible);
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int)(dp * (displayMetrics.densityDpi / 160f));
    }
}
