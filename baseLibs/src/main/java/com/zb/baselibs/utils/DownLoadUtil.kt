package com.zb.baselibs.utils

import android.util.Log
import com.jiang.awesomedownloader.core.AwesomeDownloader
import com.zb.baselibs.bean.ResFile
import com.zb.baselibs.db.ResFileDb
import java.io.File

object DownLoadUtil {

    fun downLoad(url: String, file: File, callBack: CallBack?) {
        //加入下载队列
        AwesomeDownloader.enqueue(url, file.path, "")
        AwesomeDownloader.setOnProgressChange { progress ->
            callBack?.onProgress(progress)
        }.setOnFinished { mFilePath, mFileName ->
            Log.e("AwesomeDownloader", "setOnFinished:$mFilePath,$mFileName")
            ResFileDb.instance.insertData(ResFile(url, mFilePath))
            callBack?.onFinish(mFilePath)
        }.setOnError { exception ->
            exception.printStackTrace()
        }
    }

    interface CallBack {
        fun onFinish(filePath: String)
        fun onProgress(progress: Long) {}
    }
}