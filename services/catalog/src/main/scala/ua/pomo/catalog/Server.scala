package ua.pomo.catalog

object Server extends App with ServerStarter {
  this.start()
  this.blockUntilShutdown()
}
