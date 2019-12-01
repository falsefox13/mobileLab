package com.example.mobilelab;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewItemActivity extends AppCompatActivity {
    private static final int IMAGE_REQUEST = 1;
    private TextInputLayout titleField;
    private TextInputLayout placeField;
    private TextInputLayout dateField;
    private TextInputLayout priceField;
    private DatePickerDialog picker;
    private ProgressBar progressBar;
    private String imgDownloadLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.new_item));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initFields();
    }

    private void initFields() {
        titleField = findViewById(R.id.title_wrapper);
        placeField = findViewById(R.id.place_wrapper);
        dateField = findViewById(R.id.date_wrapper);
        progressBar = findViewById(R.id.progress_bar);
        findViewById(R.id.new_image_button).setOnClickListener(v -> openImage());
        initButton();
        initDatePicker();
        priceField = findViewById(R.id.price_wrapper);
    }

    private void initButton() {
        findViewById(R.id.btn_create_item).setOnClickListener(v -> {
            final String title = Objects.requireNonNull(titleField.getEditText()).getText().toString();
            final String name = Objects.requireNonNull(placeField.getEditText()).getText().toString();
            final String date = Objects.requireNonNull(dateField.getEditText()).getText().toString();
            final String price = Objects.requireNonNull(priceField.getEditText()).getText().toString();
            addItem(title, name, date, price, imgDownloadLink);
        });
    }

    private void initDatePicker() {
        final EditText date = dateField.getEditText();
        Objects.requireNonNull(date).setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            picker = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) ->
                    date.setText(String.format(getString(R.string.date_format),
                            dayOfMonth, monthOfYear + 1, year1)), year, month, day);
            picker.show();
        });
    }

    private void addItem(final String title, final String place, final String date, final String price, final String imgPath) {
        if (!validate(title, place, date, price)) {
            return;
        }
        final GoodsService service = getApplicationEx().getApiService();
        Good good = new Good(title, place, date, price, imgPath);
        Call<Good> call = service.addGood(good);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<Good>() {
            @Override
            public void onResponse(Call<Good> call, Response<Good> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    openMainActivity();
                }
            }

            @Override
            public void onFailure(Call<Good> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Snackbar.make(findViewById(R.id.item_view), R.string.post_failed, Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void openImage() {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            new UploadImageTask().execute(data.getData());
        }
    }

    private void showImage(final String photoUrl) {
        final int TARGET_WIDTH = 200;
        final int TARGET_HEIGHT = 200;
        if (!photoUrl.isEmpty()) {
            Picasso.get()
                    .load(photoUrl)
                    .resize(TARGET_WIDTH, TARGET_HEIGHT)
                    .into((ImageView) findViewById(R.id.item_image));
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = Objects.requireNonNull(this).getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public boolean validate(final String title, final String place, final String date, final String price) {
        boolean valid = true;
        if (Utils.validateString(title)) {
            titleField.setError(null);
        } else {
            titleField.setError(getString(R.string.title_error));
            valid = false;
        }

        if (Utils.validateString(place)) {
            placeField.setError(null);
        } else {
            placeField.setError(getString(R.string.place_error));
            valid = false;
        }

        if (!date.isEmpty()) {
            dateField.setError(null);
        } else {
            dateField.setError(getString(R.string.date_error));
            valid = false;
        }

        if (Utils.validatePrice(price)) {
            priceField.setError(null);
        } else {
            priceField.setError(getString(R.string.price_error));
            valid = false;
        }

        return valid;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        openMainActivity();
        return true;
    }

    private void openMainActivity() {
        final Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
    }

    private App getApplicationEx() {
        return ((App) Objects.requireNonNull(this.getApplication()));
    }

    private class UploadImageTask extends AsyncTask<Uri, Void, Void> {
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        protected Void doInBackground(Uri... blobName) {
            ServiceAccountCredentials credentials;
            try {
                credentials = ServiceAccountCredentials.fromStream(getResources().openRawResource(R.raw.bowlingsite));
                Storage storage = StorageOptions.newBuilder().setProjectId("bowlingsite")
                        .setCredentials(credentials)
                        .build()
                        .getService();
                final Bucket bucket = storage.get("www.bowling-iot.pp.ua");
                final String imgPath = "img/" + blobName[0].getLastPathSegment() + "." + getFileExtension(blobName[0]);
                final BlobId blobId = BlobId.of(bucket.getName(), imgPath);
                final InputStream imageStream = getContentResolver().openInputStream(blobName[0]);
                final BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
                final Blob blob = storage.create(blobInfo, imageStream);
                imgDownloadLink = blob.getMediaLink();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            showImage(imgDownloadLink);
            progressBar.setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}
