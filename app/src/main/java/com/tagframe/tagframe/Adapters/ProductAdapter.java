package com.tagframe.tagframe.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.Product;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.SearchUserActivity;
import com.tagframe.tagframe.UI.Acitivity.MakeNewEvent;
import com.tagframe.tagframe.UI.Acitivity.Productlist;
import com.tagframe.tagframe.Utils.Utility;

import java.util.ArrayList;

/**
 * Created by abhinav on 01/06/2016.
 */
public class ProductAdapter extends BaseAdapter {

    private Context ctx;
    private ArrayList<Product> tagStream_models;
    private LayoutInflater inflater;


    public ProductAdapter(Context ctx, ArrayList<Product> tagStream_models) {
        this.ctx = ctx;
        this.tagStream_models = tagStream_models;

        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tagStream_models.size();
    }

    @Override
    public Object getItem(int position) {
        return tagStream_models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_grid_product, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        final Product tagStream = tagStream_models.get(position);


        try {
            Picasso.with(ctx).load(tagStream.getImage()).into(mViewHolder.ivimage);
        } catch (Exception e) {

        }
        mViewHolder.textView.setText(tagStream.getName());
        mViewHolder.tvPrice.setText(tagStream.getProduct_price());
        mViewHolder.tvEndorse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ctx instanceof Productlist) {
                    //frame endorse
                    Intent intent = new Intent();
                    intent.putExtra("product_id", tagStream.getId());
                    intent.putExtra("product_image", tagStream.getImage());
                    intent.putExtra("product_url", tagStream.getUrl());
                    intent.putExtra("product_name", tagStream.getName());
                    ((Productlist) ctx).setResult(Utility.PRODUCT_LIST_FLAG, intent);
                    ((Productlist) ctx).finish();

                } else {
                    //direct endorse
                    Intent intent = new Intent(ctx, SearchUserActivity.class);
                    intent.putExtra("product_id", tagStream.getId());
                    ctx.startActivity(intent);
                }
            }
        });
        mViewHolder.tvBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tagStream.getUrl()));
                ctx.startActivity(browserIntent);
            }
        });

        mViewHolder.ivimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProductDialog(ctx, tagStream);

            }
        });


        return convertView;
    }

    static void showProductDialog(final Context ctx, final Product product) {
        final Dialog dialog = new Dialog(ctx, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_see_product);
        dialog.setCancelable(true);
        //dismissing dialog
        dialog.findViewById(R.id.img_comment_dialog_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView product_name = (TextView) dialog.findViewById(R.id.dialog_product_name);
        TextView product_price = (TextView) dialog.findViewById(R.id.dialog_product_price);
        TextView endorse_product = (TextView) dialog.findViewById(R.id.dialog_product_endorse);
        TextView buy_product = (TextView) dialog.findViewById(R.id.dialog_product_buy);

        ImageView product_image = (ImageView) dialog.findViewById(R.id.dialog_product_image);
        Picasso.with(ctx).load(product.getImage()).into(product_image);

        product_name.setText(product.getName());
        product_price.setText("$" + product.getProduct_price());

        buy_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(product.getUrl()));
                ctx.startActivity(browserIntent);
            }
        });


        //if user access products with market place ,he can directly endorse it else
        //user endorses the product to the frame
        //if the context here is of marketplace, then directly endorse else otherwise

        if (ctx instanceof Productlist) {
            //frame endorse
            endorse_product.setText("Endorse");
        } else {
            //direct endorse
            endorse_product.setText("Direct Endorse");
        }

        endorse_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ctx instanceof Productlist) {
                    //frame endorse
                    Intent intent = new Intent();
                    intent.putExtra("product_id", product.getId());
                    intent.putExtra("product_image", product.getImage());
                    intent.putExtra("product_url", product.getUrl());
                    intent.putExtra("product_name", product.getName());
                    ((Productlist) ctx).setResult(Utility.PRODUCT_LIST_FLAG, intent);
                    ((Productlist) ctx).finish();

                } else {
                    //direct endorse
                    Intent intent = new Intent(ctx, SearchUserActivity.class);
                    intent.putExtra("product_id", product.getId());
                    ctx.startActivity(intent);
                }
            }
        });

        dialog.show();


        //setting pages
    }


    static void showProductDialog(final Context ctx, final Product product, SpannableString message) {
        final Dialog dialog = new Dialog(ctx, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_see_product);
        dialog.setCancelable(true);
        //dismissing dialog
        dialog.findViewById(R.id.img_comment_dialog_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView product_name = (TextView) dialog.findViewById(R.id.dialog_product_name);
        TextView product_message = (TextView) dialog.findViewById(R.id.txtMessage);
        TextView product_price = (TextView) dialog.findViewById(R.id.dialog_product_price);
        TextView endorse_product = (TextView) dialog.findViewById(R.id.dialog_product_endorse);
        TextView buy_product = (TextView) dialog.findViewById(R.id.dialog_product_buy);

        product_message.setText(message);

        ImageView product_image = (ImageView) dialog.findViewById(R.id.dialog_product_image);
        Picasso.with(ctx).load(product.getImage()).into(product_image);

        product_name.setText(product.getName());
        product_price.setText("$" + product.getProduct_price());

        buy_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(product.getUrl()));
                ctx.startActivity(browserIntent);
            }
        });


        //if user access products with market place ,he can directly endorse it else
        //user endorses the product to the frame
        //if the context here is of marketplace, then directly endorse else otherwise

        if (ctx instanceof Productlist) {
            //frame endorse
            endorse_product.setText("Endorse");
        } else {
            //direct endorse
            endorse_product.setText("Direct Endorse");
        }

        endorse_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ctx instanceof Productlist) {
                    //frame endorse
                    Intent intent = new Intent();
                    intent.putExtra("product_id", product.getId());
                    intent.putExtra("product_image", product.getImage());
                    intent.putExtra("product_url", product.getUrl());
                    intent.putExtra("product_name", product.getName());
                    ((Productlist) ctx).setResult(Utility.PRODUCT_LIST_FLAG, intent);
                    ((Productlist) ctx).finish();

                } else {
                    //direct endorse
                    Intent intent = new Intent(ctx, SearchUserActivity.class);
                    intent.putExtra("product_id", product.getId());
                    ctx.startActivity(intent);
                }
            }
        });


        dialog.show();


        //setting pages
    }

    private class MyViewHolder {

        ImageView ivimage;
        TextView textView, tvPrice, tvEndorse, tvBuy;
        ;


        public MyViewHolder(View item) {
            ivimage = (ImageView) item.findViewById(R.id.product_image);
            textView = (TextView) item.findViewById(R.id.product_name);
            tvPrice = (TextView) item.findViewById(R.id.product_price);
            tvEndorse = (TextView) item.findViewById(R.id.endorse);
            tvBuy = (TextView) item.findViewById(R.id.buy);

        }
    }
}
