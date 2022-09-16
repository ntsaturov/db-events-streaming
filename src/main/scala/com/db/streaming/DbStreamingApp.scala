package com.db.streaming

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import com.db.streaming.db.model.Tables
import com.db.streaming.db.model.Tables.{Tasks, TasksRow}
import com.db.streaming.db.profile.CustomPostgresProfile
import com.db.streaming.db.profile.CustomPostgresProfile.api._
import slick.dbio.Effect
import slick.lifted.TableQuery

import java.sql.Timestamp
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Random, Success, Try}

object ResultCode {
  val SuccessCode = 20
  val ErrorCode = 21
}

object DbStreamingApp extends App {
  implicit val system: ActorSystem = ActorSystem("reactive")
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  def uuid = java.util.UUID.randomUUID.toString
  val db: CustomPostgresProfile.backend.Database = Database.forConfig("app.db")
  val tq = TableQuery[Tasks]

  def executor(item: Future[TasksRow]): Future[(TasksRow, Int)] = item.map {
    item =>
      println(item.id)
      (item, Random.between(0, 2))
  }

  def producer(conn: Session): Future[TasksRow] = {
    val testQuery = """UPDATE tasks
                      |SET status = 10, execution_timestamp = now()
                      |WHERE id = (
                      |  SELECT id
                      |  FROM tasks
                      |  WHERE status=0
                      |  ORDER BY creation_timestamp
                      |  FOR UPDATE SKIP LOCKED
                      |  LIMIT 1
                      |)
                      |RETURNING id, execution_timestamp, status, creation_timestamp, action, data""".stripMargin

    val query = testQuery

    val comp: DBIOAction[Option[Tables.TasksRow], NoStream, Effect with Effect.Transactional] = for {
      task <- sql"#$query".as[TasksRow].headOption.transactionally
    } yield task

    conn.database.run(comp).flatMap {
      case Some(value) =>
        println(value)
        Future(value)
      case None => producer(conn)
    }
  }

  def statusChange(conn: Session, row:(TasksRow, Int)): Future[Int] = {
    conn.database.run(
      tq.filter(
        item => {
          item.id === row._1.id
        }
      )
        .map(task => (task.executionTimestamp, task.status))
        .update((Some(new Timestamp(System.currentTimeMillis())), 20)))
  }

  val session = db.createSession()
  import scala.concurrent.duration._


  Try(session.conn) match {
    case Success(_) =>
      val flow = Source.repeat(NotUsed).map(_ => producer(session)).mapAsync(10)(executor)
        .mapAsync(10)(statusChange(session, _))
        .runWith(Sink.ignore)

      Await.result(flow, Duration.Inf)
    case Failure(e) =>
      println(e)
  }
}
