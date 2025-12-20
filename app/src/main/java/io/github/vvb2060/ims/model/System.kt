package io.github.vvb2060.ims.model

data class SystemInfo(
    val appVersionName: String = "",
    val androidVersion: String = "",
    val deviceModel: String = "",
    val systemVersion: String = "",
    val securityPatchVersion: String = "",
)