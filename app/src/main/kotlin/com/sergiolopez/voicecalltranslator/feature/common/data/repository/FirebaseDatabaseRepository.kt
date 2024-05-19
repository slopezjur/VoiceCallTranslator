package com.sergiolopez.voicecalltranslator.feature.common.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.google.firebase.database.snapshots
import com.sergiolopez.voicecalltranslator.feature.call.data.CallDatabase
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge.DataModel
import com.sergiolopez.voicecalltranslator.feature.common.data.mapper.FirebaseRepositoryMapper
import com.sergiolopez.voicecalltranslator.feature.common.data.mapper.ResultOperation.performSimpleApiOperation
import com.sergiolopez.voicecalltranslator.feature.common.data.model.UserDatabase
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import javax.inject.Inject

class FirebaseDatabaseRepository @Inject constructor(
    private val firebaseRepositoryMapper: FirebaseRepositoryMapper
) {

    private val database = FirebaseDatabase.getInstance()
    private val vctDatabase = database.getReference(DATABASE_NAME)

    fun getUserList(userId: String): Result<Flow<List<User>>> {
        return runCatching {
            vctDatabase.child(USERS_TABLE_NAME).snapshots.map { dataSnapshot ->
                dataSnapshot.children.mapNotNull {
                    it.getValue<UserDatabase>()?.let { user ->
                        firebaseRepositoryMapper.mapUserDatabaseToUserData(user)
                    }
                }.filterNot { user ->
                    user.id == userId
                }
            }
        }
    }

    suspend fun saveUser(user: User): Boolean {
        val userData = firebaseRepositoryMapper.mapUserDataToUserDatabase(user)
        return performSimpleApiOperation {
            vctDatabase.child(USERS_TABLE_NAME).child(user.id).setValue(userData).await()
        }
    }

    suspend fun removeUser(userId: String): Boolean {
        return performSimpleApiOperation {
            vctDatabase.child(USERS_TABLE_NAME).child(userId).removeValue().await()
        }
    }

    fun getIncomingCalls(calleeId: String): Result<Flow<List<Call.CallData>>> {
        return runCatching {
            vctDatabase.child(CALLS_TABLE_NAME).orderByChild(FIELD_CALLEE_ID).equalTo(calleeId)
                .snapshots.mapNotNull { dataSnapshot ->
                    dataSnapshot.children.mapNotNull {
                        it.getValue<CallDatabase>()?.let { call ->
                            firebaseRepositoryMapper.mapCallDatabaseToCall(call)
                        }
                    }.sortedByDescending {
                        it.timestamp
                    }
                }
        }
    }

    suspend fun sendConnectionUpdate(call: DataModel) {
        runCatching {
            //al callData = firebaseRepositoryMapper.mapCallToCallDatabase(call)
            vctDatabase.child(CALLS_TABLE_NAME).child(call.target).child(LATEST_EVENT).setValue(
                Json.encodeToString(DataModel.serializer(), call)
            ).await()
        }
    }

    fun getConnectionUpdate(userId: String): Result<Flow<DataModel>> {
        return runCatching {
            vctDatabase.child(CALLS_TABLE_NAME).child(userId).child(LATEST_EVENT)
                .snapshots.mapNotNull { dataSnapshot ->
                    dataSnapshot.getValue<String>()?.let { jsonString ->
                        Json.decodeFromString(DataModel.serializer(), jsonString)
                    }
                }

            /*vctDatabase.child(CALLS_TABLE_NAME).child(userId).child(LATEST_EVENT)
                .snapshots.mapNotNull { dataSnapshot ->
                    dataSnapshot.children.firstNotNullOf {
                        it.getValue<String>()?.let { value ->
                            Json.decodeFromString(DataModel.serializer(), value)
                        }
                    }
                }*/
        }
    }

    suspend fun answerCall(call: Call.CallData) {
        runCatching {
            val callData = firebaseRepositoryMapper.mapCallToCallDatabase(call)
            vctDatabase.child(CALLS_TABLE_NAME).child(call.calleeId).setValue(callData).await()
        }
    }

    suspend fun clearCall(target: String) {
        runCatching {
            vctDatabase.child(CALLS_TABLE_NAME).child(target).removeValue().await()
        }
    }

    companion object {
        const val DATABASE_NAME = "vct"
        const val USERS_TABLE_NAME = "users"
        const val CALLS_TABLE_NAME = "calls"
        const val FIELD_CALLEE_ID = "calleeId"

        const val LATEST_EVENT = "latest_event"
    }
}