package com.example.myapplication.ui.main.qrcodescanner

object ScannerValidator {

    fun isFireBasePath(eventId:String): Boolean{
        if(eventId.contains('.') || eventId.contains('#')||eventId.contains('$')||eventId.contains('[')|| eventId.contains(']')){
            return false
        }
        return true
    }
}