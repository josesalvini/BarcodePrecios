package com.dinosaurio.preciosdino.ComplementosClass;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dinosaurio.preciosdino.R;

import java.util.List;

public class ViewQRAdapter extends BaseAdapter  {

    private Context context;
    private List< QrItem > qrList;

    public ViewQRAdapter(Context context, List<QrItem> qrList) {
        this.context = context;
        this.qrList = qrList;
    }

    @Override
    public int getCount() {
        return qrList.size();
    }

    @Override
    public Object getItem(int i) {
        return qrList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView ==null){
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.list_item_qr, null);
            holder = new ViewHolder();

            holder.qrImagen = (ImageView) convertView.findViewById(R.id.qrImage);
            holder.codigoBarra = (TextView) convertView.findViewById(R.id.tvCodigoEAN);
            holder.tipoEtiqueta = (TextView) convertView.findViewById(R.id.tvTipoEtiqueta);
            holder.cantidadCopias = (TextView) convertView.findViewById(R.id.tvCantidadCopias);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();

        }

        holder.qrImagen.setImageBitmap(qrList.get(i).getQrImagen());
        holder.codigoBarra.setText(qrList.get(i).getCodigoBarra());
        holder.tipoEtiqueta.setText(qrList.get(i).getTipoEtiqueta());
        holder.cantidadCopias.setText(qrList.get(i).getCantidadCopias());

        return convertView;
    }

    static class ViewHolder  {

        ImageView qrImagen;
        TextView codigoBarra;
        TextView tipoEtiqueta;
        TextView cantidadCopias;

    }

}
