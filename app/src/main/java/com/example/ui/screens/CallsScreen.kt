package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CrystallineGlassBox
import com.example.ui.components.CrystallineIconButton
import com.example.ui.theme.VeilBgGradientEnd
import com.example.ui.theme.VeilBgGradientMid
import com.example.ui.theme.VeilBgGradientStart
import com.example.ui.theme.VeilEmeraldShield
import com.example.ui.theme.VeilError
import com.example.ui.theme.VeilGlassBorder
import com.example.ui.theme.VeilOnPrimary
import com.example.ui.theme.VeilOnSurface
import com.example.ui.theme.VeilPrimary
import com.example.ui.theme.VeilPrimaryContainer
import com.example.ui.theme.VeilQuantumCyan
import com.example.ui.theme.VeilSecondary
import com.example.ui.viewmodel.VeilViewModel

data class EncryptedCallRecord(
  val id: Long,
  val name: String,
  val type: String, // Audio, Video
  val direction: String, // Incoming, Outgoing, Missed
  val duration: String,
  val time: String,
  val isMissed: Boolean = false
)

@Composable
fun CallsScreen(
  viewModel: VeilViewModel,
  modifier: Modifier = Modifier
) {
  val calls = listOf(
    EncryptedCallRecord(1, "Elena Vance", "Video", "Incoming", "14:22", "Today, 10:15 AM"),
    EncryptedCallRecord(2, "Elena Vance", "Audio", "Outgoing", "03:48", "Yesterday, 4:20 PM"),
    EncryptedCallRecord(3, "Julian Thorne", "Audio", "Missed", "0:00", "Sep 21, 6:02 PM", isMissed = true),
    EncryptedCallRecord(4, "Sophia Lin", "Video", "Outgoing", "28:10", "Sep 19, 2:45 PM")
  )

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
      // Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Calls",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            ),
            color = VeilPrimary
          )
          Text(
            text = "QUANTUM-ENCRYPTED PEER-TO-PEER",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.2.sp,
              fontSize = 10.sp
            ),
            color = VeilSecondary
          )
        }

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = VeilEmeraldShield.copy(alpha = 0.12f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = VeilEmeraldShield,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "P2P Shielded",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              ),
              color = VeilEmeraldShield
            )
          }
        }
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(calls, key = { it.id }) { call ->
          CrystallineGlassBox(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(if (call.name == "Elena Vance") VeilPrimaryContainer else Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = call.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  ),
                  color = if (call.name == "Elena Vance") Color.White else VeilPrimary
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = call.name,
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                  ),
                  color = if (call.isMissed) VeilError else VeilOnSurface
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = when {
                      call.isMissed -> Icons.AutoMirrored.Filled.CallMissed
                      call.direction == "Incoming" -> Icons.AutoMirrored.Filled.CallReceived
                      else -> Icons.AutoMirrored.Filled.CallMade
                    },
                    contentDescription = null,
                    tint = if (call.isMissed) VeilError else VeilEmeraldShield,
                    modifier = Modifier.size(13.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "${call.direction} ${call.type} • ${if (call.isMissed) "Missed" else call.duration}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = VeilSecondary
                  )
                }
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = call.time,
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                  color = VeilSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                  imageVector = if (call.type == "Video") Icons.Default.Videocam else Icons.Default.Call,
                  contentDescription = "Recall",
                  tint = VeilPrimary,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }
      }
    }

    // New Call FAB
    FloatingActionButton(
      onClick = { viewModel.selectConversation(1) },
      shape = CircleShape,
      containerColor = VeilPrimary,
      contentColor = VeilOnPrimary,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 20.dp, bottom = 84.dp)
        .testTag("fab_new_call")
        .shadow(6.dp, CircleShape)
    ) {
      Icon(
        imageVector = Icons.Default.Call,
        contentDescription = "New Call",
        modifier = Modifier.size(22.dp)
      )
    }
  }
}
