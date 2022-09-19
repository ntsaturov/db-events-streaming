package com.db.streaming.db.model

import com.db.streaming.db.profile.CustomPostgresProfile
import slick.jdbc.GetResult

object Tables extends Tables {
  val profile = CustomPostgresProfile
}

trait Tables {
  val profile: CustomPostgresProfile
  import CustomPostgresProfile.api._
  val scTasksTable = "tasks"

  case class TasksRow(id: String,
                      executionTimestamp: Option[java.sql.Timestamp],
                      status: Int,
                      creationTimestamp: java.sql.Timestamp,
                      action: String,
                      comment: String)

  implicit val getTasksRowResult = GetResult(r => TasksRow(r.nextString(), r.nextTimestampOption(), r.nextInt(), r.nextTimestamp(), r.nextString(), r.nextString())
  )

  class Tasks(_tableTag: Tag) extends profile.api.Table[TasksRow](_tableTag, scTasksTable) {
    def * = (id, executionTimestamp, status, creationTimestamp, action, data) <> (TasksRow .tupled, TasksRow.unapply)
    def ? = (id, executionTimestamp, status, creationTimestamp, action, data).shaped.<>({ r=>import r._; _1.map(_=> TasksRow.tupled((_1, _2, _3, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    val id: Rep[String] = column[String]("id")
    val executionTimestamp: Rep[Option[java.sql.Timestamp]] = column[java.sql.Timestamp]("execution_timestamp")
    val creationTimestamp: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("creation_timestamp")
    val status: Rep[Int] = column[Int]("status")
    val action: Rep[String] = column[String]("action")
    val data: Rep[String] = column[String]("data")
  }

  lazy val Tasks = new TableQuery(tag => new Tasks(tag))
}
