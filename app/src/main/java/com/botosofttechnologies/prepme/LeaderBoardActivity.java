package com.botosofttechnologies.prepme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.botosofttechnologies.prepme.Model.LeaderBoardModel;
import com.botosofttechnologies.prepme.ViewHolder.LeaderBoardViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;

public class LeaderBoardActivity extends AppCompatActivity {

    //Firebase
    FirebaseDatabase database  = FirebaseDatabase.getInstance();;
    DatabaseReference users = database.getReference().child("users");
    DatabaseReference Leaderboard = database.getReference().child("leader_board");
    FirebaseRecyclerAdapter<LeaderBoardModel, LeaderBoardViewHolder> adapter ;

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseUser User = mAuth.getCurrentUser();
    static final String userId = User.getUid();

    //View
    RecyclerView recycler_menu;

    String $name, $star;

    private ActionBar toolbar;
    TextView number, star, name;
    ImageView icon;

    int leadersCount;

    private Typeface header, subheading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);


        toolbar = getSupportActionBar();
        toolbar.setTitle("LeaderBoard");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recycler_menu = (RecyclerView)findViewById(R.id.recycler_leaderboard);
        recycler_menu.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(LeaderBoardActivity.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recycler_menu.setLayoutManager(layoutManager);

        header = Typeface.createFromAsset(getAssets(), "fonts/heading.ttf");
        subheading = Typeface.createFromAsset(getAssets(), "fonts/subheading1.ttf");

        initUi();


        loadMenu();
    }

    private void initUi() {

        number = (TextView) findViewById(R.id.number);
        star = (TextView) findViewById(R.id.star);
        name = (TextView) findViewById(R.id.name);


        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.child(userId).child("lbKey").getValue();
                $name = String.valueOf(dataSnapshot.child(userId).child("name").getValue());
                $star = String.valueOf(dataSnapshot.child(userId).child("currentExamTotal").child("total").getValue());

                if($star.equals("null")){
                    $star = "0";
                }
                String lbcount = String.valueOf(dataSnapshot.child(userId).child("lbKey").getValue());
                int score = 0;
                try {
                    score = Integer.parseInt(String.valueOf(dataSnapshot.child(userId).child("currentExamTotal").child("total").getValue()));
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                Leaderboard.child(lbcount).child("star").setValue(score);


                name.setText($name);
                star.setText($star);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // you will need to query the database based on scores
        final Query query = Leaderboard.orderByChild("star");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get total leaders. Let's say total leader is 5 persons
                int total = (int) dataSnapshot.getChildrenCount();
                // let's say userB is actually 2nd in the list
                int i = 0;
                // loop through dataSnapshot
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String usersName = String.valueOf(childSnapshot.child("name").getValue());
                    String stars = String.valueOf(childSnapshot.child("star").getValue());
                    if (usersName.equals($name) && stars.equals($star)) {
                        // the list is ascending, when finally reach userB, i = 3
                        int userPlace = total - i;
                        number.setText(String.valueOf(userPlace));
                        break;
                    } else {
                        i++;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        number.setTypeface(subheading);
        star.setTypeface(subheading);
        name.setTypeface(subheading);




    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

    }



    private void loadMenu() {

        Query query = FirebaseDatabase.getInstance()
                .getReference().child("leader_board").orderByChild("star");


        FirebaseRecyclerOptions<LeaderBoardModel> options =
                new FirebaseRecyclerOptions.Builder<LeaderBoardModel>()
                        .setQuery(query, new SnapshotParser<LeaderBoardModel>() {
                            @NonNull
                            @Override
                            public LeaderBoardModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new LeaderBoardModel(
                                        snapshot.child("star").getValue().toString(),
                                        snapshot.child("name").getValue().toString()
                                );
                            }
                        })
                        .build();


        adapter = new FirebaseRecyclerAdapter<LeaderBoardModel, LeaderBoardViewHolder>(options) {
            @NonNull
            @Override
            public LeaderBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.leader_board_item, parent, false);
                return new LeaderBoardViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final LeaderBoardViewHolder viewHolder, @SuppressLint("RecyclerView") final int position, @NonNull final LeaderBoardModel model) {

                Leaderboard.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        leadersCount = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));

                        viewHolder.star.setText(model.getStar());
                        viewHolder.name.setText(model.getName());
                        viewHolder.number.setText(String.valueOf(leadersCount - position));


                        viewHolder.star.setTypeface(subheading);
                        viewHolder.name.setTypeface(subheading);
                        viewHolder.number.setTypeface(subheading);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }

        };

        adapter.notifyDataSetChanged(); //Refresh data if changed
        recycler_menu.setAdapter(adapter);





    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LeaderBoardActivity.this, NavigationActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(LeaderBoardActivity.this, NavigationActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }


}
