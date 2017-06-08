package com.github.abhijitpparate.checklistview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class CheckListView extends RelativeLayout implements CheckListAdapter.OnStartDragListener {

    public static final String TAG = "CheckListView";

    RecyclerView checklistRecyclerView;

    private int editTextTextColor ;
    private int editTextHintColor ;

    CheckListAdapter checkListAdapter;
    ItemTouchHelper itemTouchHelper;

    public CheckListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context, attrs);
    }

    public CheckListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context, attrs);
    }

    private void initializeViews(Context context, AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_ckecklistview, this);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CheckList);

        for (int i = 0; i < attributes.getIndexCount(); ++i) {
            int attr = attributes.getIndex(i);

            if (attr == R.styleable.CheckListView_checklistTextColor) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    editTextTextColor = attributes.getColor(attr, getResources().getColor(android.R.color.black, getContext().getTheme()));
                } else {
                    editTextTextColor = attributes.getColor(attr, getResources().getColor(android.R.color.black));
                }
            } else if (attr == R.styleable.CheckListView_checklistHintColor) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    editTextHintColor = attributes.getColor(attr, getResources().getColor(android.R.color.black, getContext().getTheme()));
                } else {
                    editTextHintColor = attributes.getColor(attr, getResources().getColor(android.R.color.black));
                }
            }
        }

        attributes.recycle();

        checklistRecyclerView = (RecyclerView) findViewById(R.id.listContainer);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (isInEditMode()){
            checklistRecyclerView = (RecyclerView) findViewById(R.id.listContainer);
            checklistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            checkListAdapter = new CheckListAdapter();
            checklistRecyclerView.setAdapter(checkListAdapter);
            for (int i = 0; i < 5; i++) {
                checkListAdapter.addCheckList(0);
            }
        }

        checklistRecyclerView.getRecycledViewPool().setMaxRecycledViews(CheckListAdapter.CHECKLIST,0);

        checklistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        checkListAdapter = new CheckListAdapter();
        checkListAdapter.setStartDragListener(this);
        checklistRecyclerView.setAdapter(checkListAdapter);
        checklistRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        checkListAdapter.setCheckListColors(editTextTextColor, editTextHintColor);

        ItemTouchHelper.Callback callback = new ChecklistTouchHelperCallback(checkListAdapter);

        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(checklistRecyclerView);


        checkListAdapter.addOnChecklistRemovedListener(new CheckListAdapter.OnChecklistRemovedListener() {
            @Override
            public void onChecklistRemoved(int position) {
                checklistRecyclerView.smoothScrollToPosition(position);
            }
        });
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    public String getCheckListAsString(){
        try {
            return checkListAdapter.getCheckListJSON().toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    public void setChecklist(String s) {

    }

    public void setChecklist(List<CheckListItem> checklist) {
        checkListAdapter.updateList(checklist);
    }
}
