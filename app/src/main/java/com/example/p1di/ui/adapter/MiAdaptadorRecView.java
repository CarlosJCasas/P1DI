package com.example.p1di.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p1di.R;

import java.util.List;


public class MiAdaptadorRecView extends RecyclerView.Adapter<MiAdaptadorRecView.ViewHolder> {
    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public MiAdaptadorRecView(Context context1, List<String> valores1) {
        this.mInflater = LayoutInflater.from(context1);
        this.mData = valores1;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_items, parent, false);
        return new ViewHolder(view);
    }

    // une la información a la vista en cada fila
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String datos = mData.get(position);
        holder.myTextView.setText(datos);
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.textViewRecView);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

    }

    // Devuelve la info en una posicion
    public String getItem(int id) {
        return mData.get(id);
    }

    // Permite recoger eventos de click
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // Lo implementa la clase padre para definir que hacer con el click
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}