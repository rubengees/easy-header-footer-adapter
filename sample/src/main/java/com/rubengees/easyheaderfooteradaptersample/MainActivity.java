package com.rubengees.easyheaderfooteradaptersample;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rubengees.easyheaderfooteradapter.EasyHeaderFooterAdapter;
import com.rubengees.easyheaderfooteradaptersample.LayoutManager.LayoutManagerType;

/**
 * The main Activity of the sample.
 *
 * @author Ruben Gees
 */
public class MainActivity extends AppCompatActivity {

    private static final String LAYOUT_MANAGER_TYPE_STATE = "layout_manager_type_state";
    private static final String HEADER_STATE = "header_state";
    private static final String FOOTER_STATE = "footer_state";

    private RecyclerView recycler;
    private MainAdapter adapter;
    private EasyHeaderFooterAdapter headerFooterAdapter;

    @LayoutManagerType
    private int layoutManagerType;

    private ViewGroup root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            layoutManagerType = LayoutManager.LINEAR;
        } else {
            //noinspection WrongConstant
            layoutManagerType = savedInstanceState.getInt(LAYOUT_MANAGER_TYPE_STATE);
        }

        // Used for inflation later on
        root = (ViewGroup) findViewById(R.id.root);

        recycler = (RecyclerView) findViewById(R.id.recycler);
        adapter = new MainAdapter(savedInstanceState);
        headerFooterAdapter = new EasyHeaderFooterAdapter(adapter);

        adapter.setCallback(new MainAdapter.MainAdapterCallback() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this,
                        adapter.getItem(headerFooterAdapter.getRealPosition(position)).getText()
                                + " clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        if (savedInstanceState == null) {
            addRandomItems();
        }

        refreshRecycler();

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(HEADER_STATE)) {
                setHeader();
            }

            if (savedInstanceState.getBoolean(FOOTER_STATE)) {
                setFooter();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);

        switch (layoutManagerType) {
            case LayoutManager.LINEAR:
                menu.findItem(R.id.linear_layout_manager).setChecked(true);

                return true;
            case LayoutManager.GRID:
                menu.findItem(R.id.grid_layout_manager).setChecked(true);

                return true;
            case LayoutManager.STAGGERED_GRID:
                menu.findItem(R.id.staggered_grid_layout_manager).setChecked(true);

                return true;
            default:
                return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item:
                addRandomItem();

                return true;
            case R.id.remove_item:
                removeRandomItem();

                return true;
            case R.id.set_header:
                setHeader();

                return true;
            case R.id.set_footer:
                setFooter();

                return true;
            case R.id.remove_header:
                headerFooterAdapter.removeHeader();

                return true;
            case R.id.remove_footer:
                headerFooterAdapter.removeFooter();

                return true;
            case R.id.linear_layout_manager:
                layoutManagerType = LayoutManager.LINEAR;

                item.setChecked(true);
                refreshRecycler();
                return true;
            case R.id.grid_layout_manager:
                layoutManagerType = LayoutManager.GRID;

                item.setChecked(true);
                refreshRecycler();
                return true;
            case R.id.staggered_grid_layout_manager:
                layoutManagerType = LayoutManager.STAGGERED_GRID;

                item.setChecked(true);
                refreshRecycler();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(LAYOUT_MANAGER_TYPE_STATE, layoutManagerType);
        outState.putBoolean(HEADER_STATE, headerFooterAdapter.hasHeader());
        outState.putBoolean(FOOTER_STATE, headerFooterAdapter.hasFooter());
        adapter.saveInstanceState(outState);
    }

    private void setHeader() {
        View header = getLayoutInflater().inflate(R.layout.layout_header, root, false);

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Header clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        headerFooterAdapter.setHeader(header);
    }

    private void setFooter() {
        View footer = getLayoutInflater().inflate(R.layout.layout_footer, root, false);

        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Footer clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        headerFooterAdapter.setFooter(footer);
    }

    private void refreshRecycler() {
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(getLayoutManager());

        // Always set the adapter AFTER setting the LayoutManager.
        recycler.setAdapter(headerFooterAdapter);
    }

    private RecyclerView.LayoutManager getLayoutManager() {
        switch (layoutManagerType) {
            case LayoutManager.LINEAR:
                return new LinearLayoutManager(this);
            case LayoutManager.GRID:
                GridLayoutManager result = new GridLayoutManager(this, 2);

                result.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (position % 3 == 0) { // Something to show
                            return 2;
                        } else {
                            return 1;
                        }
                    }
                });

                return result;
            case LayoutManager.STAGGERED_GRID:
                return new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            default:
                throw new IllegalStateException("layoutManagerType has a unknown value: " +
                        layoutManagerType);
        }
    }

    private void addRandomItems() {
        for (int i = 0; i < 5; i++) {
            addRandomItem();
        }
    }

    private void addRandomItem() {
        // Horrible performance wise, but this is a sample app so we don't care.
        adapter.addItem(new Item("Sample item " + getRandomNumber(), getRandomColor()));
    }

    private void removeRandomItem() {
        if (adapter.getItemCount() > 0) {
            adapter.removeItem((int) (Math.random() * (adapter.getItemCount() - 1)));
        }
    }

    private int getRandomNumber() {
        return (int) (Math.random() * 10000);
    }

    @ColorInt
    private int getRandomColor() {
        TypedArray colors = getResources().obtainTypedArray(R.array.colors);

        int index = (int) (Math.random() * colors.length());
        int color = colors.getColor(index, Color.BLACK);

        colors.recycle();

        return color;
    }
}
