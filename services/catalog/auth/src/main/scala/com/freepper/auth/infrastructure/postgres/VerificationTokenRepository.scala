package com.freepper.auth.infrastructure.postgres

import com.freepper.common.infrastracture.persistance.postgres.{DoobieInstances, Queries, QueryHelpers}
import com.freepper.auth.domain.verification_token._
import com.freepper.common.domain.crud
import doobie.postgres.implicits.UuidType
import doobie.implicits.toSqlInterpolator

object VerificationTokenRepository {
  private object VerificationTokenQueries extends Queries[VerificationTokenCrud] {
    import DoobieInstances.timeInstances.UtcInstantMeta
    import DoobieInstances.commonInstances._
    import AuthReposFieldDefs._

    private val tableName = "verification_tokens"

    override def create(req: CreateVerificationToken): List[doobie.Update0] = {
      val res =
        sql"INSERT INTO verification_tokens (uid, id, expire_time) VALUES (${req.uid}, ${req.id}, ${req.expireTime})"
      List(res.update)
    }

    override def delete(id: VerificationTokenId): List[doobie.Update0] = {
      QueryHelpers.defaultDelete(tableName, id)
    }

    override def find(query: crud.Query[VerificationTokenSelector]): doobie.Query0[VerificationToken] = ???

    override def update(req: UpdateVerificationToken): List[doobie.Update0] = ???
  }
}
