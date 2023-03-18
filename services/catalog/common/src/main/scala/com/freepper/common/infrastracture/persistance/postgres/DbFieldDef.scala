package com.freepper.common.infrastracture.persistance.postgres

import doobie.Read
import doobie.Write

trait DbFieldDef[T] {
  def name: String
  def read: Read[T]
  def write: Write[T]
  def isId: Boolean
}
