package com.example.mobilelab;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodsViewHolder> {
    private List<Good> goods;
    private int rowLayout;

    public GoodsAdapter(List<Good> goods, int rowLayout) {
        this.goods = goods;
        this.rowLayout = rowLayout;
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
        Context context = holder.itemView.getContext();
        Picasso.with(context)
                .load(goods.get(position).getImg())
                .placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
                .into(holder.image);
        holder.title.setText(goods.get(position).getTitle());
        holder.price.setText(goods.get(position).getPrice());
        holder.date.setText(goods.get(position).getDate());
        holder.place.setText(context.getString(R.string.place, goods.get(position).getPlace()));
    }

    @Override
    public int getItemCount() {
        return goods.size();
    }

    public void updateGoods(final List<Good> goods) {
        this.goods = goods;
        notifyDataSetChanged();
    }

    public static class GoodsViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView place;
        private TextView date;
        private TextView price;
        private ImageView image;

        GoodsViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.good_image);
            title = v.findViewById(R.id.title);
            date = v.findViewById(R.id.date);
            place = v.findViewById(R.id.description);
            price = v.findViewById(R.id.price);
        }
    }
}