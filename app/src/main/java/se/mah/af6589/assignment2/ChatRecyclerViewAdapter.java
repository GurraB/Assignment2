package se.mah.af6589.assignment2;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gustaf Bohlin on 01/10/2017.
 */

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Chat.Message> messages;
    private MainActivity activity;

    public ChatRecyclerViewAdapter(ArrayList<Chat.Message> messages, MainActivity activity) {
        this.messages = messages;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_chat_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat.Message message = messages.get(position);
        holder.tvMemberName.setText(message.getMemberName());
        holder.tvText.setText(message.getText());
        if (message.getImageId() != null) {
            holder.ivImageMessage.setAdjustViewBounds(true);
            holder.ivImageMessage.setImage(message.getImageId(), message.getPort());
        }
        else {
            holder.ivImageMessage.setImageBitmap(null);
            holder.ivImageMessage.setAdjustViewBounds(false);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View parent;
        public TextView tvColorIndicator, tvMemberName, tvText;
        public ImageFromServerView ivImageMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            parent = itemView;
            tvColorIndicator = (TextView) itemView.findViewById(R.id.tv_chat_color_indicator);
            tvMemberName = (TextView) itemView.findViewById(R.id.tv_chat_member_name);
            tvText = (TextView) itemView.findViewById(R.id.tv_chat_text_message);
            ivImageMessage = (ImageFromServerView) itemView.findViewById(R.id.iv_chat_image);
        }
    }
}
