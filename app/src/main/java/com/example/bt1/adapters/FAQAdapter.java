package com.example.bt1.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bt1.R;
import com.example.bt1.models.FAQItem;
import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {

    private List<FAQItem> faqList;

    public FAQAdapter(List<FAQItem> faqList) {
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_faq, parent, false);
        return new FAQViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQViewHolder holder, int position) {
        FAQItem item = faqList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    class FAQViewHolder extends RecyclerView.ViewHolder {
        TextView textQuestion, textAnswer;
        ImageView iconExpand;
        View layoutQuestion;

        FAQViewHolder(@NonNull View itemView) {
            super(itemView);
            textQuestion = itemView.findViewById(R.id.text_question);
            textAnswer = itemView.findViewById(R.id.text_answer);
            iconExpand = itemView.findViewById(R.id.icon_expand);
            layoutQuestion = itemView.findViewById(R.id.layout_question);
        }

        void bind(FAQItem item) {
            textQuestion.setText(item.getQuestion());
            textAnswer.setText(item.getAnswer());

            // Toggle visibility
            textAnswer.setVisibility(item.isExpanded() ? View.VISIBLE : View.GONE);
            iconExpand.setRotation(item.isExpanded() ? 180 : 0);

            // Click listener
            layoutQuestion.setOnClickListener(v -> {
                item.toggleExpanded();
                notifyItemChanged(getAdapterPosition());
            });
        }
    }
}
