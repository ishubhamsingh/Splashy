package dev.ishubhamsingh.splashy.core.utils

/**
 * Created by Shubham Singh on 19/08/23.
 */
expect class FileUtils {

    suspend fun saveByteArrayToFile(fileName: String, data: ByteArray)

    suspend fun showMessage(message: String)
}