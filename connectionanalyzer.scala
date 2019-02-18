import java.time.Instant
import java.time.ZoneOffset
import scala.io.Source

if (args.length != 1) {
  println("Usage: connectionanalyzer <filename>")
  System.exit(1)
}

val filename = args(0)
val lines = Source.fromFile(filename).getLines().toList
val linesArray = lines.map(v => v.split(" "))
val dates = linesArray.map(v => Instant.ofEpochSecond(v(0).toLong))
val hours = dates.map(v => v.atZone(ZoneOffset.UTC).getHour()).distinct

for(hour <- hours){
  println("Serveur connecté à %d heures".format(hour))
  val servers = linesArray.filter(v => Instant.ofEpochSecond(v(0).toLong).atZone(ZoneOffset.UTC).getHour() == hour)
  val serverName = servers.flatten.filter(v => !servers.map(x => x(0)).contains(v))
  serverName.distinct.foreach(s => println("%s".format(s)))
  println("")

  val recentConnexion = servers.reduceLeft((x,y) => if (x(0) < y(0)) x else y)
  println("Connexion la plus récente à %d heures : %s, %s".format(hour, recentConnexion(1), recentConnexion(2)))
  println("")

  val highestConnexion = serverName.groupBy(identity).mapValues(_.size).maxBy(_._2)
  println("Serveur avec le plus de connexions à %d heures : %s".format(hour, highestConnexion._1))
  println("")
}

System.exit(0)