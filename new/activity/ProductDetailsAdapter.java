package com.example.bt1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ProductDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ProductDetailsAbstract> viewElementsList;
    private Context context;

    public ProductDetailsAdapter(List<ProductDetailsAbstract> list, Context context) {
        this.viewElementsList = list;
        this.context = context;
    }

    private static final int image_view = 1;
    private static final int preview_view = 2;
    private static final int spec_title_view = 3;
    private static final int spec_view = 4;
    private static final int comment_title_view = 5;
    private static final int comment_view = 6;

    public int getItemViewType(int position) {
        ProductDetailsAbstract viewEle = viewElementsList.get(position);

        if (viewEle instanceof ProductDetailsAbstract.ProductImage) {
            return image_view;
        } else if (viewEle instanceof ProductDetailsAbstract.ProductPreview) {
            return preview_view;
        } else if (viewEle instanceof ProductDetailsAbstract.ProductSpec) {
            return spec_view;
        } else if (viewEle instanceof ProductDetailsAbstract.SpecTitle) {
            return spec_title_view;
        } else if (viewEle instanceof ProductDetailsAbstract.CommentTitle) {
            return comment_title_view;
        } else {
            return comment_view;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (type) {
            case image_view:
                return new ImageViewHolder(inflater.inflate(R.layout.product_image_sublayout, parent, false));
            case preview_view:
                return new PreviewViewHolder(inflater.inflate(R.layout.product_preview_sublayout, parent, false));
            case spec_view:
                return new SpecViewHolder(inflater.inflate(R.layout.product_spec_list_sublayout, parent, false));
            case spec_title_view:
                return new SpecTitleViewHolder(inflater.inflate(R.layout.product_spec_title_sublayout, parent, false));
            case comment_title_view:
                return new CommentTitleViewHolder(inflater.inflate(R.layout.product_comment_title_sublayout, parent, false));
            default:
                return new CommentViewHolder(inflater.inflate(R.layout.product_comment_list_sublayout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductDetailsAbstract item = viewElementsList.get(position);

        if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).bind((ProductDetailsAbstract.ProductImage) item);
        } else if (holder instanceof PreviewViewHolder) {
            ((PreviewViewHolder) holder).bind((ProductDetailsAbstract.ProductPreview) item);
        }  else if (holder instanceof SpecViewHolder) {
            ((SpecViewHolder) holder).bind((ProductDetailsAbstract.ProductSpec) item);
        } else if (holder instanceof CommentViewHolder) {
            ((CommentViewHolder) holder).bind((ProductDetailsAbstract.Comment) item);
        } else if (holder instanceof SpecTitleViewHolder) {
            ((SpecTitleViewHolder) holder).bind((ProductDetailsAbstract.SpecTitle) item);
        } else if (holder instanceof CommentTitleViewHolder) {
            ((CommentTitleViewHolder) holder).bind((ProductDetailsAbstract.CommentTitle) item);
        }
    }

    @Override
    public int getItemCount() {
        return viewElementsList.size();
    }

    // ============
    // các holders
    // ============
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
        }

        void bind(ProductDetailsAbstract.ProductImage image) {
            productImage.setImageResource(image.imageId);
        }
    }

    static class PreviewViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textPrice;

        PreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_product_name);
            textPrice = itemView.findViewById(R.id.text_product_price);
        }

        void bind(ProductDetailsAbstract.ProductPreview preview) {
            textName.setText(preview.productName);
            textPrice.setText(preview.productPrice);
        }
    }

    static class SpecTitleViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;

        SpecTitleViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.product_spec_title);
        }

        void bind(ProductDetailsAbstract.SpecTitle item) {
            textTitle.setText(item.title);
        }
    }

    static class SpecViewHolder extends RecyclerView.ViewHolder {
        TextView textSpecKey, textSpecValue;

        SpecViewHolder(@NonNull View itemView) {
            super(itemView);
            textSpecKey = itemView.findViewById(R.id.text_spec_key);
            textSpecValue = itemView.findViewById(R.id.text_spec_value);
        }

        void bind(ProductDetailsAbstract.ProductSpec spec) {
            textSpecKey.setText(spec.key);
            textSpecValue.setText(spec.value);
        }
    }

    static class CommentTitleViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;

        CommentTitleViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.product_comment_title);
        }

        void bind(ProductDetailsAbstract.CommentTitle item) {
            textTitle.setText(item.title);
        }
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView textUser, textComment, textCreatedDate, textRate;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            textUser = itemView.findViewById(R.id.text_username);
            textComment = itemView.findViewById(R.id.text_content);
            textRate = itemView.findViewById(R.id.text_rate);
            textCreatedDate = itemView.findViewById(R.id.text_date);
        }

        void bind(ProductDetailsAbstract.Comment comment) {
            textUser.setText(comment.userName);
            textComment.setText(comment.content);

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            textCreatedDate.setText(formatter.format(comment.createdDate));
            textRate.setText(comment.rate);
        }
    }
}
