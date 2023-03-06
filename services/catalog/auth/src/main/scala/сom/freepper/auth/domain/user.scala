package сom.freepper.auth.domain

import com.freepper.common.domain.crud.Crud
import io.estatico.newtype.macros.newtype

import java.time.Instant
import java.util.UUID

object user {
//  option(google.api.resource) = {
//    type: "api.freepper.com/User"
//    pattern: "users/{user}"
//  };
//  string name = 1;
//  string uid = 2;
//  string user_id = 3;
//  string display_name = 4;
//  string email = 5;
//  google.protobuf.Timestamp email_verification_time = 6;
//  string image = 7;

  @newtype
  case class UserUid(value: UUID)
  @newtype
  case class UserId(value: String)
  @newtype
  case class UserDisplayName(value: String)
  @newtype
  case class UserEmail(value: String)
  @newtype
  case class UserEmailVerificationTime(value: Instant)
  @newtype
  case class UserImage(value: String)
  case class User(
      uid: UserUid,
      id: UserId,
      displayName: UserDisplayName,
      email: UserEmail,
      emailVerificationTime: UserEmailVerificationTime,
      image: UserImage
  )

  case class CreateUserCommand(
      id: UserId,
      displayName: UserDisplayName,
      email: UserEmail,
      emailVerificationTime: UserEmailVerificationTime,
      image: UserImage
  )

  case class UpdateUserCommand(
      id: UserId,
      displayName: Option[UserDisplayName],
      email: Option[UserEmail],
      emailVerificationTime: Option[UserEmailVerificationTime],
      image: Option[UserImage]
  )
  
  sealed trait UserSelector
  object UserSelector {
    final case class UidIs(uid: UserUid) extends UserSelector
    final case object All extends UserSelector
  }

  object UserCrud extends Crud[User] {
    override type Create = CreateUserCommand
    override type Update = UpdateUserCommand
    override type Entity = User
    override type EntityId = UserId
    override type Selector = UserSelector
  }
}
