package com.github.abhijitpparate.checklistview;


public class CheckListItem {

    private boolean isCheckList;
    private String item;
    private boolean isChecked;

    public CheckListItem(String item, boolean isChecked) {
        this.item = item;
        this.isChecked = isChecked;
        this.isCheckList = true;
    }

    public CheckListItem() {
        this.item = "";
        this.isChecked = false;
        this.isCheckList = false;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isCheckList() {
        return isCheckList;
    }

    public void setCheckList(boolean checkList) {
        isCheckList = checkList;
    }
}
