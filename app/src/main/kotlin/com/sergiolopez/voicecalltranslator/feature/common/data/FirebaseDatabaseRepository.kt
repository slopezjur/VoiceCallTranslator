package com.sergiolopez.voicecalltranslator.feature.common.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.snapshots
import com.google.firebase.database.values
import com.sergiolopez.voicecalltranslator.feature.common.data.mapper.UserMapper
import com.sergiolopez.voicecalltranslator.feature.common.domain.model.Result
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.feature.common.data.model.UserData
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDatabaseRepository @Inject constructor(
    private val userMapper: UserMapper
) {

    private val database = Firebase.database
    private val vtcDatabase = database.getReference(DATABASE_NAME)

    suspend fun getUserList(email: String, password: String): Result<List<User>> {
        val test = vtcDatabase.child(USERS_TABLE_NAME).values<UserData>().collect {
            Log.d("UserData: ", it.toString())
        }
        /*return vtcDatabase.child(USERS_TABLE_NAME).get().addOnSuccessListener {
            Result.Success(it.value as List<UserData>)
        }.addOnFailureListener {
            Result.Error(it)
        }*/



        val userList = mutableListOf<User>()

        vtcDatabase.snapshots.collect { dataSnapshot ->
            dataSnapshot.children.forEach {
                userList.add(userMapper.mapUserDataToUser(it.value as UserData))
            }
        }

        return Result.Success(userList)
    }

    suspend fun saveUser(user: User) {
        val userData = userMapper.mapUserToUserData(user)
        vtcDatabase.child(USERS_TABLE_NAME).child(userData.id).setValue(userData).addOnCompleteListener {
            Result.Success(userData)
        }.addOnFailureListener {
            Result.Error(it)
        }.await()
    }

    companion object {
        const val DATABASE_NAME = "vct"
        const val USERS_TABLE_NAME = "users"
    }
}