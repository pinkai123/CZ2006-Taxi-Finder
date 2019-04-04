package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.MyViewHolder> {

    Context context;
    ArrayList<FavouritesList> FavouritesList;

    public FavouritesAdapter(Context c, ArrayList<FavouritesList> p){

        context = c;
        FavouritesList = p;

    }




    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_favourites, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.addresstitle.setText(FavouritesList.get(i).getAddresstitle());
        myViewHolder.address.setText(FavouritesList.get(i).getAddress());


        final String getAddress = FavouritesList.get(i).getAddress();
        final String getAddresstitle = FavouritesList.get(i).getAddresstitle();
        final String getKeyfavourites = FavouritesList.get(i).getKeyfavourites();

        //click on address, it will bring up editing page
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent b = new Intent(context, EditTaskDesk.class);

                b.putExtra("addresstitle", getAddresstitle);
                b.putExtra("address", getAddress);
                b.putExtra("keyfavourites", getKeyfavourites);
                context.startActivity(b);
            }
        });


    }

    @Override
    public int getItemCount() {
        return FavouritesList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView addresstitle, address, keyfavourites;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            addresstitle = (TextView) itemView.findViewById(R.id.addresstitle);
            address = (TextView) itemView.findViewById(R.id.address);

        }
    }


}
