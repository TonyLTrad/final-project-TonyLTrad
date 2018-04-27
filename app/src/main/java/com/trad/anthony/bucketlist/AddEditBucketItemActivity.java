package com.trad.anthony.bucketlist;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.trad.anthony.bucketlist.BucketListActivity.UID;
import static com.trad.anthony.bucketlist.BucketListActivity.editBucketItem;
import static com.trad.anthony.bucketlist.BucketListActivity.bucketItemToEdit;
import static com.trad.anthony.bucketlist.LoginSignUpActivity.databaseReference;

public class AddEditBucketItemActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Date date = new Date();
    private EditText nameEditText, descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_bucket_item);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        Button addEditButton = findViewById(R.id.addEdit_Button);
        nameEditText = (EditText)findViewById(R.id.name_EditText);
        descriptionEditText = (EditText)findViewById(R.id.description_EditText);
        final Button datePickBtn = (Button)findViewById(R.id.selectedDate_Button);

        if (editBucketItem){
            addEditButton.setText("Save");
            myToolbar.setTitle("Edit");
        }

        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Calendar calendar = Calendar.getInstance();
        final DateFormat txtDate = DateFormat.getDateInstance();
        final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                date = calendar.getTime();
                datePickBtn.setText(txtDate.format(calendar.getTime()));
            }
        };

        datePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editBucketItem){
                    calendar.setTime(bucketItemToEdit.date);
                }
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(AddEditBucketItemActivity.this, datePicker, mYear, mMonth, mDay).show();
            }
        });


        if(editBucketItem){
            date = bucketItemToEdit.date;
            datePickBtn.setText(txtDate.format(bucketItemToEdit.date));
            nameEditText.setText(bucketItemToEdit.name);
            descriptionEditText.setText(bucketItemToEdit.description);
        }else{
            datePickBtn.setText(txtDate.format(new Date()));
        }
    }

    private Marker markerPosition;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(editBucketItem && bucketItemToEdit.userSelectedLocation){

            LatLng pos = new LatLng(bucketItemToEdit.latitude, bucketItemToEdit.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 10));
            markerPosition = mMap.addMarker(new MarkerOptions().position(pos).title("Location"));

        }else{
            LatLng dubaiLoc = new LatLng(25.206052, 55.269554);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dubaiLoc, 10));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if(markerPosition != null){
                    markerPosition.remove();
                }
                markerPosition = mMap.addMarker(new MarkerOptions().position(point).title("Location"));
            }
        });
    }

    private boolean validateForm() {
        String email = nameEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            nameEditText.setError("Required.");
            return false;
        } else {
            nameEditText.setError(null);
            return true;
        }
    }

    public void addBucketItem(View view){

        if (!validateForm()) {
            return;
        }

        BucketListActivity.BucketItem tempBucketItem = new BucketListActivity.BucketItem();

        tempBucketItem.name = nameEditText.getText().toString();
        tempBucketItem.description = descriptionEditText.getText().toString();
        tempBucketItem.date = date;
        tempBucketItem.complete = false;

        if(markerPosition != null){
            tempBucketItem.userSelectedLocation = true;
            tempBucketItem.longitude = markerPosition.getPosition().longitude;
            tempBucketItem.latitude = markerPosition.getPosition().latitude;
        }else{
            tempBucketItem.userSelectedLocation = false;
        }

        if(editBucketItem){
            databaseReference.child(UID).child("bucket").child(bucketItemToEdit.itemReference).setValue(tempBucketItem);
        }
        else
        {
            databaseReference.child(UID).child("bucket").push().setValue(tempBucketItem);
        }
        finish();
    }
}
