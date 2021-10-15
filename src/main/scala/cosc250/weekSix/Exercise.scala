package cosc250.weekSix

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.json
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.ExecutionContext.Implicits.global

object Exercise {


  /*
   * First, let's just do some basic things with Promise and Future
   */


  /**
   * Just complete this promise with a number. Have a look at what the test is doing
   */
  def completeMyPromise(p: Promise[Int]): Unit = {
    p.success(1)
    /*
    val future = p.future
    future.onSuccess({case x => println (x) })

    promise.success(1)
    // this is only for apps?
    //
    try {
      Thread.sleep(1000)
    } catch {
      case _ ->
    }

     */
  }

  /**
   * I'm going to give you a Future[Int]. Double it and return it.
   * Have a look at what the test is doing
   */
  def doubleMyFuture(p: Future[Int]): Future[Int] = {
    p.map(_ * 2)
  }


  /**
   * Let's chain a few things together.
   * I'm going to give you two Future[String]s. You're going to convert them both to uppercase, and count how many
   * letters are identical in each
   *
   * Hint: use for { a <- fut } notation
   *
   * Don't use isComplete.
   */
  def compareMyFutureStrings(fs1: Future[String], fs2: Future[String]): Future[Int] = {
    for {
      s1 <- fs1.map(_.toUpperCase)
      s2 <- fs2.map(_.toUpperCase)
    } yield s1.zip(s2).count({ case (a, b) => a == b })
  }

  /**
   * Here's an example of parsing a JSON string
   */
  def nameFromJason() = {
    val json: JsValue = Json.parse(
      """
      {
        "name" : "Watership Down",
        "location" : {
          "lat" : 51.235685,
          "long" : -1.309197
        },
        "residents" : [ {
          "name" : "Fiver",
          "age" : 4,
          "role" : null
        }, {
          "name" : "Bigwig",
          "age" : 6,
          "role" : "Owsla"
        } ]
      }
     """)

    val name = (json \ "name").as[String]

    name
  }


  /*
   * This stuff sets up our web client
   */
  implicit val system: ActorSystem = ActorSystem("Sys")
  val wsClient = StandaloneAhcWSClient()


  /**
   * Here's an example of using the Web Client.
   */
  def webExample() = {
    wsClient
      .url("http://turing.une.edu.au/~cosc250/lectures/cosc250/test.txt")
      .get()
      .map(_.body)
  }


  /**
   * Your first challenge...
   *
   * Get the file http://turing.une.edu.au/~cosc250/lectures/cosc250/second.json and extract the name from the JSON
   */
  def secondName(): Future[String] = {
    val body = wsClient
      .url("http://turing.une.edu.au/~cosc250/lectures/cosc250/second.json")
      .get()
      .map(_.body)

    for {
      jsText <- body
    } yield {
      val json = Json.parse(jsText)
      (json \ "name").as[String]
    }
  }

  /**
   * Your second challenge...
   *
   * Get the file from url1
   * Get the file from url2
   * Parse them each as JSON
   * and case insensitively see how many characters are in common in the two names...
   */
  def nameCharactersInCommon(url1: String, url2: String): Future[Int] = {

    def getName(url: String): Future[String] = {
      val body = wsClient
        .url(url)
        .get()
        .map(_.body)

      for {
        jsText <- body
      } yield {
        val json = Json.parse(jsText)
        (json \ "name").as[String]
      }
    }

    val fn1 = getName(url1)
    val fn2 = getName(url2)

    compareMyFutureStrings(fn1, fn2) // works because compare A to A or a (convert to A), so A to A
  }
/*
    val lc_comparisons = for {
      s1 <- fn1
      s2 <- fn2
    } yield s1.zip(s2).count({ case (a, b) => a == b })

    val t_comparisons = up_comparisons+lc_comparisons
    t_comparisons
  }

 */

}
/*
    val body1 = wsClient
      .url(url1)
      .get()
      .map(_.body)

    val body2 = wsClient
      .url(url2)
      .get()
      .map(_.body

    for {
      s1 <- fs1.map(_.toUpperCase)
      s2 <- fs2.map(_.toUpperCase)
    } yield s1.zip(s2).count({case (a, b) => a == b})
  }

    val i_comparisons = for {
      l1 <- body1
      l2 <- body2
    } yield l1.zip(l2).count({case (a, b) => a == b})

    val u_comparisons = for {
      u1 <- body1.map(_.toUpperCase)
      u2 <- body2.map(_.toUpperCase)
    } yield u1.zip(u2).count({case (a, b) => a == b})
  }

    val t_comp = i_comparisons ++ u_comparisons
    t_comp.length

 */
