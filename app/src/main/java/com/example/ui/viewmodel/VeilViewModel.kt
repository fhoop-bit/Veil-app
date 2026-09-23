package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AuthStatus
import com.example.data.model.Contact
import com.example.data.model.Conversation
import com.example.data.model.Message
import com.example.data.model.SecurityEnclaveState
import com.example.data.model.User
import com.example.data.repository.AuthRepository
import com.example.data.repository.VeilRepository
import com.example.data.sync.FirestoreSyncManager
import com.example.security.BiometricAuthManager
import androidx.fragment.app.FragmentActivity
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class VeilTab {
  CHATS, VAULT, CALLS, SETTINGS
}

enum class FilterCategory {
  ALL, UNREAD, VAULTED, CHANNELS
}

class VeilViewModel(
  private val repository: VeilRepository,
  private val authRepository: AuthRepository,
  private val syncManager: FirestoreSyncManager? = null
) : ViewModel() {

  init {
    viewModelScope.launch {
      authRepository.checkInitialSession()
    }
  }

  // Authentication states
  val currentUser: StateFlow<User?> = authRepository.currentUser
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val authStatus: StateFlow<AuthStatus> = authRepository.authStatus

  val isFirebaseConfigured: Boolean
    get() = authRepository.isFirebaseConfigured || (syncManager?.isCloudSyncConfigured == true)

  private val _activeTab = MutableStateFlow(VeilTab.CHATS)
  val activeTab: StateFlow<VeilTab> = _activeTab.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _filterCategory = MutableStateFlow(FilterCategory.ALL)
  val filterCategory: StateFlow<FilterCategory> = _filterCategory.asStateFlow()

  private val _selectedConversationId = MutableStateFlow<Long?>(null)
  val selectedConversationId: StateFlow<Long?> = _selectedConversationId.asStateFlow()

  private var activeConversationListener: ListenerRegistration? = null

  val priorityContacts: StateFlow<List<Contact>> = repository.priorityContacts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allContacts: StateFlow<List<Contact>> = repository.contacts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val rawConversations: StateFlow<List<Conversation>> = repository.conversations
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Filtered & searched conversations
  val conversations: StateFlow<List<Conversation>> = combine(
    repository.conversations,
    _searchQuery,
    _filterCategory
  ) { list, query, filter ->
    list.filter { conv ->
      val matchesQuery = query.isBlank() ||
        conv.title.contains(query, ignoreCase = true) ||
        conv.lastMessage.contains(query, ignoreCase = true)

      val matchesFilter = when (filter) {
        FilterCategory.ALL -> true
        FilterCategory.UNREAD -> conv.unreadCount > 0
        FilterCategory.VAULTED -> conv.isVaulted
        FilterCategory.CHANNELS -> conv.isChannel
      }
      matchesQuery && matchesFilter
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val currentConversation: StateFlow<Conversation?> = _selectedConversationId.flatMapLatest { id ->
    if (id != null) repository.getConversation(id) else flowOf(null)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val currentMessages: StateFlow<List<Message>> = _selectedConversationId.flatMapLatest { id ->
    if (id != null) repository.getMessages(id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Security Enclave State
  private val _securityState = MutableStateFlow(SecurityEnclaveState())
  val securityState: StateFlow<SecurityEnclaveState> = _securityState.asStateFlow()

  private val _showEnclaveModal = MutableStateFlow(false)
  val showEnclaveModal: StateFlow<Boolean> = _showEnclaveModal.asStateFlow()

  private val _pinInput = MutableStateFlow("")
  val pinInput: StateFlow<String> = _pinInput.asStateFlow()

  private val _pinError = MutableStateFlow<String?>(null)
  val pinError: StateFlow<String?> = _pinError.asStateFlow()

  private var pendingVaultConversationId: Long? = null

  // Playing audio memo
  private val _playingVoiceMemoId = MutableStateFlow<Long?>(null)
  val playingVoiceMemoId: StateFlow<Long?> = _playingVoiceMemoId.asStateFlow()

  // View-once modal state
  private val _viewOnceMessage = MutableStateFlow<Message?>(null)
  val viewOnceMessage: StateFlow<Message?> = _viewOnceMessage.asStateFlow()

  // Auth operations
  fun signInWithEmail(email: String, pass: String, onResult: ((Boolean, String?) -> Unit)? = null) {
    viewModelScope.launch {
      val res = authRepository.signInWithEmail(email, pass)
      res.fold(
        onSuccess = { onResult?.invoke(true, null) },
        onFailure = { onResult?.invoke(false, it.message) }
      )
    }
  }

  fun signUpWithEmail(email: String, pass: String, name: String, onResult: ((Boolean, String?) -> Unit)? = null) {
    viewModelScope.launch {
      val res = authRepository.signUpWithEmail(email, pass, name)
      res.fold(
        onSuccess = { onResult?.invoke(true, null) },
        onFailure = { onResult?.invoke(false, it.message) }
      )
    }
  }

  fun signInWithGoogle(activityContext: Context, onResult: ((Boolean, String?) -> Unit)? = null) {
    viewModelScope.launch {
      val res = authRepository.signInWithGoogleCredential(activityContext)
      res.fold(
        onSuccess = { onResult?.invoke(true, null) },
        onFailure = { onResult?.invoke(false, it.message) }
      )
    }
  }

  fun signInWithHardwarePasskey(onResult: ((Boolean, String?) -> Unit)? = null) {
    viewModelScope.launch {
      val res = authRepository.signInWithHardwarePasskey()
      res.fold(
        onSuccess = { onResult?.invoke(true, null) },
        onFailure = { onResult?.invoke(false, it.message) }
      )
    }
  }

  fun signOut() {
    viewModelScope.launch {
      activeConversationListener?.remove()
      activeConversationListener = null
      authRepository.signOut()
      _selectedConversationId.value = null
      _activeTab.value = VeilTab.CHATS
    }
  }

  fun selectTab(tab: VeilTab) {
    _activeTab.value = tab
    _selectedConversationId.value = null
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setFilterCategory(category: FilterCategory) {
    _filterCategory.value = category
  }

  fun selectConversation(conversationId: Long) {
    val conv = rawConversations.value.find { it.id == conversationId }
    if (conv?.isVaulted == true && _securityState.value.isLocked) {
      pendingVaultConversationId = conversationId
      _showEnclaveModal.value = true
    } else {
      openConversationDirectly(conversationId)
    }
  }

  private fun openConversationDirectly(conversationId: Long) {
    _selectedConversationId.value = conversationId
    activeConversationListener?.remove()
    activeConversationListener = syncManager?.listenToRemoteConversationMessages(
      conversationId = conversationId,
      currentUid = currentUser.value?.uid,
      scope = viewModelScope
    )
    viewModelScope.launch {
      repository.markConversationRead(conversationId)
      repository.syncConversationWithRemote(conversationId, currentUser.value?.uid)
    }
  }

  fun backToConversations() {
    activeConversationListener?.remove()
    activeConversationListener = null
    _selectedConversationId.value = null
  }

  fun openEnclaveModal() {
    _pinInput.value = ""
    _pinError.value = null
    _showEnclaveModal.value = true
  }

  fun closeEnclaveModal() {
    _showEnclaveModal.value = false
    _pinInput.value = ""
    _pinError.value = null
    pendingVaultConversationId = null
  }

  fun enterPinDigit(digit: String) {
    if (_pinInput.value.length < 4) {
      val next = _pinInput.value + digit
      _pinInput.value = next
      if (next.length == 4) {
        verifyPin(next)
      }
    }
  }

  fun deletePinDigit() {
    if (_pinInput.value.isNotEmpty()) {
      _pinInput.value = _pinInput.value.dropLast(1)
      _pinError.value = null
    }
  }

  fun authenticateWithBiometrics() {
    // Hardware biometric pass
    _securityState.value = _securityState.value.copy(isLocked = false)
    _showEnclaveModal.value = false
    _pinInput.value = ""
    _pinError.value = null
    pendingVaultConversationId?.let { id ->
      openConversationDirectly(id)
      pendingVaultConversationId = null
    }
  }

  private fun verifyPin(pin: String) {
    if (pin == _securityState.value.pin) {
      _securityState.value = _securityState.value.copy(isLocked = false)
      _showEnclaveModal.value = false
      _pinInput.value = ""
      _pinError.value = null
      pendingVaultConversationId?.let { id ->
        openConversationDirectly(id)
        pendingVaultConversationId = null
      }
    } else {
      _pinError.value = "Optical Enclave Mismatch. Re-enter PIN."
      _pinInput.value = ""
    }
  }

  fun lockEnclave() {
    _securityState.value = _securityState.value.copy(isLocked = true)
    if (currentConversation.value?.isVaulted == true) {
      _selectedConversationId.value = null
    }
  }

  fun sendMessage(text: String) {
    val convId = _selectedConversationId.value ?: return
    if (text.isBlank()) return
    val sender = currentUser.value?.displayName ?: "You"
    val uid = currentUser.value?.uid
    viewModelScope.launch {
      repository.sendMessage(convId, text.trim(), senderName = sender, userUid = uid)
    }
  }

  fun sendVoiceMemo(duration: String = "0:24") {
    val convId = _selectedConversationId.value ?: return
    val sender = currentUser.value?.displayName ?: "You"
    val uid = currentUser.value?.uid
    viewModelScope.launch {
      repository.sendVoiceMemo(convId, duration, senderName = sender, userUid = uid)
    }
  }

  fun sendApertureMedia() {
    val convId = _selectedConversationId.value ?: return
    val sender = currentUser.value?.displayName ?: "You"
    val uid = currentUser.value?.uid
    viewModelScope.launch {
      repository.sendAttachment(
        conversationId = convId,
        name = "Veil_Aperture_Render.raw",
        size = "5.2 MB",
        isViewOnce = true,
        senderName = sender,
        userUid = uid
      )
    }
  }

  fun toggleVoicePlayback(messageId: Long) {
    if (_playingVoiceMemoId.value == messageId) {
      _playingVoiceMemoId.value = null
    } else {
      _playingVoiceMemoId.value = messageId
    }
  }

  fun addReaction(messageId: Long, emoji: String) {
    val convId = _selectedConversationId.value ?: 1L
    viewModelScope.launch {
      repository.updateReaction(convId, messageId, emoji)
    }
  }

  fun openViewOnce(message: Message) {
    if (!message.isBurned) {
      _viewOnceMessage.value = message
    }
  }

  fun closeAndBurnViewOnce(messageId: Long) {
    val convId = _selectedConversationId.value ?: 1L
    viewModelScope.launch {
      repository.burnViewOnce(convId, messageId)
      _viewOnceMessage.value = null
    }
  }

  fun rotateSessionKey() {
    val hexChars = "0123456789ABCDEF"
    val randomHex = (1..4).map { hexChars.random() }.joinToString("") +
      "..." +
      (1..4).map { hexChars.random() }.joinToString("")
    _securityState.value = _securityState.value.copy(
      sessionKeyFingerprint = "0x$randomHex",
      sessionKeyMinutesAgo = 0
    )
  }

  fun toggleTwoStepAuth() {
    _securityState.value = _securityState.value.copy(
      twoStepAuthEnabled = !_securityState.value.twoStepAuthEnabled
    )
  }

  fun toggleChatLock() {
    _securityState.value = _securityState.value.copy(
      chatLockEnabled = !_securityState.value.chatLockEnabled
    )
  }

  fun toggleHiddenPreviews() {
    _securityState.value = _securityState.value.copy(
      hiddenPreviewsEnabled = !_securityState.value.hiddenPreviewsEnabled
    )
  }

  fun setDisappearingDays(days: Int) {
    _securityState.value = _securityState.value.copy(
      disappearingDays = days
    )
  }

  fun triggerBiometricUnlock(
    activity: FragmentActivity,
    title: String = "Unlock Optical Enclave",
    subtitle: String = "Hardware-backed biometric verification",
    onDismissOrError: ((String) -> Unit)? = null
  ) {
    BiometricAuthManager.promptBiometricUnlock(
      activity = activity,
      title = title,
      subtitle = subtitle,
      description = "Hardware cryptographic verification to decrypt messages and access optical vault",
      onSuccess = {
        authenticateWithBiometrics()
      },
      onError = { _, err ->
        _pinError.value = err
        onDismissOrError?.invoke(err)
      },
      onFailed = {
        _pinError.value = "Biometric credential not recognized"
      }
    )
  }

  fun toggleBiometricAuth() {
    _securityState.value = _securityState.value.copy(
      biometricAuthEnabled = !_securityState.value.biometricAuthEnabled
    )
  }

  fun toggleLockAppOnLaunch() {
    _securityState.value = _securityState.value.copy(
      lockAppOnLaunch = !_securityState.value.lockAppOnLaunch
    )
  }

  override fun onCleared() {
    super.onCleared()
    activeConversationListener?.remove()
    activeConversationListener = null
  }
}

class VeilViewModelFactory(
  private val repository: VeilRepository,
  private val authRepository: AuthRepository,
  private val syncManager: FirestoreSyncManager? = null
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(VeilViewModel::class.java)) {
      return VeilViewModel(repository, authRepository, syncManager) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
