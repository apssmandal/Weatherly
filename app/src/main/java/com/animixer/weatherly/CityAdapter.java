package com.animixer.weatherly;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;


class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private List<PlaceList> ListItems;
    private Context context;
    String languageToLoad  = "en"; // your language



    public CityAdapter(List<PlaceList> listItems, Context context) {
        ListItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.loc_search_list, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaceList placeList = ListItems.get(position);


        String name = placeList.getmNameData();
        String state = placeList.getmStateData();
        String country = placeList.getmCountryData();


        holder.rCityName.setText(name);
        if (state.equals("")){
            holder.rStateName.setText("");
        }
        else {
            holder.rStateName.setText(state+", ");
        }
        holder.rCountryName.setText(country);


    }

    @Override
    public int getItemCount() {
        return ListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView rCityName,rStateName,rCountryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rCityName = itemView.findViewById(R.id.search_city_name);
            rCountryName = itemView.findViewById(R.id.search_country_name);
            rStateName = itemView.findViewById(R.id.search_state_name);

        }


    }

}
