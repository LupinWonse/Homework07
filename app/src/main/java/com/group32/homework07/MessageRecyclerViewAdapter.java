package com.group32.homework07;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;


public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Message> messages;

    public MessageRecyclerViewAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textSender;
        private TextView textMessageText;
        private ImageView imageMessagePhoto;

        public ViewHolder(View itemView) {
            super(itemView);

            textSender = (TextView) itemView.findViewById(R.id.textMessageSender);
            textMessageText = (TextView) itemView.findViewById(R.id.textMessageMessage);
            imageMessagePhoto = (ImageView) itemView.findViewById(R.id.imageMessagePhoto);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message currentMessage = messages.get(position);

        holder.textSender.setText(currentMessage.getSenderUserUid());
        holder.textMessageText.setText(currentMessage.getMessageText());

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
