package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [UserEntity::class, ContactEntity::class, ConversationEntity::class, MessageEntity::class],
  version = 2,
  exportSchema = false
)
abstract class VeilDatabase : RoomDatabase() {

  abstract fun veilDao(): VeilDao

  companion object {
    @Volatile
    private var INSTANCE: VeilDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): VeilDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          VeilDatabase::class.java,
          "veil_secure_database"
        )
          .fallbackToDestructiveMigration()
          .addCallback(VeilDatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class VeilDatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.veilDao())
          }
        }
      }

      suspend fun populateInitialData(dao: VeilDao) {
        val contacts = listOf(
          ContactEntity(
            id = 1,
            name = "Elena Vance",
            title = "Master Vault Active • Quantum Key Verified",
            isVaulted = true,
            isPriority = true,
            isOnline = true,
            fingerprint = "0x89A3...4F01",
            avatarColorHex = "#6366F1"
          ),
          ContactEntity(
            id = 2,
            name = "Julian Thorne",
            title = "Architectural Lead • Studio Prism",
            isVaulted = false,
            isPriority = true,
            isOnline = true,
            fingerprint = "0x4B12...89D2",
            avatarColorHex = "#0EA5E9"
          ),
          ContactEntity(
            id = 3,
            name = "Marcus Sterling",
            title = "Cryptographic Engineer • ZeroKnowledge Inc",
            isVaulted = true,
            isPriority = true,
            isOnline = false,
            fingerprint = "0x9F4E...C21B",
            avatarColorHex = "#10B981"
          ),
          ContactEntity(
            id = 4,
            name = "Sophia Lin",
            title = "Optics Researcher • Crystalline Core",
            isVaulted = false,
            isPriority = true,
            isOnline = true,
            fingerprint = "0x1A2B...99FE",
            avatarColorHex = "#EC4899"
          ),
          ContactEntity(
            id = 5,
            name = "Orion Relay",
            title = "Official Network Node",
            isVaulted = true,
            isPriority = false,
            isOnline = true,
            fingerprint = "0x00FF...A1B2",
            avatarColorHex = "#8B5CF6"
          )
        )
        dao.insertContacts(contacts)

        val now = System.currentTimeMillis()
        val conversations = listOf(
          ConversationEntity(
            id = 1,
            contactId = 1,
            title = "Elena Vance",
            isVaulted = true,
            isChannel = false,
            unreadCount = 2,
            lastMessage = "Encrypted voice memo (0:42)",
            lastTimestamp = now - 120_000,
            isVoiceMemo = true,
            voiceDuration = "0:42"
          ),
          ConversationEntity(
            id = 2,
            contactId = 2,
            title = "Julian Thorne",
            isVaulted = false,
            isChannel = false,
            unreadCount = 0,
            lastMessage = "The architectural renders arrived. Looking through the lens...",
            lastTimestamp = now - 86_400_000,
            isVoiceMemo = false,
            voiceDuration = null
          ),
          ConversationEntity(
            id = 3,
            contactId = 3,
            title = "Marcus Sterling",
            isVaulted = true,
            isChannel = false,
            unreadCount = 0,
            lastMessage = "Encrypted transfer confirmed. Cipher key: 0x9F...A2",
            lastTimestamp = now - 172_800_000,
            isVoiceMemo = false,
            voiceDuration = null
          ),
          ConversationEntity(
            id = 4,
            contactId = 4,
            title = "Sophia Lin",
            isVaulted = false,
            isChannel = false,
            unreadCount = 0,
            lastMessage = "Refractive aperture adjusted to 48.2°. Clean signal.",
            lastTimestamp = now - 250_000_000,
            isVoiceMemo = false,
            voiceDuration = null
          ),
          ConversationEntity(
            id = 5,
            contactId = 5,
            title = "Orion Cipher Channel",
            isVaulted = true,
            isChannel = true,
            unreadCount = 0,
            lastMessage = "Zero-Knowledge relay update v4.2 deployed across all nodes",
            lastTimestamp = now - 340_000_000,
            isVoiceMemo = false,
            voiceDuration = null
          )
        )
        dao.insertConversations(conversations)

        val messagesElena = listOf(
          MessageEntity(
            id = 1,
            conversationId = 1,
            senderId = 1,
            senderName = "Elena Vance",
            isMe = false,
            text = "Have you reviewed the optical lens blueprints? Everything is ready for synthesis.",
            timestamp = now - 360_000,
            isVoiceMemo = false,
            voiceDuration = null,
            isAttachment = false,
            attachmentName = null,
            attachmentSize = null,
            isViewOnce = false,
            isBurned = false,
            reactionEmoji = null,
            cipherDigest = "ECDH-25519-SHA256:7a9f...e102"
          ),
          MessageEntity(
            id = 2,
            conversationId = 1,
            senderId = 999,
            senderName = "You",
            isMe = true,
            text = "Yes, the light refraction levels are immaculate. The privacy aperture responds instantly.",
            timestamp = now - 240_000,
            isVoiceMemo = false,
            voiceDuration = null,
            isAttachment = false,
            attachmentName = null,
            attachmentSize = null,
            isViewOnce = false,
            isBurned = false,
            reactionEmoji = null,
            cipherDigest = "ECDH-25519-SHA256:3c8d...a491"
          ),
          MessageEntity(
            id = 3,
            conversationId = 1,
            senderId = 1,
            senderName = "Elena Vance",
            isMe = false,
            text = "Veil_Lens_Aperture.raw",
            timestamp = now - 180_000,
            isVoiceMemo = false,
            voiceDuration = null,
            isAttachment = true,
            attachmentName = "Veil_Lens_Aperture.raw",
            attachmentSize = "4.8 MB",
            isViewOnce = true,
            isBurned = false,
            reactionEmoji = "✨",
            cipherDigest = "VAULT-RAW-REFRACT-ENCLAVE"
          ),
          MessageEntity(
            id = 4,
            conversationId = 1,
            senderId = 999,
            senderName = "You",
            isMe = true,
            text = "Confirming protocol clearance. I will initiate the refractive transfer cycle now.",
            timestamp = now - 120_000,
            isVoiceMemo = false,
            voiceDuration = null,
            isAttachment = false,
            attachmentName = null,
            attachmentSize = null,
            isViewOnce = false,
            isBurned = false,
            reactionEmoji = null,
            cipherDigest = "ECDH-25519-SHA256:bb12...990c"
          ),
          MessageEntity(
            id = 5,
            conversationId = 1,
            senderId = 1,
            senderName = "Elena Vance",
            isMe = false,
            text = "Encrypted voice memo (0:42)",
            timestamp = now - 60_000,
            isVoiceMemo = true,
            voiceDuration = "0:42",
            isAttachment = false,
            attachmentName = null,
            attachmentSize = null,
            isViewOnce = false,
            isBurned = false,
            reactionEmoji = "🔒",
            cipherDigest = "OPUS-VAULT-QUANTUM-0x44"
          )
        )
        dao.insertMessages(messagesElena)

        val messagesJulian = listOf(
          MessageEntity(
            id = 6,
            conversationId = 2,
            senderId = 2,
            senderName = "Julian Thorne",
            isMe = false,
            text = "The architectural renders arrived. Looking through the lens...",
            timestamp = now - 86_400_000,
            isVoiceMemo = false,
            voiceDuration = null,
            isAttachment = false,
            attachmentName = null,
            attachmentSize = null,
            isViewOnce = false,
            isBurned = false,
            reactionEmoji = null,
            cipherDigest = "AES-GCM-256:91da...ef00"
          )
        )
        dao.insertMessages(messagesJulian)

        val messagesMarcus = listOf(
          MessageEntity(
            id = 7,
            conversationId = 3,
            senderId = 3,
            senderName = "Marcus Sterling",
            isMe = false,
            text = "Encrypted transfer confirmed. Cipher key: 0x9F...A2",
            timestamp = now - 172_800_000,
            isVoiceMemo = false,
            voiceDuration = null,
            isAttachment = false,
            attachmentName = null,
            attachmentSize = null,
            isViewOnce = false,
            isBurned = false,
            reactionEmoji = null,
            cipherDigest = "ZK-RELAY-0x9FA2"
          )
        )
        dao.insertMessages(messagesMarcus)
      }
    }
  }
}
