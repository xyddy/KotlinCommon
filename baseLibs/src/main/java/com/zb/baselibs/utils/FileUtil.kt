package com.zb.baselibs.utils

import com.zb.baselibs.app.BaseApp
import java.io.File
import java.util.*

/**
 * 图片地址
 */
fun getImageFile(): File {
    val imagePath = File(BaseApp.context.cacheDir, "images")
    if (!imagePath.exists()) {
        imagePath.mkdirs()
    }
    return File(imagePath, randomString(15) + ".jpg")
}

/**
 * 视频文件
 */
fun getVideoFile(): File {
    val videoPath = File(BaseApp.context.cacheDir, "videos")
    if (!videoPath.exists()) {
        videoPath.mkdirs()
    }
    return File(videoPath, randomString(15) + ".mp4")
}

/**
 * 语音文件
 */
fun getAudioFile(): File {
    val audioPath = File(BaseApp.context.cacheDir, "audios")
    if (!audioPath.exists()) {
        audioPath.mkdirs()
    }
    return File(audioPath, randomString(15) + ".amr")
}

/**
 * 下载文件
 */
fun getDownloadFile(fileType: String): File {
    val downPath = File(BaseApp.context.cacheDir, "downFiles")
    if (!downPath.exists()) {
        downPath.mkdirs()
    }
    return File(downPath, randomString(15) + fileType)
}

/**
 * 小米聊天
 */
fun getLogCachePath(): String {
    val file = File(BaseApp.context.cacheDir, "logCachePath")
    if (!file.exists()) {
        file.mkdirs()
    }
    return file.path
}

/**
 * 小米聊天
 */
fun getTokenCachePath(): String {
    val file = File(BaseApp.context.cacheDir, "tokenCachePath")
    if (!file.exists()) {
        file.mkdirs()
    }
    return file.path
}

/**
 * 随机选取资源名称
 */
fun randomString(length: Int): String {
    val randGen = Random()
    val numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
            + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray()
    val randBuffer = CharArray(length)
    for (i in randBuffer.indices) {
        randBuffer[i] = numbersAndLetters[randGen.nextInt(71)]
    }
    return String(randBuffer)
}