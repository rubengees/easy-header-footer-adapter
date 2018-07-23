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
    public EasyHeaderFooterAdapter(@NonNull final RecyclerView.Adapter innerAdapter) {
        this.innerAdapter = innerAdapter;

        innerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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
                // fix bug. Because fromPosition may bigger then toPosition.
                // For now, itemCount must be 1. So just call notifyItemMoved
                // 目前 recycleView源码中，itemCount只能是1，因为只能通过adapter.notifyItemMoved触发。
                notifyItemMoved(getDelegatedPosition(fromPosition), getDelegatedPosition(toPosition));
            }
        });

        setHasStableIds(innerAdapter.hasStableIds());
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        initLayoutManager(recyclerView.getLayoutManager());

        innerAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        innerAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(final RecyclerView.ViewHolder holder) {
        if (!(holder instanceof HeaderFooterViewHolder)) {
            //noinspection unchecked
            innerAdapter.onViewAttachedToWindow(holder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        if (!(holder instanceof HeaderFooterViewHolder)) {
            //noinspection unchecked
            innerAdapter.onViewDetachedFromWindow(holder);
        }
    }

    @Override
    public void onViewRecycled(final RecyclerView.ViewHolder holder) {
        if (!(holder instanceof HeaderFooterViewHolder)) {
            //noinspection unchecked
            innerAdapter.onViewRecycled(holder);
        }
    }

    @Override
    public boolean onFailedToRecycleView(final RecyclerView.ViewHolder holder) {
        if (!(holder instanceof HeaderFooterViewHolder)) {
            //noinspection unchecked
            return innerAdapter.onFailedToRecycleView(holder);
        }

        return super.onFailedToRecycleView(holder);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if (viewType == TYPE_HEADER || viewType == TYPE_FOOTER) {
            return new HeaderFooterViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.easy_header_footer_adapter_item, parent, false));
        } else {
            return innerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position,
                                 final List<Object> payloads) {
        if (holder instanceof HeaderFooterViewHolder) {
            bind((HeaderFooterViewHolder) holder, position);
        } else {
            //noinspection unchecked
            innerAdapter.onBindViewHolder(holder, getRealPosition(position), payloads);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderFooterViewHolder) {
            bind((HeaderFooterViewHolder) holder, position);
        } else {
            //noinspection unchecked
            innerAdapter.onBindViewHolder(holder, getRealPosition(position));
        }
    }

    @Override
    public int getItemCount() {
        return innerAdapter.getItemCount() + (header != null ? 1 : 0) + (footer != null ? 1 : 0);
    }

    @Override
    public int getItemViewType(final int position) {
        if (isHeader(position)) {
            return TYPE_HEADER;
        } else if (isFooter(position)) {
            return TYPE_FOOTER;
        } else {
            return innerAdapter.getItemViewType(getRealPosition(position));
        }
    }

    @Override
    public long getItemId(final int position) {
        if (isHeader(position)) {
            return ID_HEADER;
        } else if (isFooter(position)) {
            return ID_FOOTER;
        } else {
            return innerAdapter.getItemId(getRealPosition(position));
        }
    }

    /**
     * Returns if a header is at the specified position.
     *
     * @param position The position.
     * @return True if a header is at the position.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isHeader(final int position) {
        return header != null && position == 0;
    }

    /**
     * Returns if a footer is at the specified position.
     *
     * @param position The position.
     * @return True if a footer is at the position.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isFooter(final int position) {
        return footer != null && position == getFooterPosition();
    }

    /**
     * Returns the header or null if none is set.
     *
     * @return The header.
     */
    @SuppressWarnings("unused")
    @Nullable
    public View getHeader() {
        return header;
    }

    /**
     * Sets the header (replaces if there was already one) and notifies the appropriate listeners.
     *
     * @param header The header.
     */
    public void setHeader(@Nullable final View header) {
        if (this.header == header) {
            return;
        }

        final boolean hadHeader = this.header != null;

        this.header = header;

        if (header == null) {
            notifyItemRemoved(0);
        } else {
            detachFromParent(header);

            if (hadHeader) {
                notifyItemChanged(0);
            } else {
                notifyItemInserted(0);
            }
        }
    }

    /**
     * Returns the footer or null if none is set.
     *
     * @return The footer.
     */
    @SuppressWarnings("unused")
    @Nullable
    public View getFooter() {
        return footer;
    }

    /**
     * Sets the footer (replaces if there was already one) and notifies the appropriate listeners.
     *
     * @param footer The footer.
     */
    public void setFooter(@Nullable final View footer) {
        if (this.footer == footer) {
            return;
        }

        final boolean hadFooter = this.footer != null;

        this.footer = footer;

        if (footer == null) {
            notifyItemRemoved(getFooterPosition());
        } else {
            if (hadFooter) {
                notifyItemChanged(getFooterPosition());
            } else {
                notifyItemInserted(getFooterPosition());
            }
        }
    }

    /**
     * Returns the inner adapter, passed in the constructor.
     *
     * @return The inner adapter.
     */
    @SuppressWarnings("unused")
    public RecyclerView.Adapter getInnerAdapter() {
        return innerAdapter;
    }

    /**
     * Returns the position of the item in the inner adapter.
     *
     * @param position The raw position.
     * @return The real position.
     */
    public int getRealPosition(final int position) {
        return position - (header != null ? 1 : 0);
    }

    private int getDelegatedPosition(final int position) {
        return position + (header != null ? 1 : 0);
    }

    private int getFooterPosition() {
        return innerAdapter.getItemCount() + (header != null ? 1 : 0);
    }

    private void initLayoutManager(final RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;

        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager castedLayoutManager = (GridLayoutManager) layoutManager;
            final SpanSizeLookup existingLookup = castedLayoutManager.getSpanSizeLookup();

            castedLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isHeader(position) || isFooter(position)) {
                        return castedLayoutManager.getSpanCount();
                    }

                    return existingLookup.getSpanSize(getRealPosition(position));
                }
            });
        }
    }

    private void bind(final HeaderFooterViewHolder holder, final int position) {
        final ViewGroup holderItemView = (ViewGroup) holder.itemView;
        final ViewGroup.LayoutParams layoutParams;
        final View viewToAdd;

        if (isHeader(position)) {
            viewToAdd = header;
        } else if (isFooter(position)) {
            viewToAdd = footer;
        } else {
            return;
        }

        detachFromParent(viewToAdd);
        holderItemView.removeAllViews();
        holderItemView.addView(viewToAdd);

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

    private void detachFromParent(@NonNull final View view) {
        final ViewGroup parent = (ViewGroup) view.getParent();

        if (parent != null) {
            parent.removeView(view);
        }
    }

    private static class HeaderFooterViewHolder extends RecyclerView.ViewHolder {
        HeaderFooterViewHolder(final View itemView) {
            super(itemView);
        }
    }
}
