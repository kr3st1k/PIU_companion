package dev.kr3st1k.piucompanion.objects

data class User(
    val username: String,
    val titleName: String,
    val backgroundUri: String,
    val avatarUri: String,
    val recentGameAccess: String,
    val coinValue: String,
    val trueUser: Boolean = false
) {
    constructor() : this("unavailable","unavailable","unavailable","unavailable","unavailable","unavailable")
}