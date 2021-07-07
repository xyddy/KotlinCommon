package com.zb.baselibs.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import okhttp3.internal.and
import java.io.*
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

object Mac {
    fun getMac(context: AppCompatActivity): String? {
        val strMac: String?
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            strMac = getLocalMacAddressFromWifiInfo(context)
            strMac
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            strMac = getMacAddress(context)
            strMac
        } else {
            if (!TextUtils.isEmpty(macAddress)) {
                strMac = macAddress
                strMac
            } else if (!TextUtils.isEmpty(machineHardwareAddress)) {
                strMac = machineHardwareAddress
                strMac
            } else {
                strMac = localMacAddressFromBusybox
                strMac
            }
        }
    }

    /**
     * 根据wifi信息获取本地mac
     *
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    private fun getLocalMacAddressFromWifiInfo(context: Context): String {
        val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val winfo = wifi.connectionInfo
        return winfo.macAddress
    }

    /**
     * android 6.0及以上、7.0以下 获取mac地址
     *
     * @param context
     * @return
     */
   private fun getMacAddress(context: Context): String {

        // 如果是6.0以下，直接通过wifimanager获取
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val macAddress0 = getMacAddress0(context)
            if (!TextUtils.isEmpty(macAddress0)) {
                return macAddress0
            }
        }
        var str = ""
        var macSerial = ""
        try {
            val pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address")
            val ir = InputStreamReader(pp.inputStream)
            val input = LineNumberReader(ir)
            while (true) {
                str = input.readLine()
                if (str != null) {
                    macSerial = str.trim { it <= ' ' } // 去空格
                    break
                }
            }
        } catch (ignored: Exception) {
        }
        if ("" == macSerial) {
            try {
                return loadFileAsString()
                    .toUpperCase(Locale.ROOT).substring(0, 17)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return macSerial
    }

    @SuppressLint("HardwareIds")
    private fun getMacAddress0(context: Context): String {
        if (isAccessWifiStateAuthorized(context)) {
            val wifiMgr =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo
            try {
                wifiInfo = wifiMgr.connectionInfo
                return wifiInfo.macAddress
            } catch (ignored: Exception) {
            }
        }
        return ""
    }

    /**
     * Check whether accessing wifi state is permitted
     *
     * @param context
     * @return
     */
    private fun isAccessWifiStateAuthorized(context: Context): Boolean {
        return PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE")
    }

    @Throws(Exception::class)
    private fun loadFileAsString(): String {
        val reader = FileReader("/sys/class/net/eth0/address")
        val text = loadReaderAsString(reader)
        reader.close()
        return text
    }

    @Throws(Exception::class)
    private fun loadReaderAsString(reader: Reader): String {
        val builder = StringBuilder()
        val buffer = CharArray(4096)
        var readLength = reader.read(buffer)
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength)
            readLength = reader.read(buffer)
        }
        return builder.toString()
    }// 获得IpD地址

    /**
     * 根据IP地址获取MAC地址
     *
     * @return
     */
    val macAddress: String?
        get() {
            var strMacAddr: String? = null
            try {
                // 获得IpD地址
                val ip = localInetAddress
                val b = NetworkInterface.getByInetAddress(ip)
                    .hardwareAddress
                val buffer = StringBuilder()
                for (i in b.indices) {
                    if (i != 0) {
                        buffer.append(':')
                    }
                    val str = Integer.toHexString(b[i] and 0xFF)
                    buffer.append(if (str.length == 1) "0$str" else str)
                }
                strMacAddr = buffer.toString().toUpperCase(Locale.ROOT)
            } catch (ignored: Exception) {
            }
            return strMacAddr
        }// 得到一个ip地址的列举// 得到下一个元素// 是否还有元素// 列举

    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    private val localInetAddress: InetAddress?
        get() {
            var ip: InetAddress? = null
            try {
                // 列举
                val en_netInterface = NetworkInterface
                    .getNetworkInterfaces()
                while (en_netInterface.hasMoreElements()) { // 是否还有元素
                    val ni = en_netInterface
                        .nextElement() as NetworkInterface // 得到下一个元素
                    val en_ip = ni.inetAddresses // 得到一个ip地址的列举
                    while (en_ip.hasMoreElements()) {
                        ip = en_ip.nextElement()
                        ip = if (!ip.isLoopbackAddress
                            && !ip.hostAddress.contains(":")
                        ) {
                            break
                        } else {
                            null
                        }
                    }
                    if (ip != null) {
                        break
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return ip
        }

    /**
     * 获取本地IP
     *
     * @return
     */
    private val localIpAddress: String?
        get() {
            try {
                val en = NetworkInterface
                    .getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf
                        .inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress) {
                            return inetAddress.hostAddress
                        }
                    }
                }
            } catch (ex: SocketException) {
                ex.printStackTrace()
            }
            return null
        }

    /**
     * 获取设备HardwareAddress地址
     *
     * @return
     */
    val machineHardwareAddress: String?
        get() {
            var interfaces: Enumeration<NetworkInterface>? = null
            try {
                interfaces = NetworkInterface.getNetworkInterfaces()
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            var hardWareAddress: String? = null
            var iF: NetworkInterface
            if (interfaces == null) {
                return null
            }
            while (interfaces.hasMoreElements()) {
                iF = interfaces.nextElement()
                try {
                    hardWareAddress = bytesToString(iF.hardwareAddress)
                    if (hardWareAddress != null) {
                        break
                    }
                } catch (e: SocketException) {
                    e.printStackTrace()
                }
            }
            return hardWareAddress
        }

    /***
     * byte转为String
     *
     * @param bytes
     * @return
     */
    private fun bytesToString(bytes: ByteArray?): String? {
        if (bytes == null || bytes.isEmpty()) {
            return null
        }
        val buf = StringBuilder()
        for (b in bytes) {
            buf.append(String.format("%02X:", b))
        }
        if (buf.isNotEmpty()) {
            buf.deleteCharAt(buf.length - 1)
        }
        return buf.toString()
    }// 如果返回的result == null，则说明网络不可取
    // 对该行数据进行解析
    // 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
    /**
     * 根据busybox获取本地Mac
     */
    private val localMacAddressFromBusybox: String
        get() {
            var result: String
            val Mac: String
            result = callCmd()
            // 如果返回的result == null，则说明网络不可取
            // 对该行数据进行解析
            // 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
            if (result.isNotEmpty() && result.contains("HWaddr")) {
                Mac = result.substring(
                    result.indexOf("HWaddr") + 6,
                    result.length - 1
                )
                result = Mac
            }
            return result
        }

    private fun callCmd(): String {
        var result = StringBuilder()
        var line: String?
        try {
            val proc = Runtime.getRuntime().exec("busybox ifconfig")
            val `is` = InputStreamReader(proc.inputStream)
            val br = BufferedReader(`is`)
            while (br.readLine().also { line = it } != null
                && !line!!.contains("HWaddr")) {
                result.append(line)
            }
            result = line?.let { StringBuilder(it) }!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result.toString()
    }
}
