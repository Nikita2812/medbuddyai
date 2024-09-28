package com.rohnsha.medbuddyai.database.userdata.scan_history

import kotlinx.coroutines.flow.Flow

class scanHistoryRepo(private val scanHistoryDAO: scanHistoryDAO) {

    var readSearchHistory: Flow<List<scanHistory>> = scanHistoryDAO.readScanHistory()

    suspend fun addScanHistory(scanHistory: scanHistory){
        scanHistoryDAO.addScanHistory(scanHistory)
    }

    suspend fun getScanHistoryCount(): Int{
        return scanHistoryDAO.getScanHistoryCounts()
    }

    suspend fun getRecentEntry(): scanHistory{
        return scanHistoryDAO.getMostRecentEntry()
    }

    suspend fun getScanDataByTimeStamp(timeStamp: Long): scanHistory {
        return  scanHistoryDAO.getScanDataByTimeStamp(timeStamp = timeStamp)
    }

    suspend fun deleteScanHistory(userIndnex: Int){
        scanHistoryDAO.deleteScanHistories(userIndex = userIndnex)
    }

}