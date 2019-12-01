package com.example.mobilelab;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_good_details);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.details));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        extractGoodFromIntent();
    }

    private void extractGoodFromIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("good_title") && intent.hasExtra("good_place") &&
                intent.hasExtra("good_date") && intent.hasExtra("good_price") &&
                intent.hasExtra("good_img_url")) {
            String goodsTitle = intent.getStringExtra("good_title");
            String goodsDate = intent.getStringExtra("good_date");
            String goodsPlace = intent.getStringExtra("good_place");
            String goodsPrice = intent.getStringExtra("good_price");
            String imageName = intent.getStringExtra("good_img_url");

            displayGood(goodsTitle, goodsDate, goodsPlace, goodsPrice, imageName);
        }
    }

    private void displayGood(String goodsTitle, String goodsDate, String goodsPlace, String goodsPrice, String imageName) {
        final TextView title = findViewById(R.id.title_detailed);
        final TextView date = findViewById(R.id.date_detailed);
        final TextView place = findViewById(R.id.place_detailed);
        final TextView price = findViewById(R.id.price_detailed);
        final ImageView imageView = findViewById(R.id.img_detailed);
        final int TARGET_WIDTH = 200;
        final int TARGET_HEIGHT = 200;
        Picasso.get()
                .load(imageName)
                .placeholder(R.drawable.default_img)
                .resize(TARGET_WIDTH, TARGET_HEIGHT)
                .into(imageView);
        title.append(goodsTitle);
        date.append(goodsDate);
        place.append(goodsPlace);
        price.append(goodsPrice);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        final Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}
