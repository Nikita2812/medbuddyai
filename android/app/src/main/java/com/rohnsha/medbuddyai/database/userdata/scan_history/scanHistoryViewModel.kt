package com.rohnsha.medbuddyai.database.userdata.scan_history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.rohnsha.medbuddyai.database.userdata.userDataDB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class scanHistoryViewModel(application: Application): AndroidViewModel(application) {

    private val scanHistoryRepo: scanHistoryRepo
    private val scanHistoryDAO: scanHistoryDAO
    private var _scanHistoryEntries= MutableStateFlow<List<scanHistory>>(emptyList())
    val scanHistoryEntries= _scanHistoryEntries.asStateFlow()

    private val _scanEntriesBeforeAdd = MutableStateFlow<Int>(Int.MAX_VALUE)
    val scanEntriesBeforeAdd= _scanEntriesBeforeAdd.asStateFlow()

    init {
        scanHistoryDAO= userDataDB.getUserDBRefence(application).scanDAO()
        scanHistoryRepo= scanHistoryRepo(scanHistoryDAO)
    }

    suspend fun addScanHistory(scanHistory: scanHistory): Boolean {

        _scanEntriesBeforeAdd.value= scanHistoryRepo.getScanHistoryCount()
        scanHistoryRepo.addScanHistory(scanHistory)
        val scanEntriesAfterAdd: Int = scanHistoryRepo.getScanHistoryCount()
        return scanEntriesAfterAdd!=_scanEntriesBeforeAdd.value
    }

    suspend fun getRecentScan(): scanHistory{
        return scanHistoryRepo.getRecentEntry()
    }

    suspend fun getScanDataByTimestamp(timeStamp: Long): scanHistory {
        return scanHistoryRepo.getScanDataByTimeStamp(timeStamp)
    }

    suspend fun deleteScanHistory(userIndex: Int){
        scanHistoryRepo.deleteScanHistory(userIndex)
    }

    suspend fun readScanHistory(){
        scanHistoryDAO.readScanHistory().collect {
            Log.d("dataDBA", it.toString())
            _scanHistoryEntries.value=it
        }
    }

}