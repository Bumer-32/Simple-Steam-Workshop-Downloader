package ua.pp.lumivoid

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.slf4j.LoggerFactory


fun main() {
    val logger = LoggerFactory.getLogger("MainKt")

    try {
        Main.main()
    } catch (e: Exception) {
        logger.error("Fatal error when running program")
        e.stackTrace.forEach { trace -> logger.error(trace.toString()) }
        throw e
    }
}

object Main {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    val httpClient = HttpClient(CIO)

    fun main() {
        val startTime = System.currentTimeMillis()

        logger.info("""Starting Simple Steam Workshop Downloader
        
            ░██████╗░██████╗░██╗░░░░░░░██╗██████╗░
            ██╔════╝██╔════╝░██║░░██╗░░██║██╔══██╗
            ╚█████╗░╚█████╗░░╚██╗████╗██╔╝██║░░██║
            ░╚═══██╗░╚═══██╗░░████╔═████║░██║░░██║
            ██████╔╝██████╔╝░░╚██╔╝░╚██╔╝░██████╔╝
            ╚═════╝░╚═════╝░░░░╚═╝░░░╚═╝░░╚═════╝░
        """.trimIndent())
        logger.info("Hello from Bumer_32!")
        logger.info("")
        logger.info("")
        logger.info("")

        SteamCMD.prepare()

        val downloadInfo = Parser.prepareInfo()
        SteamCMD.download(downloadInfo)

        logger.info("SSWD ended work  in ${System.currentTimeMillis() - startTime} ms")
    }
}