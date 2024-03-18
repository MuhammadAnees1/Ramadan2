package com.example.ramadan1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;
public class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> texts;
    private List<Integer> imageIds;

    public CustomPagerAdapter(Context context, List<String> texts, List<Integer> imageIds) {
        mContext = context;
        this.texts = texts;
        this.imageIds = imageIds;
    }
    @Override
    public int getCount() {
        return texts.size();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.viewpager_page, collection, false);

        ImageView imageView = layout.findViewById(R.id.imageView);
        TextView textView = layout.findViewById(R.id.textView);

        imageView.setImageResource(imageIds.get(position));
        textView.setText(texts.get(position));

        collection.addView(layout);
        return layout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
