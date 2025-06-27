package ua.pp.lumivoid

import org.slf4j.LoggerFactory


fun main() {
    val logger = LoggerFactory.getLogger("main")

    logger.trace("This is trace log")
    logger.debug("This is debug log")
    logger.info("This is info log")
    logger.warn("This is warn log")
    logger.error("This is error log")
}