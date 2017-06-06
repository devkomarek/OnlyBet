package com.komarekzm.onlyBet.project.models.objects;

import java.util.List;


public class ListOfHistoryAndTips {

    private List<Tip> listTip;
    private List<Tip> listHistory;

    public List<Tip> getListTip() {
        return listTip;
    }

    public void setListTip(List<Tip> listTip) {
        this.listTip = listTip;
    }

    public List<Tip> getListHistory() {
        return listHistory;
    }

    public void setListHistory(List<Tip> listHistory) {
        this.listHistory = listHistory;
    }


}
