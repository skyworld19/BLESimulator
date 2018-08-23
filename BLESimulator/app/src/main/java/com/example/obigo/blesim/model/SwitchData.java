package com.example.obigo.blesim.model;

public class SwitchData {
    private boolean isChecked;
    private int dataStatus;

    public SwitchData(boolean isChecked, int dataStatus) {
        this.isChecked = isChecked;
        this.dataStatus = dataStatus;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(int dataStatus) {
        this.dataStatus = dataStatus;
    }
}
