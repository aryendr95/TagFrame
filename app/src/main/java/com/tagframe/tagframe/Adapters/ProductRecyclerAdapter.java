package com.tagframe.tagframe.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.Product;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.Productlist;
import com.tagframe.tagframe.UI.Acitivity.SearchUserActivity;
import com.tagframe.tagframe.Utils.ImageLoader;
import com.tagframe.tagframe.Utils.Utility;
import java.util.ArrayList;

/**
 * Created by Brajendr on 1/20/2017.
 */

public class ProductRecyclerAdapter
    extends RecyclerView.Adapter<ProductRecyclerAdapter.MyViewHolder> {
  private Context ctx;
  private ArrayList<Product> products_models;

  public ProductRecyclerAdapter(Context ctx, ArrayList<Product> models) {
    this.ctx = ctx;
    this.products_models = models;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.layout_grid_product, parent, false);
    return new ProductRecyclerAdapter.MyViewHolder(itemView);
  }

  @Override public void onBindViewHolder(MyViewHolder mViewHolder, int position) {

    final Product product = products_models.get(position);
    ImageLoader.loadImageOnline(ctx, product.getImage(), mViewHolder.ivimage,
        mViewHolder.pbprdctImage);
    mViewHolder.tvName.setText(product.getName());
    mViewHolder.tvPrice.setText("$ "+product.getProduct_price());
    mViewHolder.ivimage.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        ProductAdapter.showProductDialog(ctx, product);
      }
    });
    mViewHolder.tvBuy.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(product.getUrl()));
        ctx.startActivity(browserIntent);
      }
    });
    mViewHolder.tvEndorse.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (ctx instanceof Productlist) {
          //frame endorse
          Intent intent = new Intent();
          intent.putExtra("product_id", product.getId());
          intent.putExtra("product_image", product.getImage());
          intent.putExtra("product_url", product.getUrl());
          intent.putExtra("product_name",product.getName());
          ((Productlist) ctx).setResult(Utility.PRODUCT_LIST_FLAG, intent);
          ((Productlist) ctx).finish();

        } else {
          //direct endorse
          Intent intent=new Intent(ctx, SearchUserActivity.class);
          intent.putExtra("product_id", product.getId());
          ctx.startActivity(intent);
        }
      }
    });
  }

  @Override public int getItemCount() {
    return products_models.size();
  }

  class MyViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivimage;
    private TextView tvName, tvPrice, tvEndorse, tvBuy;
    private ProgressBar pbprdctImage;

    public MyViewHolder(View item) {
      super(item);
      ivimage = (ImageView) item.findViewById(R.id.product_image);
      tvName = (TextView) item.findViewById(R.id.product_name);
      tvPrice = (TextView) item.findViewById(R.id.product_price);
      tvEndorse = (TextView) item.findViewById(R.id.endorse);
      tvBuy = (TextView) item.findViewById(R.id.buy);
      pbprdctImage = (ProgressBar) item.findViewById(R.id.pbar_product);
    }
  }
}
