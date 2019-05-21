package com.example.presentator.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.presentator.R;
import com.example.presentator.adapter.NewsAdapter;
import com.example.presentator.model.entities.Gift;
import com.example.presentator.model.entities.News;
import com.example.presentator.model.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewsFeed extends AppCompatActivity {

    private RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        initFields();
        initRecyclerView();



/*
        Gift gift = new Gift("Bicycle", "Fast mountain bicycle", "asdf", "https://www.google.com/favicon.icon");
        String curUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.child("gifts").child(curUserUid).push().setValue(gift);
        db.child("friends").child(curUserUid).push().setValue(curUserUid);
        db.child("friends").child(curUserUid).push().setValue(curUserUid);
        db.child("friends").child(curUserUid).push().setValue(curUserUid);*/

//        startObserveFriendEvents();
    }


    private void initFields() {
        // TODO вынеси все поля сюда
        db = FirebaseDatabase.getInstance().getReference();
        newsRecyclerView = findViewById(R.id.news_recycler_view);
    }

    private void initRecyclerView() {
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter();
        newsRecyclerView.setAdapter(newsAdapter);
    }

    private void startObserveFriendEvents() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db.child("friends").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String friendUid = d.getValue(String.class);
                    subscribeFriend(friendUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NewsFeed", "startObserveFriendsEvents: database error occurred", databaseError.toException());
            }
        });
    }

    private void subscribeFriend(final String friendUid) {
        db.child("users").child(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User friend = dataSnapshot.getValue(User.class);
                subscribeForFriend(friend, friendUid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NewsFeed", "subscribeFriend: database error occurred", databaseError.toException());
            }
        });

    }

    private void subscribeForFriend(final User friend, final String friendUid) {
        db.child("gifts").child(friendUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Gift gift = dataSnapshot.getValue(Gift.class);
                newsAdapter.addItem(new News(gift, friend));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
