package com.example.data.model

data class Contact(
  val id: Long,
  val name: String,
  val title: String,
  val isVaulted: Boolean = false,
  val isPriority: Boolean = false,
  val isOnline: Boolean = true,
  val fingerprint: String = "0x9F4E...C21B",
  val avatarColorHex: String = "#3B82F6"
)

data class Conversation(
  val id: Long,
  val contactId: Long,
  val title: String,
  val isVaulted: Boolean = false,
  val isChannel: Boolean = false,
  val unreadCount: Int = 0,
  val lastMessage: String = "",
  val lastTimestamp: Long = System.currentTimeMillis(),
  val isVoiceMemo: Boolean = false,
  val voiceDuration: String? = null
)

data class Message(
  val id: Long = 0,
  val conversationId: Long,
  val senderId: Long,
  val senderName: String,
  val isMe: Boolean,
  val text: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isVoiceMemo: Boolean = false,
  val voiceDuration: String? = null,
  val isAttachment: Boolean = false,
  val attachmentName: String? = null,
  val attachmentSize: String? = null,
  val isViewOnce: Boolean = false,
  val isBurned: Boolean = false,
  val reactionEmoji: String? = null,
  val cipherDigest: String = "CRYPTO-AES256-GCM-E2EE"
)

data class SecurityEnclaveState(
  val isLocked: Boolean = false,
  val pin: String = "4092",
  val sessionKeyMinutesAgo: Int = 14,
  val sessionKeyFingerprint: String = "0x7F2B...E914",
  val twoStepAuthEnabled: Boolean = true,
  val chatLockEnabled: Boolean = true,
  val hiddenPreviewsEnabled: Boolean = true,
  val disappearingDays: Int = 7,
  val biometricAuthEnabled: Boolean = true,
  val lockAppOnLaunch: Boolean = false
)

data class User(
  val uid: String,
  val email: String,
  val displayName: String,
  val photoUrl: String? = null,
  val enclaveFingerprint: String = "0x7F2B...E914",
  val isCloudSynced: Boolean = false,
  val authProvider: String = "Email"
)

sealed class AuthStatus {
  object Unauthenticated : AuthStatus()
  object Loading : AuthStatus()
  data class Authenticated(val user: User) : AuthStatus()
  data class Error(val message: String) : AuthStatus()
}

