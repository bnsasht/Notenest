package com.example.project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project.model.UserModel
import com.example.project.repository.UserRepo
import com.example.project.repository.UserRepoImplementation
import com.google.firebase.auth.FirebaseUser

class UserViewModel(val repo: UserRepo) : ViewModel() {

    // authentication and user actions

    fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        repo.login(email, password, callback)
    }

    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        repo.register(email, password, callback)
    }

    fun forgotPassword(email: String, callback: (Boolean, String) -> Unit) {
        repo.forgotPassword(email, callback)
    }

    fun addUserToDatabase(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.addUserToDatabase(userId, model, callback)
    }

    fun getCurrentUser(): FirebaseUser? {
        return repo.getCurrentUser()
    }


    private val _users = MutableLiveData<UserModel?>()
    val users: LiveData<UserModel?> get() = _users

    private val _allUsers = MutableLiveData<List<UserModel>>(emptyList())
    val allUsers: LiveData<List<UserModel>> get() = _allUsers

    fun getUserById(userId: String, callback: (Boolean, UserModel?) -> Unit) {
        repo.getUserById(userId) { success, user ->
            _users.postValue(user)
            callback(success, user)
        }
    }

    fun getAllUser(callback: (Boolean, List<UserModel>?) -> Unit) {
        repo.getAllUser { success, data ->
            if (success && data != null) {
                _allUsers.postValue(data)
            }
            callback(success, data)
        }
    }

    fun deleteUser(userId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteUser(userId) { success, message ->
            callback(success, message)
        }
    }

    fun updateProfile(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.updateProfile(userId, model) { success, message ->
            if (success) {
                _users.postValue(model)
            }
            callback(success, message)
        }
    }

    fun logout(callback: (Boolean, String) -> Unit) {
        repo.logout(callback)
    }

}