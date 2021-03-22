package com.dinosaurio.preciosdino.ComplementosClass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dinosaurio.preciosdino.ActivityClass.CargaEANActivity;
import com.dinosaurio.preciosdino.ActivityClass.ConfiguracionActivity;
import com.dinosaurio.preciosdino.ActivityClass.ExportFileActivity;
import com.dinosaurio.preciosdino.ActivityClass.ScanActivity;
import com.dinosaurio.preciosdino.ActivityClass.ViewCargaActivity;
import com.dinosaurio.preciosdino.R;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private Context context;
    private List< MenuItem > menuList;
    private IntentIntegrator scan;

    public MenuAdapter(Context context, List<MenuItem> menuList) {
        this.context = context;
        this.menuList = menuList;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View mView;
        LayoutInflater inflater = LayoutInflater.from(context);
        mView =  inflater.inflate(R.layout.recyclerview_menu_item, parent, false);
        return new MenuViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, final int position) {
        holder.mImage.setImageResource(menuList.get(position).getMenuImagen());
        holder.mTitle.setText(menuList.get(position).getMenuOpcion());

        holder.itemView.setOnClickListener(new View.OnClickListener() {  // <--- here
            @Override
            public void onClick(View v) {
                Log.i("item","Click-"+ position);
                switch (position){
                    case 0:
                        context.startActivity(new Intent(context, ExportFileActivity.class));
                        break;
                    case 1:
                        //context.startActivity(new Intent(context, ScanActivity.class));

                        //scan = new IntentIntegrator();
                       // scan.setBeepEnabled(true);
                        //scan.setCaptureActivity(CargaEANActivity.class);
                       // scan.initiateScan();

                        break;
                    case 2:
                        context.startActivity(new Intent(context, ConfiguracionActivity.class));
                        break;
                    case 3:
                        context.startActivity(new Intent(context, ViewCargaActivity.class));
                        break;
                }



            }
        });

    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {

        ImageView mImage;
        TextView mTitle;

        MenuViewHolder(View itemView) {
            super(itemView);

            mImage = itemView.findViewById(R.id.imgID);
            mTitle = itemView.findViewById(R.id.nameItem);
        }
    }

}
