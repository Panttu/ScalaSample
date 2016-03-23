package config

import com.typesafe.config.ConfigFactory
import util.Try

trait Configuration {
	val config = ConfigFactory.load()

  // Host for the calculator service.
  lazy val serviceHost = Try(config.getString("service.host")).getOrElse("localhost")

  // Port for the calculator service.
  lazy val servicePort = Try(config.getInt("service.port")).getOrElse(8080)
 	
 }