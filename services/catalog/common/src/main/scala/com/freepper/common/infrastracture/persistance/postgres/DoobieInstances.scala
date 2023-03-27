package com.freepper.common.infrastracture.persistance.postgres

import cats.Show
import cats.data.NonEmptyList
import doobie.util.meta.Meta.Basic
import doobie.postgres.implicits.UuidType
import doobie.Meta
import io.circe.{Decoder, Json, parser}
import org.postgresql.util.PGobject

import java.sql.Timestamp
import java.time.{Instant, ZoneOffset}
import java.util.Calendar

import cats.syntax.show.toShow
import doobie.{Fragment, Fragments, Put, Get, Read}

object DoobieInstances {
  object timeInstances {
    private val cal = Calendar.getInstance(java.util.TimeZone.getTimeZone(ZoneOffset.UTC))

    implicit val UtcTimestampMeta: Meta[java.sql.Timestamp] = {
      Basic.one[java.sql.Timestamp](
        doobie.enumerated.JdbcType.Timestamp,
        Nil,
        _.getTimestamp(_),
        _.setTimestamp(_, _, cal),
        _.updateTimestamp(_, _)
      )
    }

    implicit val UtcInstantMeta: Meta[Instant] = UtcTimestampMeta.imap(_.toInstant)(x => Timestamp.from(x))

  }

  object commonInstances {
    val jsonGet: Get[Json] = {
      implicit val showPGobject: Show[PGobject] = Show.show(_.getValue.take(250))

      Get.Advanced.other[PGobject](NonEmptyList.of("json")).temap[Json] { o =>
        parser.parse(o.getValue).left.map(_.show)
      }
    }

    def readJsonFromView[T: Decoder]: Get[T] = {
      jsonGet.temap(_.as[T].left.map(_.getMessage()))
    }
  }
}
