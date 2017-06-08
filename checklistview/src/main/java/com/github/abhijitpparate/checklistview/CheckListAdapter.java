package com.github.abhijitpparate.checklistview;


import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CheckListAdapter
        extends
            RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements
            ChecklistTouchHelperCallback.TouchHelperAdapter {

    private List<CheckListItem> checkListItems;

    private OnReturnPressListener returnPressListener;
    private OnChecklistAddedListener checklistAddedListener;
    private OnChecklistRemovedListener checklistRemovedListener;

    private OnStartDragListener startDragListener;

    private int editTextTextColor ;
    private int editTextHintColor ;

    public static final int CHECKLIST = 958;
    public static final int FOOTER = 123;

    public CheckListAdapter() {
        checkListItems = new ArrayList<>();
        checkListItems.add(new CheckListItem());
    }

    @Override
    public int getItemViewType(int position) {
        if (position == checkListItems.size() - 1) return FOOTER;
        return CHECKLIST;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView;
        if (viewType == FOOTER) {
            inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_checklist_footer, parent, false);
            return new CheckListFooter(inflatedView);
        } else{
            inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_checklist_item, parent, false);
            return new CheckListHolder(inflatedView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CheckListFooter){
            return;
        }
        CheckListItem checkListItem = checkListItems.get(position);
        CheckListHolder checkListHolder = (CheckListHolder) holder;
        checkListHolder.checkList.editText.setText(checkListItem.getItem());
        checkListHolder.checkList.editText.requestFocus();
        checkListHolder.checkList.checkBox.setChecked(checkListItem.isChecked());
    }

    @Override
    public int getItemCount() {
        return checkListItems.size();
    }

    public void addCheckList(int position){
        if (checkListItems == null) checkListItems = new ArrayList<>();
        checkListItems.add(position, new CheckListItem("", false));

        if (checklistAddedListener != null){
            checklistAddedListener.onChecklistAdded(position);
        }

        notifyItemInserted(position);
    }

    public void removeCheckList(int position){
        checkListItems.remove(position);

        if (checklistAddedListener != null){
            checklistAddedListener.onChecklistAdded(position);
        }

        notifyItemRemoved(position);
    }

    public void checkChecklist(int position){
        onItemMove(position, checkListItems.size() - 1);
    }

    public void addOnChecklistAddedListener(OnChecklistAddedListener listener){
        this.checklistAddedListener = listener;
    }

    public void addOnChecklistRemovedListener(OnChecklistRemovedListener listener){
        this.checklistRemovedListener = listener;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(checkListItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    public List<CheckListItem> getCheckList() {
        return checkListItems;
    }

    public JSONArray getCheckListJSON() throws JSONException {
        JSONArray array = new JSONArray();
        for (CheckListItem c : checkListItems) {
            if (c.isCheckList()){
                JSONObject o = new JSONObject();
                o.put("text", c.getItem());
                o.put("checked", c.isChecked());
                array.put(o);
            }
        }
        return array;
    }

    public void setCheckListColors(int textColor, int hintColor) {
        this.editTextTextColor = textColor;
        this.editTextHintColor = hintColor;
    }

    public void updateList(List<CheckListItem> checklist) {
        if (checklist != null && !checklist.isEmpty()){
            checkListItems.clear();
            checkListItems.addAll(checklist);
            checkListItems.add(new CheckListItem());
            notifyDataSetChanged();
        }
    }

    private class CheckListHolder
            extends RecyclerView.ViewHolder
            implements ChecklistTouchHelperCallback.TouchHelperViewHolder {

        CheckList checkList;

        public CheckListHolder(final View itemView) {
            super(itemView);
            checkList = (CheckList) itemView.findViewById(R.id.checklist);
//            checkList.editText.setTextColor(editTextTextColor);
//            checkList.editText.setHintTextColor(editTextHintColor);

            checkList.setOnReturnPressListener(new CheckList.OnReturnPressListener() {
                @Override
                public void onReturnPressed(CheckList checkList) {
                    if (returnPressListener != null){
                        returnPressListener.onReturnPressed(getLayoutPosition());
                    }
                    addCheckList(getLayoutPosition() + 1);
                }
            });

            checkList.setOnRemoveClickListener(new CheckList.OnRemoveClickListener() {
                @Override
                public void onRemoveClick(CheckList checkList) {
                    removeCheckList(getLayoutPosition());
                }
            });

            checkList.handle.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) ==
                            MotionEvent.ACTION_DOWN) {
                        startDragListener.onStartDrag(CheckListHolder.this);
                    }
                    return false;
                }
            });

            checkList.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    checkListItems.get(getLayoutPosition()).setItem(s.toString());
                }
            });

            checkList.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
//                        checkChecklist(getLayoutPosition());
                    }

                    checkListItems.get(getLayoutPosition()).setChecked(isChecked);
                    checkList.setEnabled(isChecked);
                    try {
                        Log.d("Adapter", "onCheckedChanged: " + getCheckListJSON().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public void setStartDragListener(OnStartDragListener startDragListener) {
        this.startDragListener = startDragListener;
    }

    private class CheckListFooter extends RecyclerView.ViewHolder {

        TextView addList;

        public CheckListFooter(View itemView) {
            super(itemView);
            addList = (TextView) itemView.findViewById(R.id.tvAddListItem);
            addList.setTextColor(Color.DKGRAY);
            addList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addCheckList(getLayoutPosition());
                }
            });
        }
    }

    interface OnReturnPressListener{
        void onReturnPressed(int position);
    }

    interface OnChecklistAddedListener {
        void onChecklistAdded(int position);
    }

    interface OnChecklistRemovedListener {
        void onChecklistRemoved(int position);
    }

    public interface OnStartDragListener {

        /**
         * Called when a view is requesting a start of a drag.
         *
         * @param viewHolder The holder of the view to drag.
         */
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }
}
