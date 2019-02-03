package com.example.victor.temarlije;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Notification extends Fragment {

    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final String MSG_LENGTH_KEY = "msg_length";
    private static final int RC_PHOTO_PICKER =  2;
    public static final int RC_SIGN_IN = 1;
    Calendar currentTime;

    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;
    private boolean edit = false;
    private Messages mMessage = null;

    private String mUsername;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListner;
    private FirebaseAuth mFirbaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;


    private List<Messages> messagesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotificationAdapter mAdapter;

    private String groupID = ""+1;

    public static Notification newInstance(String groupID) {
        Notification fragment = new Notification();
        fragment.groupID = groupID;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        setHasOptionsMenu(true);

        final DatabaseHandler db = new DatabaseHandler(getContext());

        //configuring firebase
        mUsername = ANONYMOUS;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirbaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");
        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");

        // Initialize references to views
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mPhotoPickerButton = (ImageButton) view.findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) view.findViewById(R.id.messageEditText);
        mSendButton = (Button) view.findViewById(R.id.sendButton);


        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);


        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click
                currentTime = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("hh:mm: a");
                String strDate = mdformat.format(currentTime.getTime());
                if (edit == false){
                    Messages message = new Messages(mUsername, mMessageEditText.getText().toString(),strDate, null, groupID);
                    //mMessagesDatabaseReference.push().setValue(message);
                    db.addMessage(message);
                    messagesList.add(message);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(),"here2", Toast.LENGTH_SHORT).show();
                    // Clear input box
                    mMessageEditText.setText("");
                }
                else if (!(mMessage.getTextMessage().equals(mMessageEditText.getText().toString()))) {
                    mMessage.setTextMessage(mMessageEditText.getText().toString());
                    mMessage.setTextTime("edited "+strDate);
                    db.updateMessage(mMessage);
                    mAdapter.notifyDataSetChanged();
                    mMessageEditText.setText("");
                    Toast.makeText(getContext(),"text edited", Toast.LENGTH_SHORT).show();
                    edit = false;
                    mMessage = null;
                }
                else {
                    mMessageEditText.setText("");
                }

            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    // user is signed in
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // user is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.PhoneBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put(MSG_LENGTH_KEY, DEFAULT_MSG_LENGTH_LIMIT);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);
        fetchConfig();



        recyclerView = (RecyclerView) view.findViewById(R.id.rec_notification);

        mAdapter = new NotificationAdapter(messagesList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        // set the adapter
        recyclerView.setAdapter(mAdapter);
        // row click listener
        recyclerView.addOnItemTouchListener(new StudentTouchListener(getContext(), recyclerView, new StudentTouchListener.ClickListener() {
            @Override
            public void onClick(View view,final int position) {
                final Messages message = messagesList.get(position);

                final DatabaseHandler db = new DatabaseHandler(getContext());
                final String[] items = {"edit","delete"};

                //show up selected item in dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        String selectedText = items[pos];
                        if(selectedText == "edit"){
                            edit = true;
                            mMessage = message;
                            mMessageEditText.setText(message.getTextMessage());
                        }
                        else if (selectedText == "delete"){
                            db.deleteMessage(""+message.getID());
                            Toast.makeText(getContext(), "Message deleted", Toast.LENGTH_SHORT).show();
                            messagesList.remove(position);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });

                AlertDialog alertDialogObject = dialogBuilder.create();
                alertDialogObject.show();

            }

            @Override
            public void onLongClick(View view, final int position) {

            }
        }));

        MessageData();
        return view;
    }

    private void MessageData() {

        DatabaseHandler db = new DatabaseHandler(getContext());

        ArrayList<Messages> messages = db.getAllMessages(groupID);
        //Add item in listview
        for (int i=0; i < messages.size();i++ ){
            messagesList.add(messages.get(i));
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                //sign out
                AuthUI.getInstance().signOut(getContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            if(requestCode == Activity.RESULT_OK){
                Toast.makeText(getContext(),"Sign in!",Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getContext(),"Sign in cancelled", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            } else if (requestCode == RC_PHOTO_PICKER && resultCode == Activity.RESULT_OK){
                Uri selectedImageUri = data.getData();
                Toast.makeText(getContext(),"it is here"+selectedImageUri, Toast.LENGTH_SHORT).show();
                StorageReference photoRef =
                        mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
                photoRef.putFile(selectedImageUri).addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                                FriendlyMessage friendlyMessage =
                                        new FriendlyMessage(null, mUsername, downloadUrl.toString());
                                mMessagesDatabaseReference.push().setValue(friendlyMessage);

                            }
                        });
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mFirbaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mAuthStateListener != null) {
            mFirbaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
        //mMessageAdapter.clear();
        messagesList.clear();
        mAdapter.notifyDataSetChanged();
    }

    private void onSignedInInitialize(String username){
        mUsername = username;
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup(){
        mUsername = ANONYMOUS;
        //mMessageAdapter.clear();
        messagesList.clear();
        mAdapter.notifyDataSetChanged();
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener(){
        if(mChildEventListner == null) {
            mChildEventListner = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //Messages message = dataSnapshot.getValue(Messages.class);
                    //mMessageAdapter.add(message);
                    //messagesList.add(message);
                    mAdapter.notifyDataSetChanged();
                    }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) { }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListner);
        }
    }

    private void detachDatabaseReadListener() {
        if(mChildEventListner != null){
            mMessagesDatabaseReference.removeEventListener(mChildEventListner);
            mChildEventListner = null;
        }
    }

    public void fetchConfig(){
        long cacheExpiration = 3600;

        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()){
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFirebaseRemoteConfig.activateFetched();
                        applyRetrievedLengthLimit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error fetching config", e);
                        applyRetrievedLengthLimit();
                    }
                });
    }

    private void applyRetrievedLengthLimit(){
        Long msg_length = mFirebaseRemoteConfig.getLong(MSG_LENGTH_KEY);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(msg_length.intValue())});
        Log.d(TAG, MSG_LENGTH_KEY + " = " + msg_length);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.notification_menu, menu);
    }

}