package com.app.mg.aoe.upc.Adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.mg.aoe.upc.Activities.ControlWsActivity;
import com.app.mg.aoe.upc.R;

import java.util.List;

public class NetworkAdapter extends RecyclerView.Adapter<NetworkAdapter.ViewHolder> {
    List<String> networkList;

    public NetworkAdapter(List<String> networkList){
        this.networkList = networkList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.network_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.updateViews(networkList.get(i));
    }

    @Override
    public int getItemCount() {
        return networkList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView networkAddress;
        Button btnConnectWS;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            networkAddress = itemView.findViewById(R.id.network_ip);
            btnConnectWS = itemView.findViewById(R.id.btn_connect_ws);
            btnConnectWS.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(),ControlWsActivity.class);
                intent.putExtra("networkIp",networkAddress.getText().toString());
                v.getContext().startActivity(intent);
            });
        }
        public void updateViews(String networkIp) {
            networkAddress.setText(networkIp);
        }

    }
}
