package com.example.data.repository

import com.example.data.local.MessageEntity
import com.example.data.local.VeilDao
import com.example.data.model.Contact
import com.example.data.model.Conversation
import com.example.data.model.Message
import com.example.data.sync.FirestoreSyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VeilRepository(
  private val dao: VeilDao,
  private val syncManager: FirestoreSyncManager? = null
) {

  val conversations: Flow<List<Conversation>> = dao.getAllConversations().map { list ->
    list.map { it.toModel() }
  }

  val contacts: Flow<List<Contact>> = dao.getAllContacts().map { list ->
    list.map { it.toModel() }
  }

  val priorityContacts: Flow<List<Contact>> = dao.getPriorityContacts().map { list ->
    list.map { it.toModel() }
  }

  fun getMessages(conversationId: Long): Flow<List<Message>> {
    return dao.getMessagesForConversation(conversationId).map { list ->
      list.map { it.toModel() }
    }
  }

  fun getConversation(id: Long): Flow<Conversation?> {
    return dao.getConversationById(id).map { it?.toModel() }
  }

  suspend fun sendMessage(
    conversationId: Long,
    text: String,
    senderName: String = "You",
    userUid: String? = null
  ) {
    val now = System.currentTimeMillis()
    val msg = MessageEntity(
      conversationId = conversationId,
      senderId = 999,
      senderName = senderName,
      isMe = true,
      text = text,
      timestamp = now,
      isVoiceMemo = false,
      voiceDuration = null,
      isAttachment = false,
      attachmentName = null,
      attachmentSize = null,
      isViewOnce = false,
      isBurned = false,
      reactionEmoji = null,
      cipherDigest = "ECDH-25519-SHA256:${(1000..9999).random()}x"
    )
    val insertedId = dao.insertMessage(msg)
    dao.updateLastMessage(conversationId, text, now, false)
    syncManager?.uploadMessageToFirestore(msg.copy(id = insertedId), userUid)
  }

  suspend fun sendVoiceMemo(
    conversationId: Long,
    duration: String = "0:18",
    senderName: String = "You",
    userUid: String? = null
  ) {
    val now = System.currentTimeMillis()
    val memoText = "Encrypted voice memo ($duration)"
    val msg = MessageEntity(
      conversationId = conversationId,
      senderId = 999,
      senderName = senderName,
      isMe = true,
      text = memoText,
      timestamp = now,
      isVoiceMemo = true,
      voiceDuration = duration,
      isAttachment = false,
      attachmentName = null,
      attachmentSize = null,
      isViewOnce = false,
      isBurned = false,
      reactionEmoji = null,
      cipherDigest = "OPUS-VAULT-QUANTUM-0x${(100..999).random()}"
    )
    val insertedId = dao.insertMessage(msg)
    dao.updateLastMessage(conversationId, memoText, now, true)
    syncManager?.uploadMessageToFirestore(msg.copy(id = insertedId), userUid)
  }

  suspend fun sendAttachment(
    conversationId: Long,
    name: String,
    size: String,
    isViewOnce: Boolean,
    senderName: String = "You",
    userUid: String? = null
  ) {
    val now = System.currentTimeMillis()
    val msg = MessageEntity(
      conversationId = conversationId,
      senderId = 999,
      senderName = senderName,
      isMe = true,
      text = name,
      timestamp = now,
      isVoiceMemo = false,
      voiceDuration = null,
      isAttachment = true,
      attachmentName = name,
      attachmentSize = size,
      isViewOnce = isViewOnce,
      isBurned = false,
      reactionEmoji = null,
      cipherDigest = "VAULT-RAW-REFRACT-ENCLAVE"
    )
    val insertedId = dao.insertMessage(msg)
    dao.updateLastMessage(conversationId, name, now, false)
    syncManager?.uploadMessageToFirestore(msg.copy(id = insertedId), userUid)
  }

  suspend fun updateReaction(conversationId: Long, messageId: Long, emoji: String) {
    if (syncManager != null) {
      syncManager.updateMessageReaction(conversationId, messageId, emoji)
    } else {
      dao.updateMessageReaction(messageId, emoji)
    }
  }

  suspend fun burnViewOnce(conversationId: Long, messageId: Long) {
    if (syncManager != null) {
      syncManager.burnMessageOnRemote(conversationId, messageId)
    } else {
      dao.burnViewOnceMessage(messageId)
    }
  }

  suspend fun markConversationRead(conversationId: Long) {
    dao.markConversationRead(conversationId)
  }

  suspend fun syncConversationWithRemote(conversationId: Long, userUid: String?) {
    syncManager?.syncRoomToFirestore(conversationId, userUid)
  }
}
