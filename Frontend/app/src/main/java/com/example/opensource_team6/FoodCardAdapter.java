package com.example.opensource_team6;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.opensource_team6.RecommendationResultActivity.FoodDistance;
import com.example.opensource_team6.data.Food;

import java.util.List;

public class FoodCardAdapter extends RecyclerView.Adapter<FoodCardAdapter.ViewHolder> {

    private List<FoodDistance> foodList;

    public FoodCardAdapter(List<FoodDistance> list) {
        this.foodList = list;
    }


    public void updateList(List<FoodDistance> newList) {
        this.foodList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameView, kcalView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            nameView = view.findViewById(R.id.foodName);
            kcalView = view.findViewById(R.id.foodKcal);
        }
    }

    @Override
    public FoodCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoodCardAdapter.ViewHolder holder, int position) {
        Food food = foodList.get(position).food;
        holder.nameView.setText(food.getName());
        holder.kcalView.setText("칼로리: " + (int) food.getEnergy() + " kcal");
        holder.imageView.setImageResource(R.drawable.sample); // 임시 이미지

        // 향후 음식에 맞는 이미지 연동 가능
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }
}
