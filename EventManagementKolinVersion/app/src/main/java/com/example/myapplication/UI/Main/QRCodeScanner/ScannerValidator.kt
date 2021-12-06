package com.example.myapplication.UI.Main.QRCodeScanner

object ScannerValidator {

    fun isFireBasePath(eventId:String): Boolean{
        if(eventId.contains('.') || eventId.contains('#')||eventId.contains('$')||eventId.contains('[')|| eventId.contains(']')){
            return false
        }
        return true
    }
}