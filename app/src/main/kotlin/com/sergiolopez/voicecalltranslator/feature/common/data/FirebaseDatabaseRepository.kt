package com.sergiolopez.voicecalltranslator.feature.common.data

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.google.firebase.database.snapshots
import com.sergiolopez.voicecalltranslator.feature.common.data.mapper.UserMapper
import com.sergiolopez.voicecalltranslator.feature.common.data.model.UserData
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDatabaseRepository @Inject constructor(
    private val userMapper: UserMapper
) {

    private val database = FirebaseDatabase.getInstance()
    private val vtcDatabase = database.getReference(DATABASE_NAME)

    fun getUserList(): Result<Flow<List<User>>> {
        return runCatching {
            vtcDatabase.child(USERS_TABLE_NAME).snapshots.map { dataSnapshot ->
                dataSnapshot.children.mapNotNull {
                    it.getValue<UserData>()?.let { userData ->
                        userMapper.mapUserDataToUser(userData)
                    }
                }
            }
        }
    }

    suspend fun saveUser(user: User) {
        val userData = userMapper.mapUserToUserData(user)
        vtcDatabase.child(USERS_TABLE_NAME).child(user.id).setValue(userData).await()
    }

    companion object {
        const val DATABASE_NAME = "vct"
        const val USERS_TABLE_NAME = "users"
    }
}