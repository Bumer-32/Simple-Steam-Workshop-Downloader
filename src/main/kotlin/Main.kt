package ua.pp.lumivoid

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.slf4j.LoggerFactory


fun main() {
    Main.main()
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