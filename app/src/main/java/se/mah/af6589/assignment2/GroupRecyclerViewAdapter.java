package se.mah.af6589.assignment2;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Gustaf Bohlin on 30/09/2017.
 */

public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> groups;
    private MainActivity activity;

    public GroupRecyclerViewAdapter(MainActivity activity, ArrayList<String> groups) {
        this.activity = activity;
        this.groups = groups;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_group_item, parent, false);
        return new ViewHolder(activity, v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String group = groups.get(position);
        holder.tvName.setText(group);
        holder.bind(activity, group);
        if (activity.getController().getDataFragment().isInGroup(group))
            holder.ibJoinLeave.setImageResource(R.drawable.ic_leave);
        else
            holder.ibJoinLeave.setImageResource(R.drawable.ic_join);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View parent;
        public ImageView ivExpand;
        public TextView tvName;
        public ImageButton ibJoinLeave;
        public RecyclerView rvMembers;
        private SubRecyclerViewAdapter adapter;
        private boolean isOpen = false;

        public ViewHolder(MainActivity activity, View itemView) {
            super(itemView);
            parent = itemView;
            ivExpand = (ImageView) itemView.findViewById(R.id.iv_expand);
            tvName = (TextView) itemView.findViewById(R.id.tv_group_name);
            ibJoinLeave = (ImageButton) itemView.findViewById(R.id.ib_join);
            rvMembers = (RecyclerView) itemView.findViewById(R.id.rv_members);
            rvMembers.setLayoutManager(new LinearLayoutManager(activity));
            adapter = new SubRecyclerViewAdapter(activity, new ArrayList<Member>());
            rvMembers.setAdapter(adapter);
        }

        public void bind(final MainActivity activity, final String group) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RotateAnimation anim;
                    if (!isOpen) {
                        adapter.setMembers(activity.getController().getDataFragment().getMembersInGroup(group));
                        adapter.notifyDataSetChanged();
                        isOpen = true;
                        anim = new RotateAnimation(0.0f, 90.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    } else {
                        adapter.setMembers(new ArrayList<Member>());
                        adapter.notifyDataSetChanged();
                        isOpen = false;
                        anim = new RotateAnimation(90.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    }
                    anim.setInterpolator(new LinearInterpolator());
                    anim.setDuration(200);
                    anim.setFillEnabled(true);
                    anim.setFillAfter(true);
                    ivExpand.startAnimation(anim);
                }
            });

            ibJoinLeave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (activity.getController().getDataFragment().isInGroup(group)) {
                        activity.getController().leaveGroup(group);
                        ibJoinLeave.setImageResource(R.drawable.ic_join);
                    } else {
                        activity.getController().joinGroup(group);
                        ibJoinLeave.setImageResource(R.drawable.ic_leave);
                    }
                }
            });
        }
    }
}
