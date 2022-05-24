package com.example.valentinesgaragetaskmanagementapp.models
import kotlinx.serialization.Serializable

@Serializable
class User()  {
    lateinit var name: String
    lateinit var email: String
    lateinit var department:String
    lateinit var role:String
    lateinit var image:String
    lateinit var token:String
    lateinit var id:String
}