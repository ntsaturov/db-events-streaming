package com.db.streaming.db.profile

import com.github.tminglei.slickpg.{ExPostgresProfile, PgCirceJsonSupport}

trait CustomPostgresProfile extends ExPostgresProfile with PgCirceJsonSupport {
  def pgjson = "jsonb"

  override val api = MyAPI

  override protected def computeCapabilities: Set[slick.basic.Capability] =
    super.computeCapabilities + slick.jdbc.JdbcCapabilities.insertOrUpdate

  object MyAPI extends API with JsonImplicits
}

object CustomPostgresProfile extends CustomPostgresProfile
