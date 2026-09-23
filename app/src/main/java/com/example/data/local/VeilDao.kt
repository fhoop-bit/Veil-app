package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VeilDao {

  @Query("SELECT * FROM conversations ORDER BY lastTimestamp DESC")
  fun getAllConversations(): Flow<List<ConversationEntity>>

  @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
  fun getConversationById(id: Long): Flow<ConversationEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertConversation(conversation: ConversationEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertConversations(conversations: List<ConversationEntity>)

  @Update
  suspend fun updateConversation(conversation: ConversationEntity)

  @Query("UPDATE conversations SET lastMessage = :lastMessage, lastTimestamp = :timestamp, isVoiceMemo = :isVoiceMemo WHERE id = :id")
  suspend fun updateLastMessage(id: Long, lastMessage: String, timestamp: Long, isVoiceMemo: Boolean = false)

  @Query("UPDATE conversations SET unreadCount = 0 WHERE id = :id")
  suspend fun markConversationRead(id: Long)

  @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
  fun getMessagesForConversation(conversationId: Long): Flow<List<MessageEntity>>

  @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
  suspend fun getMessagesList(conversationId: Long): List<MessageEntity>

  @Query("SELECT * FROM messages WHERE id = :id LIMIT 1")
  suspend fun getMessageById(id: Long): MessageEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: MessageEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessages(messages: List<MessageEntity>)

  @Query("UPDATE messages SET reactionEmoji = :emoji WHERE id = :messageId")
  suspend fun updateMessageReaction(messageId: Long, emoji: String)

  @Query("UPDATE messages SET isBurned = 1 WHERE id = :messageId")
  suspend fun burnViewOnceMessage(messageId: Long)

  @Query("SELECT * FROM contacts ORDER BY isPriority DESC, name ASC")
  fun getAllContacts(): Flow<List<ContactEntity>>

  @Query("SELECT * FROM contacts WHERE isPriority = 1")
  fun getPriorityContacts(): Flow<List<ContactEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertContacts(contacts: List<ContactEntity>)

  @Query("SELECT COUNT(*) FROM contacts")
  suspend fun getContactCount(): Int

  @Query("SELECT * FROM users ORDER BY lastActiveTimestamp DESC LIMIT 1")
  fun getCurrentUser(): Flow<UserEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity)

  @Query("DELETE FROM users")
  suspend fun clearUsers()
}
