package ua.pomo.catalog
import cats.effect.IO
import ch.qos.logback.classic.{Level, LoggerContext}
import ch.qos.logback.classic.layout.TTLLLayout
import ch.qos.logback.classic.spi.Configurator
import ch.qos.logback.core.{ConsoleAppender, FileAppender}
import ch.qos.logback.core.encoder.LayoutWrappingEncoder
import ch.qos.logback.core.spi.ContextAwareBase
import ch.qos.logback.classic.spi.ILoggingEvent

class LoggingConfigurator extends ContextAwareBase with Configurator {
  override def configure(lc: LoggerContext): Unit = {
    addInfo("Setting up default configuration.")
    // same as
    // PatternLayout layout = new PatternLayout();
    // layout.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
    val layout = new TTLLLayout
    layout.setContext(lc)
    layout.start()

    val encoder = new LayoutWrappingEncoder[ILoggingEvent]
    encoder.setContext(lc)
    encoder.setLayout(layout)

    val ca = new ConsoleAppender[ILoggingEvent]
    ca.setContext(lc)
    ca.setName("consoleAppender")
    ca.setEncoder(encoder)
    ca.start()

    val logFile = System.getProperty("LOG_FILE")
    val fa = new FileAppender[ILoggingEvent]
    fa.setContext(lc)
    fa.setName("fileAppender")
    fa.setFile(logFile)
    fa.setEncoder(encoder)
    fa.start()

    val rootLogger = lc.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
    rootLogger.setLevel(Level.INFO)
    rootLogger.addAppender(ca)
    rootLogger.addAppender(fa)

  }
}
