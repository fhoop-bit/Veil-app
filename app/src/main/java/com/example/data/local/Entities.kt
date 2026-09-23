package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Contact
import com.example.data.model.Conversation
import com.example.data.model.Message
import com.example.data.model.User

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey val uid: String,
  val email: String,
  val displayName: String,
  val photoUrl: String?,
  val enclaveFingerprint: String,
  val isCloudSynced: Boolean,
  val authProvider: String,
  val lastActiveTimestamp: Long = System.currentTimeMillis()
) {
  fun toModel(): User = User(
    uid = uid,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl,
    enclaveFingerprint = enclaveFingerprint,
    isCloudSynced = isCloudSynced,
    authProvider = authProvider
  )
}

@Entity(tableName = "contacts")
data class ContactEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val title: String,
  val isVaulted: Boolean,
  val isPriority: Boolean,
  val isOnline: Boolean,
  val fingerprint: String,
  val avatarColorHex: String
) {
  fun toModel(): Contact = Contact(
    id = id,
    name = name,
    title = title,
    isVaulted = isVaulted,
    isPriority = isPriority,
    isOnline = isOnline,
    fingerprint = fingerprint,
    avatarColorHex = avatarColorHex
  )
}

@Entity(tableName = "conversations")
data class ConversationEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val contactId: Long,
  val title: String,
  val isVaulted: Boolean,
  val isChannel: Boolean,
  val unreadCount: Int,
  val lastMessage: String,
  val lastTimestamp: Long,
  val isVoiceMemo: Boolean,
  val voiceDuration: String?
) {
  fun toModel(): Conversation = Conversation(
    id = id,
    contactId = contactId,
    title = title,
    isVaulted = isVaulted,
    isChannel = isChannel,
    unreadCount = unreadCount,
    lastMessage = lastMessage,
    lastTimestamp = lastTimestamp,
    isVoiceMemo = isVoiceMemo,
    voiceDuration = voiceDuration
  )
}

@Entity(tableName = "messages")
data class MessageEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val conversationId: Long,
  val senderId: Long,
  val senderName: String,
  val isMe: Boolean,
  val text: String,
  val timestamp: Long,
  val isVoiceMemo: Boolean,
  val voiceDuration: String?,
  val isAttachment: Boolean,
  val attachmentName: String?,
  val attachmentSize: String?,
  val isViewOnce: Boolean,
  val isBurned: Boolean,
  val reactionEmoji: String?,
  val cipherDigest: String
) {
  fun toModel(): Message = Message(
    id = id,
    conversationId = conversationId,
    senderId = senderId,
    senderName = senderName,
    isMe = isMe,
    text = text,
    timestamp = timestamp,
    isVoiceMemo = isVoiceMemo,
    voiceDuration = voiceDuration,
    isAttachment = isAttachment,
    attachmentName = attachmentName,
    attachmentSize = attachmentSize,
    isViewOnce = isViewOnce,
    isBurned = isBurned,
    reactionEmoji = reactionEmoji,
    cipherDigest = cipherDigest
  )
}
