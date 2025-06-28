package ua.pp.lumivoid

import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess

object Parser {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private fun parseLinks(): List<String> {
        val folder = File(Constants.LINKS_FOLDER_PATH)
        if (!folder.exists()) folder.mkdirs()

        if (folder.listFiles().isEmpty()) {
            logger.error("No links files found")

            Thread.sleep(5000)
            exitProcess(0)
        }

        val linksList = mutableListOf<String>()

        folder.listFiles().forEach { file ->
            if (file.isFile && file.extension == "txt") {
                file.readLines().forEach { link ->
                    if (link.startsWith("http://") || link.startsWith("https://")) {
                        linksList.add(link)
                        logger.info("Found link $link")
                    }
                }
            }
        }

        return linksList
    }

    private fun parseSingleLink(link: String): SingleWorkshopInfo {
        val id = link.split("?id=")[1].split("&")[0]

        logger.info("Parsing link $link")

        val doc = Jsoup.connect(link).get()
        val title = doc.select(".workshopItemTitle").text()
        val gameId = doc.select("#ShareItemBtn").attr("onclick").split("'")[3]

        return SingleWorkshopInfo(link,title, gameId, id)
    }

    fun prepareInfo(): List<SingleWorkshopInfo> {
        val links = parseLinks()
        val downloadInfo = mutableListOf<SingleWorkshopInfo>()
//        var i = 0

        links.forEach { link ->
//            if (i >= 10) {
//                logger.info("Stopping for 15 seconds... For safety...")
//                Thread.sleep(15000)
//                i = 0
//            }

            val data = parseSingleLink(link)
            downloadInfo.add(data)
//            i++
        }

        return downloadInfo
    }
}