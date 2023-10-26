package com.freepper.auth.app

import com.freepper.auth.api.{Account, AuthServiceFs2Grpc, CreateAccountRequest, CreateSessionRequest, CreateUserRequest, CreateVerificationTokenRequest, DeleteAccountRequest, DeleteSessionRequest, DeleteUserRequest, DeleteVerificationTokenRequest, GetAccountRequest, GetSessionRequest, GetUserRequest, GetVerificationTokenRequest, ListUsersRequest, ListUsersResponse, Session, UpdateAccountRequest, UpdateSessionRequest, UpdateUserRequest, User, VerificationToken}
import com.freepper.common.domain.auth.CallContext
import com.google.protobuf.empty.Empty

class AuthApi[F[_]]() extends AuthServiceFs2Grpc[F, CallContext] {
  override def createUser(request: CreateUserRequest, ctx: CallContext): F[User] = ???

  override def getUser(request: GetUserRequest, ctx: CallContext): F[User] = ???

  override def updateUser(request: UpdateUserRequest, ctx: CallContext): F[User] = ???
  
  override def listUsers(request: ListUsersRequest, ctx: CallContext): F[ListUsersResponse] = ???

  override def deleteUser(request: DeleteUserRequest, ctx: CallContext): F[Empty] = ???

  override def createAccount(request: CreateAccountRequest, ctx: CallContext): F[Account] = ???

  override def getAccount(request: GetAccountRequest, ctx: CallContext): F[Account] = ???

  override def updateAccount(request: UpdateAccountRequest, ctx: CallContext): F[Account] = ???

  override def deleteAccount(request: DeleteAccountRequest, ctx: CallContext): F[Empty] = ???

  override def createSession(request: CreateSessionRequest, ctx: CallContext): F[Session] = ???

  override def getSession(request: GetSessionRequest, ctx: CallContext): F[Session] = ???

  override def updateSession(request: UpdateSessionRequest, ctx: CallContext): F[Session] = ???

  override def deleteSession(request: DeleteSessionRequest, ctx: CallContext): F[Empty] = ???

  override def createVerificationToken(request: CreateVerificationTokenRequest, ctx: CallContext): F[VerificationToken] = ???

  override def getVerificationToken(request: GetVerificationTokenRequest, ctx: CallContext): F[VerificationToken] = ???

  override def deleteVerificationToken(request: DeleteVerificationTokenRequest, ctx: CallContext): F[Empty] = ???
}
