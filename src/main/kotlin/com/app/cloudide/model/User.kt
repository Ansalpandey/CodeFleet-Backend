package com.app.cloudide.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.annotation.Nonnull
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
@TypeAlias("user")
data class User(
  @Id
  val id: ObjectId? = null,
  @Nonnull
  val username: String,
  @Nonnull
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  var password: String,
  @Nonnull
  val firstName: String,
  @Nonnull
  val lastName: String,
  @Nonnull
  val email: String
)
