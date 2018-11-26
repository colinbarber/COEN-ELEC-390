package com.example.roumeliotis.tagit;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HintAdapter extends ArrayAdapter<NFCTag> {

    private Context mContext;
    private List<NFCTag> tagsList = new ArrayList<>();
    private List<Long> foundTagsList = new ArrayList<>();

    public HintAdapter(@NonNull Context context, List<NFCTag> list1, List<Long> list2 ) {
        super(context, 0 , list1);
        mContext = context;
        tagsList = list1;
        foundTagsList = list2;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.hint_list_item, parent, false);

        NFCTag currentTag = tagsList.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.text_view_hint);
        name.setText(currentTag.getHint());

        ImageView imageCheck = (ImageView) listItem.findViewById(R.id.check_mark_image);
        ImageView imageNoCheck = (ImageView) listItem.findViewById(R.id.nocheck_mark_image);
        //image.setImageResource(17301520);
        if (foundTagsList.contains(currentTag.getRemote_id())) {
            imageCheck.setVisibility(listItem.VISIBLE);
            imageNoCheck.setVisibility(listItem.GONE);
        }
        else{
            imageCheck.setVisibility(listItem.GONE);
            imageNoCheck.setVisibility(listItem.VISIBLE);
        }
        return listItem;
    }
}