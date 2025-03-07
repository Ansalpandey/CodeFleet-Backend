package com.app.cloudide.repository

import com.app.cloudide.model.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, ObjectId> {
  fun findByUsername(username: String): User?
  fun findByEmail(email: String): User?
}