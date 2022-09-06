package com.db.streaming

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.db.streaming.db.model.Tables.{Tasks, TasksRow}
import com.db.streaming.db.profile.CustomPostgresProfile
import com.db.streaming.db.profile.CustomPostgresProfile.api._
import slick.dbio.Effect
import slick.lifted.TableQuery
import slick.sql.FixedSqlAction

import scala.concurrent.duration._
import java.sql.Timestamp
import scala.annotation.tailrec
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Random, Success, Try}


object ResultCode {
  val SuccessCode = 20
  val ErrorCode = 21
}

object DbStreamingApp extends App {
  val db: CustomPostgresProfile.backend.Database = Database.forConfig("app.db")

  val tq = TableQuery[Tasks]

  implicit val system: ActorSystem = ActorSystem("reactive")
  implicit val ec: ExecutionContextExecutor = system.dispatcher


  @tailrec
  def dbProduceData(): TasksRow = {
    Try(Await.result(db.run(tq.filter(_.status === 0).take(1)
      .result.headOption
      .transactionally), 10.seconds)) match {
      case Success(value) if value.isEmpty =>
        println("No data. Thread sleep on 10 sec")
        Thread.sleep(10000)
        dbProduceData()
      case Success(Some(value)) => value
      case Failure(exception) =>
        throw exception
    }
  }

  val statusChange: Flow[(TasksRow, Int), Future[Int], NotUsed] = Flow[(TasksRow, Int)].map { input =>
    def action(status: Int): FixedSqlAction[Int, NoStream, Effect.Write] = tq
      .filter(
        item => item.id === input._1.id
      )
      .map(task => (task.executionTimestamp, task.status))
      .update((Some(new Timestamp(System.currentTimeMillis())), status))

    input._2 match {
      case result if result == 0 =>
        println(s"Item upd : ${input._1.id} with status: 21")
        db.run(action(ResultCode.ErrorCode))

      case _ =>
        println(s"Item upd : ${input._1.id} with status: 20")
        db.run(action(ResultCode.SuccessCode))
    }
  }

  val executor: Flow[TasksRow, (TasksRow, Int), NotUsed]= Flow[TasksRow].map {(_, Random.between(0, 2))}
  val stream = Source(LazyList.continually(dbProduceData())).via(executor).via(statusChange).runWith(Sink.ignore)
  Await.result(stream, Duration.Inf)
}
