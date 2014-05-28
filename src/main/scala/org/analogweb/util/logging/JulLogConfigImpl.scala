package org.analogweb.util.logging

import java.util.logging.{ ConsoleHandler, Level, Logger, LogManager }

class JulLogConfigImpl extends JulLogConfig {
  override def createLoggerInternal(name: String, manager: LogManager): Logger = {
    var logger = Logger.getLogger(name)
    logger.setLevel(Level.FINE)
    logger.addHandler(createConsoleHandler)
    manager.addLogger(logger)
    logger
  }
  def createConsoleHandler: ConsoleHandler = {
    var console = new ConsoleHandler()
    console.setFormatter(new JulLogFormatter())
    console.setLevel(Level.FINE)
    console;
  }
}
