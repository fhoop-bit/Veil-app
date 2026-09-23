package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Conversation
import com.example.data.model.User
import com.example.ui.components.CrystallineFilterPill
import com.example.ui.components.CrystallineGlassBox
import com.example.ui.components.CrystallineIconButton
import com.example.ui.components.PriorityContactItem
import com.example.ui.theme.VeilBgGradientEnd
import com.example.ui.theme.VeilBgGradientMid
import com.example.ui.theme.VeilBgGradientStart
import com.example.ui.theme.VeilEmeraldShield
import com.example.ui.theme.VeilGlassBorder
import com.example.ui.theme.VeilOnPrimary
import com.example.ui.theme.VeilOnSurface
import com.example.ui.theme.VeilPrimary
import com.example.ui.theme.VeilPrimaryContainer
import com.example.ui.theme.VeilQuantumCyan
import com.example.ui.theme.VeilSecondary
import com.example.ui.viewmodel.FilterCategory
import com.example.ui.viewmodel.VeilViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatsScreen(
  viewModel: VeilViewModel,
  onOpenSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  val conversations by viewModel.conversations.collectAsState()
  val priorityContacts by viewModel.priorityContacts.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
    val filterCategory by viewModel.filterCategory.collectAsState()
  val securityState by viewModel.securityState.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            VeilBgGradientStart,
            VeilBgGradientMid,
            VeilBgGradientEnd
          )
        )
      )
  ) {
    // Top radial glow atmosphere
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .background(
          Brush.radialGradient(
            colors = listOf(
              Color.White.copy(alpha = 0.85f),
              Color.Transparent
            ),
            radius = 650f
          )
        )
    )

    Column(
      modifier = Modifier.fillMaxSize()
    ) {
      // Top Crystal Bar Header
      ChatsTopBar(
        user = currentUser,
        isEnclaveLocked = securityState.isLocked,
        onLockToggle = {
          if (securityState.isLocked) {
            viewModel.openEnclaveModal()
          } else {
            viewModel.lockEnclave()
          }
        },
        onSettingsClick = onOpenSettings
      )

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp)
      ) {
        // Search bar
        item {
          CrystallineSearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.setSearchQuery(it) }
          )
          Spacer(modifier = Modifier.height(14.dp))
        }

        // Filter Pills
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            CrystallineFilterPill(
              label = "All",
              isSelected = filterCategory == FilterCategory.ALL,
              onClick = { viewModel.setFilterCategory(FilterCategory.ALL) }
            )
            CrystallineFilterPill(
              label = "Unread",
              isSelected = filterCategory == FilterCategory.UNREAD,
              onClick = { viewModel.setFilterCategory(FilterCategory.UNREAD) }
            )
            CrystallineFilterPill(
              label = "Vaulted",
              icon = Icons.Default.Lock,
              isSelected = filterCategory == FilterCategory.VAULTED,
              onClick = { viewModel.setFilterCategory(FilterCategory.VAULTED) }
            )
            CrystallineFilterPill(
              label = "Channels",
              isSelected = filterCategory == FilterCategory.CHANNELS,
              onClick = { viewModel.setFilterCategory(FilterCategory.CHANNELS) }
            )
          }
          Spacer(modifier = Modifier.height(18.dp))
        }

        // Priority row
        item {
          Text(
            text = "PRIORITY SECURE CHANNELS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.2.sp,
              fontSize = 11.sp
            ),
            color = VeilSecondary.copy(alpha = 0.8f),
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
          )

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
          ) {
            items(priorityContacts, key = { it.id }) { contact ->
              PriorityContactItem(
                contact = contact,
                onClick = { viewModel.selectConversation(contact.id) }
              )
            }

            // Invite / Add item
            item {
              Column(
                modifier = Modifier
                  .defaultMinSize(minWidth = 60.dp, minHeight = 64.dp)
                  .testTag("invite_contact_button")
                  .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true),
                    onClick = { /* New encrypted pair */ }
                  )
                  .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Box(
                  modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f))
                    .border(1.dp, VeilGlassBorder, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Contact",
                    tint = VeilPrimary,
                    modifier = Modifier.size(22.dp)
                  )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Invite",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                  ),
                  color = VeilSecondary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(24.dp))
        }

        // Section header
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${conversations.size} Active",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
              ),
              color = VeilOnSurface
            )

            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.clickable { /* Sort */ }
            ) {
              Text(
                text = "Quantum Sorted",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Medium,
                  fontSize = 11.sp
                ),
                color = VeilSecondary
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
        }

        // Conversation items
        if (conversations.isEmpty()) {
          item {
            CrystallineGlassBox(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Icon(
                  imageVector = Icons.Default.Shield,
                  contentDescription = null,
                  tint = VeilSecondary,
                  modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = "No encrypted conversations found",
                  style = MaterialTheme.typography.bodyMedium,
                  color = VeilSecondary
                )
              }
            }
          }
        } else {
          items(conversations, key = { it.id }) { conv ->
            ConversationCard(
              conversation = conv,
              onClick = { viewModel.selectConversation(conv.id) }
            )
            Spacer(modifier = Modifier.height(10.dp))
          }
        }
      }
    }

    // Floating Action Button
    FloatingActionButton(
      onClick = {
        // Select Elena Vance or open new cipher chat
        viewModel.selectConversation(1)
      },
      shape = CircleShape,
      containerColor = VeilPrimary,
      contentColor = VeilOnPrimary,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 20.dp, bottom = 84.dp)
        .testTag("fab_new_message")
        .shadow(6.dp, CircleShape)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Edit,
          contentDescription = "New Message",
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "New Cipher",
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
        )
      }
    }
  }
}

@Composable
private fun ChatsTopBar(
  user: User?,
  isEnclaveLocked: Boolean,
  onLockToggle: () -> Unit,
  onSettingsClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Brand emblem & title
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.7f))
          .border(1.dp, VeilGlassBorder, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_veil_emblem),
          contentDescription = "Veil Crystal Emblem",
          modifier = Modifier.size(24.dp)
        )
      }

      Column {
        Text(
          text = "Veil",
          style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
            fontSize = 22.sp
          ),
          color = VeilPrimary
        )
        Text(
          text = "CHATS",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            fontSize = 9.sp
          ),
          color = VeilSecondary.copy(alpha = 0.8f)
        )
      }
    }

    // Top action buttons
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Enclave status button
      CrystallineIconButton(
        icon = if (isEnclaveLocked) Icons.Default.Lock else Icons.Default.Security,
        contentDescription = if (isEnclaveLocked) "Unlock Enclave" else "Lock Enclave",
        tint = if (isEnclaveLocked) VeilEmeraldShield else VeilPrimary,
        onClick = onLockToggle,
        testTag = "btn_toggle_enclave_lock"
      )

      // Settings & Profile Avatar button
      val initials = (user?.displayName ?: "EV")
        .split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .ifEmpty { "EV" }

      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.7f))
          .border(1.dp, VeilGlassBorder, CircleShape)
          .clickable { onSettingsClick() }
          .testTag("btn_top_settings"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = initials,
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          ),
          color = VeilPrimary
        )
      }
    }
  }
}

@Composable
private fun CrystallineSearchBar(
  query: String,
  onQueryChange: (String) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(50.dp)
      .shadow(1.dp, RoundedCornerShape(25.dp))
      .clip(RoundedCornerShape(25.dp))
      .background(Color.White.copy(alpha = 0.55f))
      .border(1.dp, VeilGlassBorder, RoundedCornerShape(25.dp)),
    contentAlignment = Alignment.CenterStart
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = "Search",
        tint = VeilSecondary,
        modifier = Modifier.size(20.dp)
      )

      Spacer(modifier = Modifier.width(10.dp))

      TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
          Text(
            text = "Search conversations & encrypted keys...",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = VeilSecondary.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        },
        colors = TextFieldDefaults.colors(
          focusedContainerColor = Color.Transparent,
          unfocusedContainerColor = Color.Transparent,
          focusedIndicatorColor = Color.Transparent,
          unfocusedIndicatorColor = Color.Transparent,
          cursorColor = VeilPrimary
        ),
        singleLine = true,
        modifier = Modifier
          .weight(1f)
          .testTag("input_search_conversations")
      )

      IconButton(
        onClick = { /* Voice search */ },
        modifier = Modifier.size(36.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Mic,
          contentDescription = "Voice filter",
          tint = VeilSecondary,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

@Composable
private fun ConversationCard(
  conversation: Conversation,
  onClick: () -> Unit
) {
  CrystallineGlassBox(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("conversation_card_${conversation.id}")
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(bounded = true),
        onClick = onClick
      )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Avatar with vault status
      Box(
        modifier = Modifier.size(46.dp),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(
              if (conversation.isVaulted) VeilPrimaryContainer else Color(0xFFE2E8F0)
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = conversation.title.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            ),
            color = if (conversation.isVaulted) Color.White else VeilPrimary
          )
        }

        if (conversation.isVaulted) {
          Box(
            modifier = Modifier
              .size(16.dp)
              .align(Alignment.BottomEnd)
              .shadow(1.dp, CircleShape)
              .clip(CircleShape)
              .background(VeilPrimary)
              .border(1.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Vaulted",
              tint = Color.White,
              modifier = Modifier.size(9.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Middle content (Title, Last message)
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = conversation.title,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.SemiBold,
              fontSize = 15.sp
            ),
            color = VeilOnSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
          )

          if (conversation.isVaulted) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = VeilPrimary.copy(alpha = 0.08f),
              modifier = Modifier.border(0.5.dp, VeilPrimary.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            ) {
              Text(
                text = "VAULT",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 8.sp,
                  letterSpacing = 1.sp
                ),
                color = VeilPrimary,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (conversation.isVoiceMemo) {
            Icon(
              imageVector = Icons.Default.GraphicEq,
              contentDescription = "Voice Memo",
              tint = VeilQuantumCyan,
              modifier = Modifier
                .size(15.dp)
                .padding(end = 4.dp)
            )
          }

          Text(
            text = conversation.lastMessage,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontSize = 13.sp,
              fontWeight = if (conversation.unreadCount > 0) FontWeight.Medium else FontWeight.Normal
            ),
            color = if (conversation.unreadCount > 0) VeilOnSurface else VeilSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Right: Timestamp and Unread badge
      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = formatTimestamp(conversation.lastTimestamp),
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
          color = VeilSecondary.copy(alpha = 0.8f)
        )

        if (conversation.unreadCount > 0) {
          Box(
            modifier = Modifier
              .defaultMinSize(minWidth = 19.dp, minHeight = 19.dp)
              .clip(CircleShape)
              .background(VeilPrimary)
              .padding(horizontal = 5.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = conversation.unreadCount.toString(),
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              ),
              color = Color.White
            )
          }
        }
      }
    }
  }
}

private fun formatTimestamp(timestamp: Long): String {
  val diff = System.currentTimeMillis() - timestamp
  return when {
    diff < 60_000 -> "Just now"
    diff < 3600_000 -> "${diff / 60_000}m"
    diff < 86400_000 -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
    diff < 172800_000 -> "Yesterday"
    else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
  }
}
