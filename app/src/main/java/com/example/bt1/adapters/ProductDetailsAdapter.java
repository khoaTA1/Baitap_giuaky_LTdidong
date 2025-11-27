package com.example.bt1.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt1.R;
import com.example.bt1.activities.ProductDetailActivity;
import com.example.bt1.models.ProductDetailsAbstract;
import com.example.bt1.utils.RenderImage;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ProductDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ProductDetailsAbstract> viewElementsList;
    private Context context;
    private static RenderImage renderImage;

    public ProductDetailsAdapter(List<ProductDetailsAbstract> list, Context context) {
        this.viewElementsList = list;
        this.context = context;
    }

    private static final int image_view = 1;
    private static final int preview_view = 2;
    private static final int spec_title_view = 3;
    private static final int spec_view = 4;
    private static final int comment_title_view = 5;
    private static final int comment_input = 6;
    private static final int comment_view = 7;
    private static final int similar_products_view = 8;

    // tạo callback cho activity
    public interface OnCommentSendListener {
        void onSend(String content, float rating);
    }
    private OnCommentSendListener commentSendListener;

    public void setOnCommentSendListener(OnCommentSendListener listener) {
        this.commentSendListener = listener;
    }

    public int getItemViewType(int position) {
        ProductDetailsAbstract viewEle = viewElementsList.get(position);

        if (viewEle instanceof ProductDetailsAbstract.ProductImage) {
            return image_view;
        } else if (viewEle instanceof ProductDetailsAbstract.ProductPreview) {
            return preview_view;
        } else if (viewEle instanceof ProductDetailsAbstract.ProductSpecGroup) {
            return spec_view;
        } else if (viewEle instanceof ProductDetailsAbstract.SpecTitle) {
            return spec_title_view;
        } else if (viewEle instanceof ProductDetailsAbstract.CommentTitle) {
            return comment_title_view;
        } else if (viewEle instanceof ProductDetailsAbstract.CommentInput) {
            return comment_input;
        } else if (viewEle instanceof ProductDetailsAbstract.SimilarProductsSection) {
            return similar_products_view;
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
                return new SpecViewHolder(inflater.inflate(R.layout.product_spec_card_sublayout, parent, false));
            case spec_title_view:
                return new SpecTitleViewHolder(inflater.inflate(R.layout.product_spec_title_sublayout, parent, false));
            case comment_title_view:
                return new CommentTitleViewHolder(inflater.inflate(R.layout.product_comment_title_sublayout, parent, false));
            case comment_input:
                return new CommentInputViewHolder(inflater.inflate(R.layout.product_comment_input_sublayout, parent, false));
            case similar_products_view:
                return new SimilarProductsViewHolder(inflater.inflate(R.layout.item_similar_products, parent, false), context);
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
            ((SpecViewHolder) holder).bind((ProductDetailsAbstract.ProductSpecGroup) item);
        } else if (holder instanceof CommentViewHolder) {
            ((CommentViewHolder) holder).bind((ProductDetailsAbstract.Comment) item);
        } else if (holder instanceof SpecTitleViewHolder) {
            ((SpecTitleViewHolder) holder).bind((ProductDetailsAbstract.SpecTitle) item);
        } else if (holder instanceof CommentTitleViewHolder) {
            ((CommentTitleViewHolder) holder).bind((ProductDetailsAbstract.CommentTitle) item);
        } else if (holder instanceof CommentInputViewHolder) {
            ((CommentInputViewHolder) holder).bind(commentSendListener);
        } else if (holder instanceof SimilarProductsViewHolder) {
            ((SimilarProductsViewHolder) holder).bind((ProductDetailsAbstract.SimilarProductsSection) item);
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
            renderImage = new RenderImage();
            //renderImage.renderProductImage(productImage.getContext(), image.product, productImage);
            productImage.setImageResource(image.imageId);
        }
    }

    static class PreviewViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textPrice, textOriginalPrice, textDesc;

        PreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_product_name);
            textPrice = itemView.findViewById(R.id.text_product_price);
            textOriginalPrice = itemView.findViewById(R.id.text_original_price);
            textDesc = itemView.findViewById(R.id.text_product_desc);
        }

        void bind(ProductDetailsAbstract.ProductPreview preview) {
            textName.setText(preview.productName);
            textPrice.setText(preview.productPrice);
            textDesc.setText(preview.productDesc);
            
            // Hiển thị giá gốc nếu có giảm giá
            if (preview.originalPrice != null && !preview.originalPrice.isEmpty()) {
                textOriginalPrice.setText(preview.originalPrice);
                textOriginalPrice.setVisibility(View.VISIBLE);
                textOriginalPrice.setPaintFlags(textOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                textOriginalPrice.setVisibility(View.GONE);
            }
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

        LinearLayout specContainer;
        LayoutInflater inflater;

        SpecViewHolder(@NonNull View itemView) {
            super(itemView);
            specContainer = itemView.findViewById(R.id.spec_container);
            inflater = LayoutInflater.from(itemView.getContext());
        }

        void bind(ProductDetailsAbstract.ProductSpecGroup group) {
            specContainer.removeAllViews();

            for (int i = 0; i < group.list.size(); i++) {
                ProductDetailsAbstract.ProductSpec spec = group.list.get(i);
                View specItem = inflater.inflate(R.layout.product_spec_list_sublayout, specContainer, false);

                TextView textKey = specItem.findViewById(R.id.text_spec_key);
                TextView textValue = specItem.findViewById(R.id.text_spec_value);
                View divider = specItem.findViewById(R.id.spec_divider);

                textKey.setText(spec.key);
                textValue.setText(spec.value);

                // Ẩn divider nếu là item cuối cùng
                if (i == group.list.size() - 1) {
                    divider.setVisibility(View.GONE);
                }

                specContainer.addView(specItem);
            }
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
        TextView textUser, textComment, textCreatedDate;
        RatingBar Rate;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            textUser = itemView.findViewById(R.id.text_username);
            textComment = itemView.findViewById(R.id.text_content);
            Rate = itemView.findViewById(R.id.rating_bar_view);
            textCreatedDate = itemView.findViewById(R.id.text_date);
        }

        void bind(ProductDetailsAbstract.Comment comment) {
            textUser.setText(comment.userName);
            textComment.setText(comment.content);

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            textCreatedDate.setText(formatter.format(comment.createdDate));

            Rate.setRating(comment.rate);
        }
    }

    static class CommentInputViewHolder extends RecyclerView.ViewHolder {
        EditText editComment;
        RatingBar ratingBar;
        Button btnSendComment;

        CommentInputViewHolder(View itemView) {
            super(itemView);
            editComment = itemView.findViewById(R.id.edit_comment);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            btnSendComment = itemView.findViewById(R.id.btn_send_comment);
        }

        void bind(ProductDetailsAdapter.OnCommentSendListener listener) {
            // Xử lý việc gửi bình luận
            btnSendComment.setOnClickListener(v -> {
                String commentText = editComment.getText().toString();
                float rating = ratingBar.getRating();
                // Xử lý gửi đánh giá
                if (!commentText.isEmpty() && rating > 0) {
                    listener.onSend(commentText, rating);

                    editComment.setText("");
                    ratingBar.setRating(0);
                    Toast.makeText(v.getContext(), "Đã gửi bình luận", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Vui lòng nhập bình luận và chọn sao", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    static class SimilarProductsViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerSimilarProducts;
        Context context;

        SimilarProductsViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            recyclerSimilarProducts = itemView.findViewById(R.id.recycler_similar_products);
            recyclerSimilarProducts.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        }

        void bind(ProductDetailsAbstract.SimilarProductsSection section) {
            ProductAdapter adapter = new ProductAdapter(context, section.similarProducts, product -> {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                context.startActivity(intent);
            });
            recyclerSimilarProducts.setAdapter(adapter);
        }
    }
}