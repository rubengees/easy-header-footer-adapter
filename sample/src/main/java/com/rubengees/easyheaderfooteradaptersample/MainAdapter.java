package com.rubengees.easyheaderfooteradaptersample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple adapter for the {@link Item}. It covers to different view types: Text and color.
 *
 * @author Ruben Gees
 */
public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TEXT_VIEW_TYPE = 0;
    private static final int COLOR_VIEW_TYPE = 1;

    private static final String ITEMS_STATE = "items_state";

    private ArrayList<Item> items;

    @Nullable
    private MainAdapterCallback callback;

    public MainAdapter(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            this.items = new ArrayList<>();
        } else {
            this.items = savedInstanceState.getParcelableArrayList(ITEMS_STATE);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TEXT_VIEW_TYPE:
                return new TextViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_item_text, parent, false));
            case COLOR_VIEW_TYPE:
                return new ColorViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_item_color, parent, false));
            default:
                throw new IllegalArgumentException("Unknown viewType: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TextViewHolder) {
            ((TextViewHolder) holder).bind(items.get(position));
        } else if (holder instanceof ColorViewHolder) {
            ((ColorViewHolder) holder).bind(items.get(position));
        } else {
            throw new IllegalArgumentException("Unknown ViewHolder type: " + holder.getClass());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).getColor() % 2 == 0) { // Something to show
            return TEXT_VIEW_TYPE;
        } else {
            return COLOR_VIEW_TYPE;
        }
    }

    public void addItem(@NonNull Item item) {
        items.add(item);

        notifyItemInserted(getItemCount() - 1);
    }

    public void removeItem(@NonNull Item item) {
        removeItem(items.indexOf(item));
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    public void setCallback(@Nullable MainAdapterCallback callback) {
        this.callback = callback;
    }

    public void saveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(ITEMS_STATE, items);
    }

    public interface MainAdapterCallback {
        void onItemClick(int position);
    }

    private class TextViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        TextViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && callback != null) {
                        callback.onItemClick(getAdapterPosition());
                    }
                }
            });
        }

        public void bind(Item item) {
            textView.setText(item.getText());
        }
    }

    private class ColorViewHolder extends RecyclerView.ViewHolder {

        private View colorContainer;

        ColorViewHolder(View itemView) {
            super(itemView);

            colorContainer = itemView.findViewById(R.id.colorContainer);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && callback != null) {
                        callback.onItemClick(getAdapterPosition());
                    }
                }
            });
        }

        public void bind(Item item) {
            colorContainer.setBackgroundColor(item.getColor());
        }
    }
}
