package com.github.abhijitpparate.keeps.screen.home;


public interface NotesTouchHelper {

    interface Adapter {
        void onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
    }

    interface ViewHolder {
        void onItemSelected();
        void onItemClear();
    }

}
