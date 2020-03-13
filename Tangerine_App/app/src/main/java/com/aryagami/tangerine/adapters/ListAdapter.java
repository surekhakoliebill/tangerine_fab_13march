package com.aryagami.tangerine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.aryagami.R;
import com.aryagami.data.DeviceOrder;
import com.aryagami.data.ListViewHolder;
import com.aryagami.data.Product;

import java.util.ArrayList;
import java.util.List;


public class ListAdapter extends BaseAdapter {

    public ArrayList<Product> listProducts;
    public List<DeviceOrder> deviceOrderList = new ArrayList<DeviceOrder>();
    private Context context;


    public ListAdapter(Context context, ArrayList<Product> listProducts) {
        this.context = context;
        this.listProducts = listProducts;
    }

    public ListAdapter(Context context, List<DeviceOrder> deviceOrderList) {
        this.context = context;
        this.deviceOrderList = deviceOrderList;
    }
    public  void onTrimMemory(int level) {
        System.gc();
    }
    @Override
    public int getCount() {
        return listProducts.size();
    }

    @Override
    public Product getItem(int position) {
        return listProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView
            , ViewGroup parent) {
        View row;
        final ListViewHolder listViewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.item_list_for_device, parent, false);
            listViewHolder = new ListViewHolder();
            listViewHolder.productName = row.findViewById(R.id.product_name);
            listViewHolder.productCategory = row.findViewById(R.id.product_category);

            listViewHolder.btnPlus = row.findViewById(R.id.ib_addnew);
            listViewHolder.edTextQuantity = row.findViewById(R.id.editTextQuantity);
            listViewHolder.btnMinus = row.findViewById(R.id.ib_remove);
            row.setTag(listViewHolder);
        } else {
            row = convertView;
            listViewHolder = (ListViewHolder) row.getTag();
        }
        final Product products = getItem(position);

        listViewHolder.productName.setText(products.ProductName);
        if(!products.ProductCategory.isEmpty()){
        listViewHolder.productCategory.setText(products.ProductCategory);
        }
       // listViewHolder.ivProduct.setImageResource(products.ProductImage);
//        listViewHolder.tvPrice.setText(products.ProductPrice + " $");
        listViewHolder.edTextQuantity.setText(products.CartQuantity + "");
        listViewHolder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantity(position, listViewHolder.edTextQuantity, 1);
            }
        });
        //listViewHolder.edTextQuantity.setText("0");
        listViewHolder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantity(position, listViewHolder.edTextQuantity, -1);

            }
        });

        return row;
    }

    private void updateQuantity(int position, EditText edTextQuantity, int value) {

        Product products = getItem(position);
        if (value > 0) {
            products.CartQuantity = products.CartQuantity + 1;
        } else {
            if (products.CartQuantity > 0) {
                products.CartQuantity = products.CartQuantity - 1;
            }

        }
        edTextQuantity.setText(products.CartQuantity + "");
    }
}