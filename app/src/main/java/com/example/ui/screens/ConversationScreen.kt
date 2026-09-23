package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Conversation
import com.example.data.model.Message
import com.example.ui.components.CrystallineGlassBox
import com.example.ui.components.CrystallineIconButton
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
import com.example.ui.viewmodel.VeilViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConversationScreen(
  viewModel: VeilViewModel,
  conversation: Conversation,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val messages by viewModel.currentMessages.collectAsState()
  val playingMemoId by viewModel.playingVoiceMemoId.collectAsState()
  var textInput by remember { mutableStateOf("") }
  var showAttachmentMenu by remember { mutableStateOf(false) }

  val listState = rememberLazyListState()

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

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
    Column(modifier = Modifier.fillMaxSize()) {
      // Top Header
      ConversationTopBar(
        conversation = conversation,
        onBack = onBack
      )

      // Quantum Enclave Protocol Banner
      QuantumSecurityBanner()

      // Messages List
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(messages, key = { it.id }) { message ->
          if (message.isMe) {
            OutgoingMessageBubble(message = message)
          } else {
            IncomingMessageBubble(
              message = message,
              isPlayingVoice = playingMemoId == message.id,
              onPlayVoice = { viewModel.toggleVoicePlayback(message.id) },
              onOpenAttachment = { viewModel.openViewOnce(message) },
              onAddReaction = { emoji -> viewModel.addReaction(message.id, emoji) }
            )
          }
        }
      }

      // Quick attachment drawer
      AnimatedVisibility(
        visible = showAttachmentMenu,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        AttachmentPickerBar(
          onSendMedia = {
            viewModel.sendApertureMedia()
            showAttachmentMenu = false
          },
          onSendVoice = {
            viewModel.sendVoiceMemo("0:32")
            showAttachmentMenu = false
          },
          onClose = { showAttachmentMenu = false }
        )
      }

      // Bottom Crystalline Input Bar
      CrystallineMessageInputBar(
        text = textInput,
        onTextChange = { textInput = it },
        onSend = {
          if (textInput.isNotBlank()) {
            viewModel.sendMessage(textInput)
            textInput = ""
          }
        },
        onAttachClick = { showAttachmentMenu = !showAttachmentMenu },
        onMicClick = {
          viewModel.sendVoiceMemo("0:19")
        }
      )
    }
  }
}

@Composable
private fun ConversationTopBar(
  conversation: Conversation,
  onBack: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color.White.copy(alpha = 0.5f))
      .padding(horizontal = 8.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    CrystallineIconButton(
      icon = Icons.AutoMirrored.Filled.ArrowBack,
      contentDescription = "Back",
      onClick = onBack,
      testTag = "btn_conversation_back"
    )

    Spacer(modifier = Modifier.width(6.dp))

    // Contact avatar & status
    Box(
      modifier = Modifier
        .size(40.dp)
        .clip(CircleShape)
        .background(if (conversation.isVaulted) VeilPrimaryContainer else Color(0xFFE2E8F0)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = conversation.title.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        ),
        color = if (conversation.isVaulted) Color.White else VeilPrimary
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = conversation.title,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.SemiBold,
          fontSize = 15.sp
        ),
        color = VeilOnSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(VeilEmeraldShield)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Veil Quantum Key • Active End-to-End",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = VeilSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
      CrystallineIconButton(
        icon = Icons.Default.Call,
        contentDescription = "Encrypted Call",
        onClick = { /* Call */ },
        size = 38.dp,
        testTag = "btn_encrypted_call"
      )
      CrystallineIconButton(
        icon = Icons.Default.Videocam,
        contentDescription = "Encrypted Video",
        onClick = { /* Video */ },
        size = 38.dp,
        testTag = "btn_encrypted_video"
      )
    }
  }
}

@Composable
private fun QuantumSecurityBanner() {
  Surface(
    color = Color.White.copy(alpha = 0.4f),
    modifier = Modifier
      .fillMaxWidth()
      .border(0.5.dp, VeilGlassBorder, RoundedCornerShape(0.dp))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Lock,
        contentDescription = null,
        tint = VeilEmeraldShield,
        modifier = Modifier.size(12.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Veil Vault Enforced • ECDH-Curve25519 • Zero Knowledge",
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 10.sp,
          fontWeight = FontWeight.Medium,
          letterSpacing = 0.4.sp
        ),
        color = VeilSecondary
      )
    }
  }
}

@Composable
private fun OutgoingMessageBubble(message: Message) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("msg_outgoing_${message.id}"),
    horizontalArrangement = Arrangement.End
  ) {
    Surface(
      shape = RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
      color = VeilPrimaryContainer,
      shadowElevation = 1.dp,
      modifier = Modifier
        .fillMaxWidth(0.82f)
        .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
    ) {
      Column(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
        Text(
          text = message.text,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = Color.White,
            fontSize = 14.sp,
            lineHeight = 20.sp
          )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.align(Alignment.End),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp)),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = Color.White.copy(alpha = 0.6f)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = Icons.Default.DoneAll,
            contentDescription = "Delivered",
            tint = VeilQuantumCyan,
            modifier = Modifier.size(13.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun IncomingMessageBubble(
  message: Message,
  isPlayingVoice: Boolean,
  onPlayVoice: () -> Unit,
  onOpenAttachment: () -> Unit,
  onAddReaction: (String) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("msg_incoming_${message.id}"),
    horizontalArrangement = Arrangement.Start
  ) {
    Column(modifier = Modifier.fillMaxWidth(0.85f)) {
      CrystallineGlassBox(
        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
        backgroundColor = Color.White.copy(alpha = 0.58f),
        borderColor = VeilGlassBorder
      ) {
        Column(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
          // Normal text
          if (!message.isVoiceMemo && !message.isAttachment) {
            Text(
              text = message.text,
              style = MaterialTheme.typography.bodyMedium.copy(
                color = VeilOnSurface,
                fontSize = 14.sp,
                lineHeight = 20.sp
              )
            )
          }

          // Voice memo bubble
          if (message.isVoiceMemo) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(VeilPrimary)
                  .clickable(onClick = onPlayVoice),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (isPlayingVoice) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = if (isPlayingVoice) "Pause" else "Play",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }

              Spacer(modifier = Modifier.width(10.dp))

              Column(modifier = Modifier.weight(1f)) {
                // Waveform simulation
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                  listOf(6, 12, 18, 14, 8, 16, 20, 15, 10, 14, 18, 12, 8, 14, 19, 11, 7, 13, 16, 9)
                    .forEach { h ->
                      Box(
                        modifier = Modifier
                          .weight(1f)
                          .height(h.dp)
                          .clip(RoundedCornerShape(1.dp))
                          .background(if (isPlayingVoice) VeilQuantumCyan else VeilPrimary.copy(alpha = 0.65f))
                      )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "Encrypted Voice Memo",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = VeilSecondary
                  )
                  Text(
                    text = message.voiceDuration ?: "0:42",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = VeilSecondary
                  )
                }
              }
            }
          }

          // Attachment / View Once card
          if (message.isAttachment) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, VeilGlassBorder, RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.45f))
                .clickable(onClick = onOpenAttachment)
                .padding(10.dp)
            ) {
              // Lens aperture preview image
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(130.dp)
                  .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
              ) {
                Image(
                  painter = painterResource(id = R.drawable.img_crystalline_preview),
                  contentDescription = "Veil Lens Aperture RAW Preview",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )

                // View Once Badge Overlay
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = VeilPrimary.copy(alpha = 0.85f),
                  modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.Timer,
                      contentDescription = null,
                      tint = VeilQuantumCyan,
                      modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "VIEW ONCE",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                      ),
                      color = Color.White
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = message.attachmentName ?: "Attachment",
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 13.sp
                    ),
                    color = VeilOnSurface
                  )
                  Text(
                    text = "${message.attachmentSize ?: "4.8 MB"} • Tap to inspect",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = VeilSecondary
                  )
                }

                Surface(
                  shape = RoundedCornerShape(14.dp),
                  color = VeilPrimaryContainer,
                  modifier = Modifier.shadow(0.5.dp)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.Visibility,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "Decrypt",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                      ),
                      color = Color.White
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Timestamp
          Text(
            text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp)),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = VeilSecondary.copy(alpha = 0.8f),
            modifier = Modifier.align(Alignment.End)
          )
        }
      }

      // Reaction pill row
      Row(
        modifier = Modifier
          .padding(top = 4.dp, start = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        listOf("✨", "🔒", "🤍", "👍").forEach { emoji ->
          val isCurrent = message.reactionEmoji == emoji
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isCurrent) VeilPrimaryContainer else Color.White.copy(alpha = 0.55f),
            shadowElevation = 0.5.dp,
            modifier = Modifier
              .clickable { onAddReaction(emoji) }
              .border(
                0.5.dp,
                if (isCurrent) Color.White.copy(alpha = 0.3f) else VeilGlassBorder,
                RoundedCornerShape(12.dp)
              )
          ) {
            Text(
              text = emoji,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun AttachmentPickerBar(
  onSendMedia: () -> Unit,
  onSendVoice: () -> Unit,
  onClose: () -> Unit
) {
  CrystallineGlassBox(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp),
    shape = RoundedCornerShape(16.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .clickable(onClick = onSendMedia)
          .padding(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(VeilPrimary),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.AttachFile,
            contentDescription = "Send Media RAW",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Aperture RAW",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
          color = VeilOnSurface
        )
      }

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .clickable(onClick = onSendVoice)
          .padding(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(VeilQuantumCyan),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.GraphicEq,
            contentDescription = "Voice Memo",
            tint = VeilPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Voice Memo",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
          color = VeilOnSurface
        )
      }
    }
  }
}

@Composable
private fun CrystallineMessageInputBar(
  text: String,
  onTextChange: (String) -> Unit,
  onSend: () -> Unit,
  onAttachClick: () -> Unit,
  onMicClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .shadow(2.dp, RoundedCornerShape(26.dp))
        .clip(RoundedCornerShape(26.dp))
        .background(Color.White.copy(alpha = 0.65f))
        .border(1.dp, VeilGlassBorder, RoundedCornerShape(26.dp))
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onAttachClick,
        modifier = Modifier.size(38.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Attachment",
          tint = VeilPrimary,
          modifier = Modifier.size(22.dp)
        )
      }

      TextField(
        value = text,
        onValueChange = onTextChange,
        placeholder = {
          Text(
            text = "Message (End-to-End Encrypted)...",
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
          .testTag("input_message_text")
      )

      if (text.isNotBlank()) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(VeilPrimary)
            .clickable(onClick = onSend)
            .testTag("btn_send_message"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      } else {
        IconButton(
          onClick = onMicClick,
          modifier = Modifier
            .size(38.dp)
            .testTag("btn_mic_memo")
        ) {
          Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Voice Memo",
            tint = VeilPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}
