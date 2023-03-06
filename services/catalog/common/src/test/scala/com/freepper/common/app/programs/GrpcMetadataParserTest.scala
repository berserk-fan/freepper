package com.freepper.common.app.programs

import cats.effect.IO
import io.grpc.Metadata
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import com.freepper.common.TestIORuntime
import com.freepper.common.domain.auth._

class GrpcMetadataParserTest extends AnyFunSuite with Matchers with TestIORuntime {
  private val jweSecret = "k+Twz5BFK8s/Y728OHqPQzgUzadi+tdRVsjzY6wLDwk="
  private val token = "eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2R0NNIn0..L-Fw_o" +
    "6dqwxwOBpb.ZorGu8X2VvAZ0Wm3a_-DO2tq0Bp-1g1bDItTV3aV_h68iFu65gPoj8y" +
    "_Ft3vuTvFsLRI6rpGqBVdUTgd9hkyLI43NUMgPORMTmE9iKSOu3RQmzMYz8yHaixaiil" +
    "if6snXQFGJDYGKfnZtcUOJTqXpVmva67zG6rNA91lOQJpoTaDMwQGIO5mlznfVMpaPN_" +
    "6NP9ZpmkC3aKkMq2ufc4IuKJe_zaUVEWUViziKroltCBqbEjc_AlF-V7I_HuPG1pwCz" +
    "QwuF-K_KsK2mAji-6u8-1q.WDQrd7rkhd34FMBqo1s_sg"

  test("user token parsing") {
    val metadata = new Metadata()
    metadata.put(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER), token)
    val res = new GrpcMetadataParser[IO](AuthConfig(List(UserEmail("fakedoe888@gmail.com")), jweSecret, ""))
      .extractCallContext(metadata)
      .unsafeRunSync()
    res.user should ===(Some(User(UserEmail("fakedoe888@gmail.com"), UserRole.Admin)))

    val res2 = new GrpcMetadataParser[IO](AuthConfig(List(), jweSecret, ""))
      .extractCallContext(metadata)
      .unsafeRunSync()
    res2.user should ===(Some(User(UserEmail("fakedoe888@gmail.com"), UserRole.User)))
  }

  test("call context serialization") {
    val mdp = new GrpcMetadataParser[IO](AuthConfig(List(), jweSecret, ""))
    val user = User(UserEmail("some_user_email@qq.com"), UserRole.User)
    val md =
      mdp.extractMetadata(CallContext(Some(user))).unsafeRunSync()
    val cc = mdp.extractCallContext(md).unsafeRunSync()
    cc.user should ===(Some(user))

    val md2 = mdp.extractMetadata(CallContext(None)).unsafeRunSync()
    val cc2 = mdp.extractCallContext(md2).unsafeRunSync()
    cc2.user should ===(None)
  }
}
