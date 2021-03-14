package io.getquill.examples

import io.getquill.{ Literal, PostgresZioJdbcContext }
import io.getquill.context.ZioJdbc.Layers
import zio.console.putStrLn
import zio.Runtime

object PlainApp {

  object MyPostgresContext extends PostgresZioJdbcContext(Literal)
  import MyPostgresContext._

  case class Person(name: String, age: Int)

  val zioConn =
    Layers.dataSourceFromPrefix("testPostgresDB") >>> Layers.dataSourceToConnection

  def main(args: Array[String]): Unit = {
    val people = quote {
      query[Person].filter(p => p.name == "Alex")
    }
    val qzio =
      MyPostgresContext.run(people)
        .tap(result => putStrLn(result.toString))
        .provideCustomLayer(zioConn)

    Runtime.default.unsafeRun(qzio)
    ()
  }
}
