package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.VeilDatabase
import com.example.data.repository.AuthRepository
import com.example.data.repository.VeilRepository
import com.example.data.sync.FirestoreSyncManager
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BiometricLockGateScreen
import com.example.ui.screens.CallsScreen
import com.example.ui.screens.ChatsScreen
import com.example.ui.screens.ConversationScreen
import com.example.ui.screens.SecureEnclaveModal
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.screens.ViewOnceModal
import com.example.ui.theme.VeilGlassBorder
import com.example.ui.theme.VeilOnPrimary
import com.example.ui.theme.VeilPrimary
import com.example.ui.theme.VeilPrimaryContainer
import com.example.ui.theme.VeilSecondary
import com.example.ui.theme.VeilTheme
import com.example.ui.viewmodel.VeilTab
import com.example.ui.viewmodel.VeilViewModel
import com.example.ui.viewmodel.VeilViewModelFactory

class MainActivity : FragmentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      VeilTheme {
        VeilApp()
      }
    }
  }
}

@Composable
fun VeilApp() {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val database = remember { VeilDatabase.getDatabase(context, scope) }
  val authRepository = remember { AuthRepository(context, database.veilDao()) }
  val syncManager = remember { FirestoreSyncManager(context, database.veilDao()) }
  val repository = remember { VeilRepository(database.veilDao(), syncManager) }
  val viewModel: VeilViewModel = viewModel(factory = VeilViewModelFactory(repository, authRepository, syncManager))

  val currentUser by viewModel.currentUser.collectAsState()
  val activeTab by viewModel.activeTab.collectAsState()
  val selectedConvId by viewModel.selectedConversationId.collectAsState()
  val currentConv by viewModel.currentConversation.collectAsState()
  val securityState by viewModel.securityState.collectAsState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
  ) {
    if (currentUser == null) {
      AuthScreen(
        viewModel = viewModel,
        modifier = Modifier.fillMaxSize()
      )
    } else if (securityState.lockAppOnLaunch && securityState.isLocked) {
      BiometricLockGateScreen(
        viewModel = viewModel,
        modifier = Modifier.fillMaxSize()
      )
      SecureEnclaveModal(viewModel = viewModel)
    } else {
      Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        bottomBar = {
          if (selectedConvId == null) {
            CrystallineBottomNav(
              activeTab = activeTab,
              onTabSelected = { viewModel.selectTab(it) }
            )
          }
        }
      ) { innerPadding ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
          AnimatedContent(
            targetState = Pair(selectedConvId, activeTab),
            transitionSpec = {
              fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
            },
            label = "screen_transition"
          ) { (convId, tab) ->
            if (convId != null && currentConv != null) {
              ConversationScreen(
                viewModel = viewModel,
                conversation = currentConv!!,
                onBack = { viewModel.backToConversations() }
              )
            } else {
              when (tab) {
                VeilTab.CHATS -> ChatsScreen(
                  viewModel = viewModel,
                  onOpenSettings = { viewModel.selectTab(VeilTab.SETTINGS) }
                )
                VeilTab.VAULT -> VaultScreen(viewModel = viewModel)
                VeilTab.CALLS -> CallsScreen(viewModel = viewModel)
                VeilTab.SETTINGS -> SettingsScreen(
                  viewModel = viewModel,
                  onBack = { viewModel.selectTab(VeilTab.CHATS) }
                )
              }
            }
          }
        }
      }

      // Overlays
      SecureEnclaveModal(viewModel = viewModel)
      ViewOnceModal(viewModel = viewModel)
    }
  }
}

@Composable
fun CrystallineBottomNav(
  activeTab: VeilTab,
  onTabSelected: (VeilTab) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 20.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Surface(
      shape = RoundedCornerShape(28.dp),
      color = Color.White.copy(alpha = 0.65f),
      shadowElevation = 4.dp,
      modifier = Modifier
        .shadow(4.dp, RoundedCornerShape(28.dp))
        .border(1.dp, VeilGlassBorder, RoundedCornerShape(28.dp))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        BottomNavItem(
          icon = Icons.Default.ChatBubble,
          label = "Chats",
          isSelected = activeTab == VeilTab.CHATS,
          onClick = { onTabSelected(VeilTab.CHATS) },
          testTag = "nav_tab_chats"
        )
        BottomNavItem(
          icon = Icons.Default.Shield,
          label = "Vault",
          isSelected = activeTab == VeilTab.VAULT,
          onClick = { onTabSelected(VeilTab.VAULT) },
          testTag = "nav_tab_vault"
        )
        BottomNavItem(
          icon = Icons.Default.Call,
          label = "Calls",
          isSelected = activeTab == VeilTab.CALLS,
          onClick = { onTabSelected(VeilTab.CALLS) },
          testTag = "nav_tab_calls"
        )
        BottomNavItem(
          icon = Icons.Default.Settings,
          label = "Settings",
          isSelected = activeTab == VeilTab.SETTINGS,
          onClick = { onTabSelected(VeilTab.SETTINGS) },
          testTag = "nav_tab_settings"
        )
      }
    }
  }
}

@Composable
fun BottomNavItem(
  icon: ImageVector,
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  Box(
    modifier = Modifier
      .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
      .clip(RoundedCornerShape(20.dp))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(bounded = true),
        onClick = onClick
      )
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    if (isSelected) {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = VeilPrimaryContainer,
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = icon,
            contentDescription = label,
            tint = VeilOnPrimary,
            modifier = Modifier.size(17.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp
            ),
            color = VeilOnPrimary
          )
        }
      }
    } else {
      Column(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = label,
          tint = VeilSecondary,
          modifier = Modifier.size(20.dp)
        )
        Text(
          text = label,
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
          ),
          color = VeilSecondary
        )
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  VeilTheme { Greeting("Android") }
}
