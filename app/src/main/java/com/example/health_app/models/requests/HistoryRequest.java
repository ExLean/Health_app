package com.example.health_app.models.requests;

import java.sql.Date;

public class HistoryRequest {

    private int historyId;
    private int userId;
    private Date historyDate;

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setHistoryDate(Date historyDate) {
        this.historyDate = historyDate;
    }
}
