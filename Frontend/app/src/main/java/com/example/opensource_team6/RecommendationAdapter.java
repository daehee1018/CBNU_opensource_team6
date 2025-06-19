package com.example.opensource_team6;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opensource_team6.data.Food;

import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {
    private List<RecommendationResultActivity.FoodDistance> items;
    private Context context;

    public RecommendationAdapter(List<RecommendationResultActivity.FoodDistance> items, Context context) {
        this.items = items;
        this.context = context;
    }

    public void updateList(List<RecommendationResultActivity.FoodDistance> newList) {
        this.items = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommendation_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecommendationResultActivity.FoodDistance item = items.get(position);
        holder.foodNameTextView.setText(item.name);
        holder.scoreTextView.setText(String.format("추천 점수: %.2f", item.score));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FoodDetailActivity.class);

            // 수정된 부분: items.get(position).food를 넘김
            Food food = items.get(position).food;  // 이 줄을 추가
            intent.putExtra("food_name", food.getName());  // 이름만 전달 (방법 B)

            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView, scoreTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
        }
    }
}
