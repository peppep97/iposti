package com.tecnovajet.iposti.facilities;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tecnovajet.iposti.R;
import com.tecnovajet.iposti.expandablelist.AnimatedExpandableListView;

import java.util.List;
import java.util.Locale;

import androidx.core.content.ContextCompat;

public class ServiziAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private LayoutInflater inflater;
    private Context context;

    private static class GroupHolder {
        TextView title;
        ImageView arrow;
    }

    private static class ChildHolder {
        TextView title;
        TextView prezzo;
    }

    private List<GroupItem> items;

    public ServiziAdapter(Context context, List<GroupItem> items) {
        this.context = context;
        this.items = items;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public Servizio getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).childServizi.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder;
        Servizio servizio = getChild(groupPosition, childPosition);
        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.exp_item_list, parent, false);
            holder.title = convertView.findViewById(R.id.expandedListItem);
            holder.title.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/comfortaa.ttf"));
            //holder.title.setTypeface(holder.title.getTypeface(), Typeface.BOLD);
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.black));

            holder.prezzo = convertView.findViewById(R.id.prezzo);
            holder.prezzo.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/comfortaa.ttf"));
            holder.prezzo.setTypeface(holder.title.getTypeface(), Typeface.BOLD);

            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        holder.title.setText(servizio.getNome());

        if (servizio.getPrezzo() == -1)
            holder.prezzo.setVisibility(View.INVISIBLE);
        else {
            holder.prezzo.setVisibility(View.VISIBLE);
            String prezzo = String.format(Locale.ITALIAN, "€ %.2f", servizio.getPrezzo());
            holder.prezzo.setText(prezzo);
        }

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return items.get(groupPosition).childServizi.size();
    }

    @Override
    public GroupItem getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder;
        GroupItem item = getGroup(groupPosition);
        if (convertView == null) {
            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.exp_item_group, parent, false);
            holder.title = convertView.findViewById(R.id.listTitle);
            holder.title.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/comfortaa.ttf"));
            holder.title.setTypeface(holder.title.getTypeface(), Typeface.BOLD);
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

            holder.arrow = convertView.findViewById(R.id.imageView3);

            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        holder.title.setText(item.title);
        holder.arrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_right));
        if (isExpanded){
            holder.arrow.setRotation(90);
        }else{
            holder.arrow.setRotation(0);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return (items.get(arg0).childServizi.get(arg1).getPrezzo() > -1);
    }

}