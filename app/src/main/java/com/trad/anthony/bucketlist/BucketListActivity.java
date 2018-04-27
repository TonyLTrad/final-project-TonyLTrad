package com.trad.anthony.bucketlist;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static com.trad.anthony.bucketlist.LoginSignUpActivity.firebaseAuth;
import static com.trad.anthony.bucketlist.LoginSignUpActivity.databaseReference;

public class BucketListActivity extends AppCompatActivity {

    public static class BucketItem{
        public String itemReference;
        public String name;
        public String description;
        public Date date;
        public boolean complete;
        public boolean userSelectedLocation;
        public double latitude;
        public double longitude;
    }

    final ArrayList<BucketItem> bucketList = new ArrayList<>();
    final CustomAdapter bucketListAdapter = new CustomAdapter(bucketList);
    public static boolean editBucketItem = false;
    public static BucketItem bucketItemToEdit;
    ListView listViewBucketItems;
    public static String UID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_list);

        listViewBucketItems = (ListView) findViewById(R.id.listViewBucketItems);
        listViewBucketItems.setAdapter(bucketListAdapter);

        UID = firebaseAuth.getCurrentUser().getUid();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editBucketItem = false;
                startActivity(new Intent(BucketListActivity.this, AddEditBucketItemActivity.class));
            }
        });

        databaseReference.child(UID).child("bucket").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bucketList.clear();
                for (DataSnapshot imageSnapshot: dataSnapshot.getChildren()) {

                    BucketItem tempBucketItem = imageSnapshot.getValue(BucketItem.class);
                    tempBucketItem.itemReference = imageSnapshot.getKey();
                    bucketList.add(tempBucketItem);
                }
                orderItems();
                bucketListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        listViewBucketItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editBucketItem = true;
                bucketItemToEdit = bucketList.get(position);
                startActivity(new Intent(BucketListActivity.this, AddEditBucketItemActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(BucketListActivity.this, LoginSignUpActivity.class));
            finish();
        }
        return true;
    }

    public void orderItems(){

        Collections.sort(bucketList, new Comparator<BucketItem>() {
            public int compare(BucketItem o1, BucketItem o2) {
                if (o1.date == null || o2.date == null) return 0;
                else return o1.date.compareTo(o2.date);
            }
        });

        for (int j = 0; j < bucketList.size(); j++) {
            for (int i = bucketList.size()-1; 0 <= i; i--) {
                if(bucketList.get(i).complete){
                    bucketList.add(bucketList.get(i));
                    bucketList.remove(i);
                }
            }
        }
    }

    final DateFormat txtDate = DateFormat.getDateInstance();;

    public class CustomAdapter extends BaseAdapter {

        private ArrayList<BucketItem> tempBucketList;

        public CustomAdapter(ArrayList<BucketItem> temp){
            tempBucketList = temp;
        }

        @Override
        public int getCount() {
            return tempBucketList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            view = getLayoutInflater().inflate(R.layout.list_custom,null);
            TextView name = (TextView) view.findViewById(R.id.name_TextView);
            TextView date = (TextView) view.findViewById(R.id.date_TextView);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            final CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkBox);

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bucketList.get(i).complete = checkbox.isChecked();
                    databaseReference.child(UID)
                            .child("bucket")
                            .child(bucketList.get(i).itemReference)
                            .setValue(bucketList.get(i));
                }
            });

            if(tempBucketList.get(i).complete) checkBox.setChecked(true);
            else checkBox.setChecked(false);

            date.setText(txtDate.format(tempBucketList.get(i).date));
            name.setText(tempBucketList.get(i).name);

            return view;
        }
    }

}
