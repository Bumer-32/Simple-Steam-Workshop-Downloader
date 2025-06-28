package ua.pp.lumivoid

import com.pty4j.PtyProcessBuilder
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.zip.ZipInputStream
import kotlin.system.exitProcess

object SteamCMD {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private fun check(): Boolean {
        logger.info("Checking is SteamCMD available")

        val folder = File(Constants.STEAMCMD_FOLDER_PATH)
        if (!folder.exists()) {
            logger.warn("SteamCMD folder does not exist")
            folder.mkdirs()
            return false
        }

        val file = File(folder, "steamcmd.exe")
        if (!file.exists()) {
            logger.warn("SteamCMD binary file does not exist")
            return false
        }

        logger.info("")
        return true
    }

    private suspend fun downloadAndUnpack() {
        val folder = File(Constants.STEAMCMD_FOLDER_PATH)
        val file = File(folder, "steamcmd.exe")
        val archive = File(folder, "steamcmd.zip")
        if (archive.exists()) archive.delete()

        logger.info("Downloading steamcmd...")

        val startTime = System.currentTimeMillis()

        val response = Main.httpClient.get(Constants.STEAMCMD_DOWNLOAD_URL)

        if (response.status.isSuccess()) {
            val data = response.body<ByteArray>()
            archive.writeBytes(data)

            logger.info("Downloaded steamcmd in ${System.currentTimeMillis() - startTime}ms")
        } else {
            logger.error("SteamCMD download failed in ${System.currentTimeMillis() - startTime}ms, HTTP status: ${response.status.value}")

            Thread.sleep(5000)
            exitProcess(1)
        }

        logger.info("Unzipping steamcmd...")

        ZipInputStream(FileInputStream(archive)).use { zip ->
            zip.nextEntry
            file.outputStream().use { output ->
                zip.copyTo(output)
            }
        }

        logger.info("Deleting archive...")
        archive.delete()
    }

    fun prepare() {
        logger.info("Preparing steamCMD")
        val exists = check()

        if (!exists) runBlocking { downloadAndUnpack() }
    }

    fun download(downloadInfo: List<SingleWorkshopInfo>) {
        val process = PtyProcessBuilder().setCommand(arrayOf(Constants.STEAMCMD_FOLDER_PATH + "/steamcmd.exe")).start()

        val input = BufferedReader(InputStreamReader(process.inputStream))
        val output = BufferedWriter(OutputStreamWriter(process.outputStream))

        output.write("login anonymous\n")

        var modsFolder = File(Constants.SELF_PATH, "mods")

        val customPath = File(Constants.SELF_PATH, "path.txt")
        if (customPath.exists()) {
            modsFolder = File(customPath.readLines()[0])
        }

        if (!modsFolder.exists()) modsFolder.mkdirs()

        val workshopFolder = File(Constants.STEAMCMD_FOLDER_PATH, "steamapps/workshop/content/")
        if (workshopFolder.exists() and workshopFolder.listFiles().isNotEmpty()) {
            workshopFolder.listFiles().forEach { file -> file.delete() }
        }

        var downloaded = 0

        Thread {
            var line: String?
            while (input.readLine().also { line = it } != null) {
                logger.info("$line")

                if (line!!.startsWith("Success. Downloaded item")) {
                    logger.info("Downloaded file, try to copy")

                    val id = line.split("Downloaded item ")[1].split(" to")[0]
                    val workshopInfo = downloadInfo.find { it.id == id }!!

                    val folder = File(workshopFolder, "${workshopInfo.gameId}/$id")

                    if (folder.listFiles().isNotEmpty()) {
                        folder.listFiles().forEach { file ->
                            val newName = workshopInfo.name.replace(Regex("[\\\\/:*?\"<>|]"), "_")
                            val newFile = File(modsFolder, "$newName.${file.extension}")
                            file.renameTo(newFile)
                            logger.info("Copied and renamed file from ${file.absolutePath} to ${newFile.absolutePath}")
                        }
                    }

                    downloaded++
                    folder.delete()
                }
            }
        }.start()

        downloadInfo.forEach { element ->
            output.write("workshop_download_item ${element.gameId} ${element.id}\n")
            output.flush()
        }

        output.write("quit\n")
        output.flush()

        process.waitFor()

        logger.info("steamCMD ended work!")
        logger.info("Downloaded $downloaded workshops")
    }
}