package com.rubengees.easyheaderfooteradapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * A simple to use adapter for the RecyclerView. It decorates an existing adapter with the ability
 * to set any View as header, footer or both.
 *
 * @author Ruben Gees
 */
public class EasyHeaderFooterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = Integer.MIN_VALUE;
    private static final int TYPE_FOOTER = Integer.MIN_VALUE + 1;
    private static final long ID_HEADER = Long.MIN_VALUE;
    private static final long ID_FOOTER = Long.MIN_VALUE + 1;

    private RecyclerView.Adapter innerAdapter;

    private View header;
    private View footer;

    private RecyclerView.LayoutManager layoutManager;

    /**
     * The constructor.
     *
     * @param innerAdapter The adapter to wrap.
     */
    public EasyHeaderFooterAdapter(@NonNull RecyclerView.Adapter innerAdapter) {
        this.innerAdapter = innerAdapter;

        this.innerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onChanged() {
                notifyDataSetChanged();
            }

            public void onItemRangeChanged(int positionStart, int itemCount) {
                notifyItemRangeChanged(getDelegatedPosition(positionStart), itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                notifyItemRangeChanged(getDelegatedPosition(positionStart), itemCount, payload);
            }

            public void onItemRangeInserted(int positionStart, int itemCount) {
                notifyItemRangeInserted(getDelegatedPosition(positionStart), itemCount);
            }

            public void onItemRangeRemoved(int positionStart, int itemCount) {
                notifyItemRangeRemoved(getDelegatedPosition(positionStart), itemCount);
            }

            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                notifyItemRangeChanged(getDelegatedPosition(fromPosition),
                        getDelegatedPosition(toPosition) + itemCount);
            }
        });

        setHasStableIds(innerAdapter.hasStableIds());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        layoutManager = recyclerView.getLayoutManager();

        initLayoutManager();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER || viewType == TYPE_FOOTER) {
            return new HeaderFooterViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.easy_header_footer_adapter_item, parent, false));
        } else {
            return innerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position,
                                 List<Object> payloads) {
        if (holder instanceof HeaderFooterViewHolder) {
            bind((HeaderFooterViewHolder) holder, position);
        } else {
            //noinspection unchecked
            innerAdapter.onBindViewHolder(holder, getRealPosition(position), payloads);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderFooterViewHolder) {
            bind((HeaderFooterViewHolder) holder, position);
        } else {
            //noinspection unchecked
            innerAdapter.onBindViewHolder(holder, getRealPosition(position));
        }
    }

    @Override
    public int getItemCount() {
        return innerAdapter.getItemCount() + (hasHeader() ? 1 : 0) + (hasFooter() ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return TYPE_HEADER;
        } else if (isFooter(position)) {
            return TYPE_FOOTER;
        } else {
            return innerAdapter.getItemViewType(getRealPosition(position));
        }
    }

    @Override
    public long getItemId(int position) {
        if (isHeader(position)) {
            return ID_HEADER;
        } else if (isFooter(position)) {
            return ID_FOOTER;
        } else {
            return innerAdapter.getItemId(getRealPosition(position));
        }
    }

    /**
     * Returns if a header is currently set.
     *
     * @return True if a header is set.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean hasHeader() {
        return header != null;
    }

    /**
     * Returns if a footer is currently set.
     *
     * @return True if a footer is set.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean hasFooter() {
        return footer != null;
    }

    /**
     * Returns if a header is at the specified position.
     *
     * @param position The position.
     * @return True if a header is at the position.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isHeader(int position) {
        return header != null && position == 0;
    }

    /**
     * Returns if a footer is at the specified position.
     *
     * @param position The position.
     * @return True if a footer is at the position.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isFooter(int position) {
        return footer != null && position == getFooterPosition();
    }

    /**
     * Sets the header (replaces if there was already one) and notifies the appropriate listeners.
     *
     * @param header The header.
     */
    public void setHeader(@Nullable View header) {
        boolean hadHeader = this.header != null;
        this.header = header;

        if (header == null) {
            if (hadHeader) {
                notifyItemRemoved(0);
            }
        } else {
            if (hadHeader) {
                notifyItemChanged(0);
            } else {
                notifyItemInserted(0);
            }
        }
    }

    /**
     * Sets the footer (replaces if there was already one) and notifies the appropriate listeners.
     *
     * @param footer The footer.
     */
    public void setFooter(@Nullable View footer) {
        boolean hadFooter = this.footer != null;
        this.footer = footer;

        if (footer == null) {
            if (hadFooter) {
                notifyItemRemoved(getFooterPosition());
            }
        } else {
            if (hadFooter) {
                notifyItemChanged(getFooterPosition());
            } else {
                notifyItemInserted(getFooterPosition());
            }
        }
    }

    /**
     * Removes the header and notifies the appropriate listeners if present.
     */
    public void removeHeader() {
        setHeader(null);
    }

    /**
     * Removes the footer and notifies the appropriate listeners if present.
     */
    public void removeFooter() {
        setFooter(null);
    }

    /**
     * Returns the inner adapter, passed in the constructor.
     *
     * @return The inner adapter.
     */
    public RecyclerView.Adapter getInnerAdapter() {
        return innerAdapter;
    }

    /**
     * Returns the position of the item in the inner adapter.
     *
     * @param position The raw position.
     * @return The real position.
     */
    public int getRealPosition(int position) {
        return position - (hasHeader() ? 1 : 0);
    }

    private int getDelegatedPosition(int position) {
        return position + (hasHeader() ? 1 : 0);
    }


    private int getFooterPosition() {
        return innerAdapter.getItemCount() + (hasHeader() ? 1 : 0);
    }

    private void initLayoutManager() {
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager castedLayoutManager = (GridLayoutManager) layoutManager;
            final SpanSizeLookup existingLookup = castedLayoutManager.getSpanSizeLookup();

            castedLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isHeader(position) || isFooter(position)) {
                        return castedLayoutManager.getSpanCount();
                    }

                    return existingLookup.getSpanSize(position);
                }
            });
        }
    }

    private void bind(HeaderFooterViewHolder holder, int position) {
        View viewToAdd = isHeader(position) ? header : footer;

        if (viewToAdd.getParent() != null) {
            ((ViewGroup) viewToAdd.getParent()).removeView(viewToAdd);
        }

        ((ViewGroup) holder.itemView).addView(viewToAdd);

        ViewGroup.LayoutParams layoutParams;

        if (layoutManager instanceof StaggeredGridLayoutManager) {
            if (viewToAdd.getLayoutParams() == null) {
                layoutParams = new StaggeredGridLayoutManager.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
            } else {
                layoutParams = new StaggeredGridLayoutManager.LayoutParams(
                        viewToAdd.getLayoutParams().width,
                        viewToAdd.getLayoutParams().height
                );
            }

            ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
        } else {
            if (viewToAdd.getLayoutParams() == null) {
                layoutParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
            } else {
                layoutParams = new ViewGroup.LayoutParams(
                        viewToAdd.getLayoutParams().width,
                        viewToAdd.getLayoutParams().height
                );
            }
        }

        holder.itemView.setLayoutParams(layoutParams);
    }

    private static class HeaderFooterViewHolder extends RecyclerView.ViewHolder {

        HeaderFooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
