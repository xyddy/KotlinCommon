package com.zb.baselibs.utils

import android.annotation.SuppressLint
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.math.BigDecimal

object DataCleanManager {
    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)
     *
     * @param context
     */
    fun cleanInternalCache(context: AppCompatActivity) {
        deleteFilesByDirectory(context.cacheDir)
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases)
     *
     * @param context
     */
    @SuppressLint("SdCardPath")
    fun cleanDatabases(context: AppCompatActivity) {
        deleteFilesByDirectory(File("/data/data/${context.packageName}/databases"))
    }

    /**
     * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs)
     *
     * @param context
     */
    @SuppressLint("SdCardPath")
    fun cleanSharedPreference(context: AppCompatActivity) {
        deleteFilesByDirectory(File("/data/data/${context.packageName}/shared_prefs"))
    }

    /**
     * 按名字清除本应用数据库
     *
     * @param context
     * @param dbName
     */
    fun cleanDatabaseByName(context: AppCompatActivity, dbName: String?) {
        context.deleteDatabase(dbName)
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容
     *
     * @param context
     */
    fun cleanFiles(context: AppCompatActivity) {
        deleteFilesByDirectory(context.filesDir)
    }

    /**
     * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     *
     * @param context
     */
    fun cleanExternalCache(context: AppCompatActivity) {
        if ((Environment.getExternalStorageState() ==
                    Environment.MEDIA_MOUNTED)
        ) {
            deleteFilesByDirectory(context.externalCacheDir)
        }
    }

    /**
     * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除
     *
     * @param file
     */
    fun cleanCustomCache(file: File?) {
        deleteFilesByDirectory(file)
    }

    /**
     * 清除本应用所有的数据
     *
     * @param context
     * @param filepath
     */
    fun cleanApplicationData(context: AppCompatActivity, vararg filepath: String?) {
        cleanInternalCache(context)
        cleanExternalCache(context)
        cleanDatabases(context)
        cleanSharedPreference(context)
        cleanFiles(context)
        for (filePath: String? in filepath) {
            cleanCustomCache(File(filePath!!))
        }
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理
     *
     * @param directory
     */
    private fun deleteFilesByDirectory(directory: File?) {
        if ((directory != null) && directory.exists() && directory.isDirectory) {
            for (item: File in directory.listFiles()) {
                item.delete()
            }
        }
    }

    fun deleteFile(file: File) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile) { // 判断是否是文件
                file.delete() // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory) { // 否则如果它是一个目录
                val files = file.listFiles() // 声明目录下所有的文件 files[];
                for (value: File in files) { // 遍历目录下所有的文件
                    deleteFile(value) // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete()
        }
    }

    /**
     * 格式化单位	 * 	 * @param size	 * @return
     */
    private fun getFormatSize(size: Long): String {
        val kiloByte: Float = size / 1024f
        //        if (kiloByte < 1) {
//            return size + "B";
//        }
        val megaByte: Float = kiloByte / 1024f
        //        if (megaByte < 1) {
//            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
//            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
//        }
        val gigaByte: Float = megaByte / 1024f
        if (gigaByte < 1) {
            val result2 = BigDecimal(megaByte.toString())
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
        }
        val teraBytes: Float = gigaByte / 1024f
        if (teraBytes < 1) {
            val result3 = BigDecimal(gigaByte.toString())
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes.toString())
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
    }

    // 获取文件
    private fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            for (value: File in fileList) {
                // 如果下面还有文件
                size += if (value.isDirectory) {
                    getFolderSize(value)
                } else {
                    value.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    fun getCacheSize(file: File): String {
        return getFormatSize(getFolderSize(file))
    }
}