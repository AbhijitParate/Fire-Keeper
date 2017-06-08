package com.github.abhijitpparate.checklistview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;


public class CheckList extends LinearLayout {

    public static final String TAG = "Checklist";

    Button handle;
    CheckBox checkBox;
    EditText editText;
    Button remove;

    boolean isEnabled = true;

    boolean isChecked = false;

    String editTextText;
    String editTextHint;

    int editTextTextColor;
    int editTextHintColor;

    OnHandleClickListener onHandleClickListener;
    OnRemoveClickListener onRemoveClickListener;
    OnCheckedChangeListener onCheckedChangeListener;
    OnReturnPressListener onReturnPressListener;

    public CheckList(@NonNull Context context) {
        super(context);
        initializeViews(context, null);
    }

    public CheckList(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context, attrs);
    }

    public CheckList(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initializeViews(Context context, AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_checklist, this, true);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CheckList);

        for (int i = 0; i < attributes.getIndexCount(); ++i) {
            int attr = attributes.getIndex(i);
            if (attr == R.styleable.CheckList_text) {
                editTextText = attributes.getString(attr);
            } else if (attr == R.styleable.CheckList_hint) {
                editTextHint = attributes.getString(attr);
            } if (attr == R.styleable.CheckList_textColor) {
                editTextTextColor = attributes.getColor(attr, getResources().getColor(android.R.color.primary_text_light, getContext().getTheme()));
            } else if (attr == R.styleable.CheckList_hintColor) {
                editTextHintColor = attributes.getColor(attr, getResources().getColor(android.R.color.tertiary_text_light, getContext().getTheme()));
            } else if (attr == R.styleable.CheckList_checked){
                isChecked = attributes.getBoolean(attr, false);
            } else if (attr == R.styleable.CheckList_enabled){
                isEnabled = attributes.getBoolean(attr, false);
            }
        }

        attributes.recycle();

        handle = (Button) getRootView().findViewById(R.id.handle);
        checkBox = (CheckBox) getRootView().findViewById(R.id.checkbox);
        editText = (EditText) getRootView().findViewById(R.id.editText);
        remove = (Button) getRootView().findViewById(R.id.remove);

        remove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: remove");
                if (onRemoveClickListener != null)
                    onRemoveClickListener.onRemoveClick(CheckList.this);
            }
        });

        handle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: handle");
                if (onHandleClickListener != null)
                    onHandleClickListener.onHandleClick(CheckList.this);
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: ");
                setChecked(isChecked);
                if (onCheckedChangeListener != null)
                    onCheckedChangeListener.onCheckboxStateChanged(CheckList.this, isChecked);
            }
        });

        final boolean[] isTextEmpty = {false};
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG, "onKey: edittext");
                if (keyCode ==  KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                    onReturnPressListener.onReturnPressed(CheckList.this);
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DEL &&
                        event.getAction() == KeyEvent.ACTION_UP &&
                        editText.getText().toString().isEmpty()) {
                    if (isTextEmpty[0]) {
                        if (onRemoveClickListener != null)
                        onRemoveClickListener.onRemoveClick(CheckList.this);
                    }
                    else isTextEmpty[0] = true;
                    return true;
                } else if(keyCode == KeyEvent.KEYCODE_DEL &&
                        event.getAction() == KeyEvent.ACTION_UP){

                }
                return false;
            }
        });

        editText.requestFocus();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (isInEditMode()) {

            if (!isEnabled) {

                handle.setAlpha(0.5f);
                handle.setEnabled(false);

                checkBox.setAlpha(0.5f);
                checkBox.setEnabled(false);

                editText.setAlpha(0.5f);
                editText.setEnabled(false);

                remove.setAlpha(0.5f);
                remove.setEnabled(false);
            }

            editText.setText(editTextText);
            editText.setHint(editTextHint);

            if (editTextTextColor != 0)
                editText.setTextColor(editTextTextColor);

            if (editTextHintColor != 0)
                editText.setHintTextColor(editTextHintColor);

            checkBox.setChecked(isChecked);
        }
    }

    public void setChecked(boolean bool){
        this.isChecked = bool;
        if (isChecked) {
            handle.setEnabled(false);
            editText.setEnabled(false);
            remove.setEnabled(false);
        } else {
            handle.setEnabled(true);
            editText.setEnabled(true);
            remove.setEnabled(true);
        }
    }

    public void setEnabled(boolean bool){
        this.isEnabled = bool;
        if (isEnabled){
            handle.setEnabled(false);
            checkBox.setAlpha(0.5f);
            editText.setEnabled(false);
            remove.setEnabled(false);
        } else {
            handle.setEnabled(true);
            checkBox.setAlpha(1f);
            editText.setEnabled(true);
            remove.setEnabled(true);
        }
    }

    public void setText(String s){
        this.editText.setText(s);
    }

    public void setHint(String s){
        this.editText.setHint(s);
    }

    public void setOnHandleClickListener(OnHandleClickListener onHandleClickListener) {
        this.onHandleClickListener = onHandleClickListener;
    }

    public void setOnRemoveClickListener(OnRemoveClickListener onRemoveClickListener) {
        this.onRemoveClickListener = onRemoveClickListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    interface OnHandleClickListener {
        void onHandleClick(CheckList checkList);
    }

    interface OnRemoveClickListener {
        void onRemoveClick(CheckList checkList);
    }

    interface OnBackPressListener {
        void onBackPressClick(CheckList checkList);
    }

    interface OnCheckedChangeListener{
        void onCheckboxStateChanged(CheckList checkList, boolean isChecked);
    }

    interface OnReturnPressListener {
        void onReturnPressed(CheckList checkList);
    }

    public void setOnReturnPressListener(OnReturnPressListener onReturnPressListener) {
        this.onReturnPressListener = onReturnPressListener;
    }


    public JSONObject getCheckList() throws JSONException {
        return new JSONObject()
                .put("checked", checkBox.isChecked())
                .put("text", editText.getText().toString());
    }
}
