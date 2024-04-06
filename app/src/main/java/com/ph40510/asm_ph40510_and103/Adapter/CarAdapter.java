package com.ph40510.asm_ph40510_and103.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ph40510.asm_ph40510_and103.Model.CarModel;
import com.ph40510.asm_ph40510_and103.R;

import java.util.ArrayList;


public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder>{

    private Context context;
    private ArrayList<CarModel> list;
    private CarClick carClick;


    public CarAdapter(Context context, ArrayList<CarModel> list, CarClick carClick) {
        this.context = context;
        this.list = list;
        this.carClick = carClick;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvten,tvnamSX,tvhang,tvgia;
        ImageView btnedit,btndelete,img;
        public ViewHolder(View itemView) {
            super(itemView);

            tvten = itemView.findViewById(R.id.tv_ten);
            tvnamSX =  itemView.findViewById(R.id.tv_namSX);
            tvhang =  itemView.findViewById(R.id.tv_hang);
            tvgia =  itemView.findViewById(R.id.tv_gia);
            btnedit =  itemView.findViewById(R.id.btn_edit);
            btndelete =  itemView.findViewById(R.id.btn_delete);
            img =  itemView.findViewById(R.id.img);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {



        CarModel car = list.get(position);
        holder.tvten.setText("Tên: " +car.getTen());
        holder.tvnamSX.setText("Năm SX: " +car.getNamSX());
        holder.tvhang.setText("Hãng: " +car.getHang());
        holder.tvgia.setText("Giá: " +car.getGia());
        String url  = car.getImage().get(0);
        String newUrl = url.replace("localhost", "10.0.2.2");
        Glide.with(context)
                .load(newUrl)
                .thumbnail(Glide.with(context).load(R.drawable.baseline_broken_image_24))
                .into(holder.img);

        holder.btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carClick.edit(car);
            }
        });
        holder.btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carClick.delete(car);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface CarClick {
        void delete(CarModel car);
        void edit(CarModel car);

        void showDetail(CarModel car);
    }
}
