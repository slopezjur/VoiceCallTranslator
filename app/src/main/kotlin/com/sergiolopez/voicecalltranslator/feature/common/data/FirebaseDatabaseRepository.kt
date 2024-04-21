package com.sergiolopez.voicecalltranslator.feature.common.data

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.google.firebase.database.snapshots
import com.sergiolopez.voicecalltranslator.feature.call.data.CallData
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.common.data.mapper.FirebaseRepositoryMapper
import com.sergiolopez.voicecalltranslator.feature.common.data.model.UserData
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDatabaseRepository @Inject constructor(
    private val firebaseRepositoryMapper: FirebaseRepositoryMapper
) {

    private val database = FirebaseDatabase.getInstance()
    private val vtcDatabase = database.getReference(DATABASE_NAME)

    fun getUserList(userId: String): Result<Flow<List<User>>> {
        return runCatching {
            vtcDatabase.child(USERS_TABLE_NAME).snapshots.map { dataSnapshot ->
                dataSnapshot.children.mapNotNull {
                    it.getValue<UserData>()?.let { userData ->
                        firebaseRepositoryMapper.mapUserDataToUser(userData)
                    }
                }.filterNot { user ->
                    user.id == userId
                }
            }
        }
    }

    suspend fun saveUser(user: User) {
        runCatching {
            val userData = firebaseRepositoryMapper.mapUserToUserData(user)
            vtcDatabase.child(USERS_TABLE_NAME).child(user.id).setValue(userData).await()
        }
    }

    fun getCallList(): Result<Flow<List<Call>>> {
        return runCatching {
            vtcDatabase.child(CALLS_TABLE_NAME).snapshots.map { dataSnapshot ->
                dataSnapshot.children.mapNotNull {
                    it.getValue<CallData>()?.let { callData ->
                        firebaseRepositoryMapper.mapCallDataToCall(callData)
                    }
                }
            }
        }
    }

    suspend fun createCall(call: Call) {
        runCatching {
            val callData = firebaseRepositoryMapper.mapCallToCallData(call)
            vtcDatabase.child(CALLS_TABLE_NAME).child(call.callerId).setValue(callData).await()
        }
    }

    suspend fun removeCall(callerId: String) {
        runCatching {
            vtcDatabase.child(CALLS_TABLE_NAME).child(callerId).removeValue().await()
        }
    }

    companion object {
        const val DATABASE_NAME = "vct"
        const val USERS_TABLE_NAME = "users"
        const val CALLS_TABLE_NAME = "calls"
    }
}