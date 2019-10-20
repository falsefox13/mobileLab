package com.example.mobilelab;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodsViewHolder> {
    private List<Good> goods;
    private int rowLayout;
    private Context context;
    private static final String BASE_URL = "http://bowling-iot.pp.ua";

    GoodsAdapter(List<Good> goods, int rowLayout, Context context) {
        this.goods = goods;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    static class GoodsViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout goodsLayout;
        TextView title;
        TextView place;
        TextView date;
        TextView price;
        ImageView image;

        GoodsViewHolder(View v) {
            super(v);
            goodsLayout = v.findViewById(R.id.goods_layout);
            image = v.findViewById(R.id.good_image);
            title = v.findViewById(R.id.title);
            date = v.findViewById(R.id.date);
            place = v.findViewById(R.id.description);
            price = v.findViewById(R.id.price);
        }
    }

    @NonNull
    @Override
    public GoodsAdapter.GoodsViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new GoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoodsViewHolder holder, final int position) {
        String image_url = BASE_URL + goods.get(position).getImg();
        Picasso.with(context)
                .load(image_url)
                .placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
                .into(holder.image);
        holder.title.setText(goods.get(position).getTitle());
        holder.price.setText(String.format("%s$", goods.get(position).getPrice()));
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        int MILLISEC = 1000;
        Date date = new Date(goods.get(position).getDate() * MILLISEC);
        holder.date.setText(formatter.format(date));
        holder.place.setText(context.getString(R.string.place, goods.get(position).getPlace()));
    }

    @Override
    public int getItemCount() {
        return goods.size();
    }
}