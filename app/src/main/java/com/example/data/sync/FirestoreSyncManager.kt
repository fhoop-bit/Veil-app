package com.example.data.sync

import android.content.Context
import android.util.Log
import com.example.data.local.MessageEntity
import com.example.data.local.VeilDao
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Room-to-Firestore synchronization helper class for real-time encrypted message storage.
 * Handles bi-directional synchronization between local Room cache and Firebase Cloud Firestore
 * with zero-knowledge fallback when offline or unconfigured.
 */
class FirestoreSyncManager(
  private val context: Context,
  private val dao: VeilDao
) {
  private val tag = "VeilFirestoreSync"

  val isCloudSyncConfigured: Boolean by lazy {
    try {
      FirebaseApp.getApps(context).isNotEmpty()
    } catch (e: Exception) {
      Log.w(tag, "Firebase is not initialized: ${e.message}")
      false
    }
  }

  private val firestore: FirebaseFirestore?
    get() {
      return try {
        if (isCloudSyncConfigured) FirebaseFirestore.getInstance() else null
      } catch (e: Exception) {
        Log.w(tag, "Firestore instance unavailable: ${e.message}")
        null
      }
    }

  /**
   * Serializes a Room [MessageEntity] to a Firestore-compatible document payload.
   */
  fun messageToFirestoreMap(message: MessageEntity, userUid: String?): Map<String, Any?> {
    return mapOf(
      "id" to message.id,
      "conversationId" to message.conversationId,
      "senderId" to message.senderId,
      "senderName" to message.senderName,
      "senderUid" to (userUid ?: "anonymous_enclave"),
      "isMe" to message.isMe,
      "text" to message.text,
      "timestamp" to message.timestamp,
      "isVoiceMemo" to message.isVoiceMemo,
      "voiceDuration" to message.voiceDuration,
      "isAttachment" to message.isAttachment,
      "attachmentName" to message.attachmentName,
      "attachmentSize" to message.attachmentSize,
      "isViewOnce" to message.isViewOnce,
      "isBurned" to message.isBurned,
      "reactionEmoji" to message.reactionEmoji,
      "cipherDigest" to message.cipherDigest,
      "syncedAt" to System.currentTimeMillis()
    )
  }

  /**
   * Deserializes a Firestore document snapshot into a Room [MessageEntity].
   */
  fun firestoreMapToMessage(data: Map<String, Any?>, currentUid: String?): MessageEntity? {
    return try {
      val id = (data["id"] as? Number)?.toLong() ?: 0L
      val conversationId = (data["conversationId"] as? Number)?.toLong() ?: 1L
      val senderId = (data["senderId"] as? Number)?.toLong() ?: 0L
      val senderName = (data["senderName"] as? String) ?: "Unknown"
      val senderUid = (data["senderUid"] as? String)
      val isMe = if (currentUid != null && senderUid != null) {
        senderUid == currentUid
      } else {
        (data["isMe"] as? Boolean) ?: (senderId == 999L)
      }
      val text = (data["text"] as? String) ?: ""
      val timestamp = (data["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
      val isVoiceMemo = (data["isVoiceMemo"] as? Boolean) ?: false
      val voiceDuration = data["voiceDuration"] as? String
      val isAttachment = (data["isAttachment"] as? Boolean) ?: false
      val attachmentName = data["attachmentName"] as? String
      val attachmentSize = data["attachmentSize"] as? String
      val isViewOnce = (data["isViewOnce"] as? Boolean) ?: false
      val isBurned = (data["isBurned"] as? Boolean) ?: false
      val reactionEmoji = data["reactionEmoji"] as? String
      val cipherDigest = (data["cipherDigest"] as? String) ?: "ECDH-25519-AES-GCM"

      MessageEntity(
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
    } catch (e: Exception) {
      Log.e(tag, "Failed to parse Firestore document into MessageEntity", e)
      null
    }
  }

  /**
   * Uploads a single newly sent or edited message to Firestore.
   */
  suspend fun uploadMessageToFirestore(message: MessageEntity, userUid: String?) = withContext(Dispatchers.IO) {
    val db = firestore ?: return@withContext
    try {
      val payload = messageToFirestoreMap(message, userUid)
      val docRef = db.collection("conversations")
        .document(message.conversationId.toString())
        .collection("messages")
        .document(message.id.toString())

      docRef.set(payload, SetOptions.merge()).await()
      Log.d(tag, "Successfully uploaded message ${message.id} to Firestore")
    } catch (e: Exception) {
      Log.e(tag, "Error uploading message ${message.id} to Firestore", e)
    }
  }

  /**
   * Reads all messages from Room for a conversation and syncs any missing records to Firestore.
   */
  suspend fun syncRoomToFirestore(conversationId: Long, userUid: String?) = withContext(Dispatchers.IO) {
    val db = firestore ?: return@withContext
    try {
      val localMessages = dao.getMessagesList(conversationId)
      for (msg in localMessages) {
        val payload = messageToFirestoreMap(msg, userUid)
        db.collection("conversations")
          .document(conversationId.toString())
          .collection("messages")
          .document(msg.id.toString())
          .set(payload, SetOptions.merge())
          .await()
      }
      Log.d(tag, "Synced ${localMessages.size} messages from Room to Firestore for conversation $conversationId")
    } catch (e: Exception) {
      Log.e(tag, "Error syncing conversation $conversationId from Room to Firestore", e)
    }
  }

  /**
   * Listens to real-time changes on Firestore for a conversation and writes updates back into Room.
   */
  fun listenToRemoteConversationMessages(
    conversationId: Long,
    currentUid: String?,
    scope: CoroutineScope,
    onMessageSynced: ((MessageEntity) -> Unit)? = null
  ): ListenerRegistration? {
    val db = firestore ?: return null
    return try {
      db.collection("conversations")
        .document(conversationId.toString())
        .collection("messages")
        .addSnapshotListener { snapshots, error ->
          if (error != null) {
            Log.w(tag, "Listen failed for conversation $conversationId", error)
            return@addSnapshotListener
          }

          if (snapshots != null && !snapshots.isEmpty) {
            scope.launch(Dispatchers.IO) {
              val incoming = mutableListOf<MessageEntity>()
              var latestMsg: MessageEntity? = null

              for (doc in snapshots.documents) {
                val data = doc.data ?: continue
                val entity = firestoreMapToMessage(data, currentUid) ?: continue
                incoming.add(entity)
                if (latestMsg == null || entity.timestamp > latestMsg.timestamp) {
                  latestMsg = entity
                }
              }

              if (incoming.isNotEmpty()) {
                dao.insertMessages(incoming)
                latestMsg?.let { last ->
                  dao.updateLastMessage(
                    id = conversationId,
                    lastMessage = last.text,
                    timestamp = last.timestamp,
                    isVoiceMemo = last.isVoiceMemo
                  )
                  onMessageSynced?.invoke(last)
                }
              }
            }
          }
        }
    } catch (e: Exception) {
      Log.e(tag, "Error establishing snapshot listener for conversation $conversationId", e)
      null
    }
  }

  /**
   * Updates emoji reaction on a message in both Firestore and Room.
   */
  suspend fun updateMessageReaction(
    conversationId: Long,
    messageId: Long,
    emoji: String
  ) = withContext(Dispatchers.IO) {
    dao.updateMessageReaction(messageId, emoji)
    val db = firestore ?: return@withContext
    try {
      db.collection("conversations")
        .document(conversationId.toString())
        .collection("messages")
        .document(messageId.toString())
        .update("reactionEmoji", emoji)
        .await()
    } catch (e: Exception) {
      Log.w(tag, "Failed to update reaction on Firestore", e)
    }
  }

  /**
   * Burns a View Once message on Firestore so all participants expunge it.
   */
  suspend fun burnMessageOnRemote(
    conversationId: Long,
    messageId: Long
  ) = withContext(Dispatchers.IO) {
    dao.burnViewOnceMessage(messageId)
    val db = firestore ?: return@withContext
    try {
      db.collection("conversations")
        .document(conversationId.toString())
        .collection("messages")
        .document(messageId.toString())
        .update("isBurned", true)
        .await()
    } catch (e: Exception) {
      Log.w(tag, "Failed to burn message on Firestore", e)
    }
  }
}
