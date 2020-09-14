package com.botosofttechnologies.prepme.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.botosofttechnologies.prepme.R;

public class LeaderBoardViewHolder extends RecyclerView.ViewHolder {

    public TextView star;
    public TextView name;
    public TextView number;


    public LeaderBoardViewHolder(View itemView) {
        super(itemView);
        this.star = (TextView) itemView.findViewById(R.id.star);
        this.name = (TextView) itemView.findViewById(R.id.name);
        this.number = (TextView) itemView.findViewById(R.id.number);

    }
}
