package se.mah.af6589.assignment2;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gustaf Bohlin on 30/09/2017.
 */

public class SubRecyclerViewAdapter extends RecyclerView.Adapter<SubRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Member> members;
    private MainActivity activity;

    public SubRecyclerViewAdapter(MainActivity activity, ArrayList<Member> members) {
        this.activity = activity;
        this.members = members;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_member_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Member member = members.get(position);
        holder.bind(activity, member);
        holder.tvName.setText((member.getName()));
        holder.cvColorIndicator.setForeground((member.isShowOnMap()? new ColorDrawable(Color.GREEN): new ColorDrawable(Color.RED)));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void setMembers(ArrayList<Member> members) {
        this.members = members;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View parent;
        public TextView tvName;
        public CardView cvColorIndicator;

        public ViewHolder(View itemView) {
            super(itemView);
            parent = itemView;
            tvName = (TextView) itemView.findViewById(R.id.tv_member);
            cvColorIndicator = (CardView) itemView.findViewById(R.id.cv_color_indicator);
        }

        public void bind(final MainActivity activity, final Member member) {
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (activity.getController().getDataFragment().isInGroup(member.getGroup())) {
                        member.setShowOnMap(!member.isShowOnMap());
                        cvColorIndicator.setForeground((member.isShowOnMap() ? new ColorDrawable(Color.GREEN) : new ColorDrawable(Color.RED)));
                        activity.getController().refreshMarkers();
                    }
                }
            });
        }
    }
}