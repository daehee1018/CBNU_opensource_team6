package com.example.opensource_team6.data;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opensource_team6.FoodDetailActivity;
import com.example.opensource_team6.R;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private List<Food> foodList;
    private Context context;

    public FoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_card, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.name.setText(food.getName());
        holder.kcal.setText("칼로리: " + (int) food.getEnergy() + " kcal");
        holder.image.setImageResource(R.drawable.sample); // 정적 이미지 사용

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FoodDetailActivity.class);
            intent.putExtra("food_name", food.getName());
            intent.putExtra("food_kcal", food.getEnergy());
            intent.putExtra("food_carbs", food.getCarbohydrate());
            intent.putExtra("food_protein", food.getProtein());
            intent.putExtra("food_fat", food.getFat());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, kcal;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.foodName);
            kcal = itemView.findViewById(R.id.foodKcal);
        }
    }
}
