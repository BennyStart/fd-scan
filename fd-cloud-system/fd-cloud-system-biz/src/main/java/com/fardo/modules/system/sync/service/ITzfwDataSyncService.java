package com.fardo.modules.system.sync.service;

public interface ITzfwDataSyncService {

    String DB_KEY = "TZFW_DB";

    /**
     * 同步机构用户数据
     */
    void syncDepartAndUserData();
}
