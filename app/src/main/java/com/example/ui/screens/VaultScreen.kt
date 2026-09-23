package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.theme.VeilGlassBorder
import com.example.ui.theme.VeilOnPrimary
import com.example.ui.theme.VeilOnSurface
import com.example.ui.theme.VeilPrimary
import com.example.ui.theme.VeilPrimaryContainer
import com.example.ui.theme.VeilQuantumCyan
import com.example.ui.theme.VeilSecondary
import com.example.ui.viewmodel.VeilViewModel

@Composable
fun VaultScreen(
  viewModel: VeilViewModel,
  modifier: Modifier = Modifier
) {
  val securityState by viewModel.securityState.collectAsState()
  val rawConversations by viewModel.rawConversations.collectAsState()
  val vaultedChats = rawConversations.filter { it.isVaulted }

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
            text = "Zero-Knowledge Vault",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            ),
            color = VeilPrimary
          )
          Text(
            text = "HARDWARE ISOLATED STORAGE",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.2.sp,
              fontSize = 10.sp
            ),
            color = VeilSecondary
          )
        }

        CrystallineIconButton(
          icon = if (securityState.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
          contentDescription = "Lock/Unlock",
          tint = if (securityState.isLocked) VeilEmeraldShield else VeilPrimary,
          onClick = {
            if (securityState.isLocked) viewModel.openEnclaveModal() else viewModel.lockEnclave()
          },
          testTag = "btn_vault_lock_toggle"
        )
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Vault Armor Status Card
        item {
          CrystallineGlassBox(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(if (securityState.isLocked) VeilPrimary else VeilEmeraldShield),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (securityState.isLocked) Icons.Default.Lock else Icons.Default.Shield,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(24.dp)
                )
              }

              Spacer(modifier = Modifier.width(14.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = if (securityState.isLocked) "Enclave Sealed" else "Enclave Armored & Active",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                  ),
                  color = VeilOnSurface
                )
                Text(
                  text = if (securityState.isLocked) "Enter 4-digit PIN or optical biometric to decrypt" else "Zero-Knowledge session verified • Curve25519",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                  color = VeilSecondary
                )
              }
            }
          }
        }

        // Vaulted conversations section
        item {
          Text(
            text = "SEALED CONVERSATIONS (${vaultedChats.size})",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.2.sp,
              fontSize = 11.sp
            ),
            color = VeilSecondary.copy(alpha = 0.8f),
            modifier = Modifier.padding(start = 4.dp, top = 6.dp)
          )
        }

        item {
          CrystallineGlassBox(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
              vaultedChats.forEachIndexed { index, chat ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectConversation(chat.id) }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(38.dp)
                      .clip(CircleShape)
                      .background(VeilPrimaryContainer),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Lock,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(16.dp)
                    )
                  }

                  Spacer(modifier = Modifier.width(12.dp))

                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = chat.title,
                      style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                      ),
                      color = VeilOnSurface
                    )
                    Text(
                      text = "Sealed with Hardware Key",
                      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                      color = VeilSecondary
                    )
                  }

                  Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = VeilSecondary
                  )
                }

                if (index < vaultedChats.size - 1) {
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(0.5.dp)
                      .background(VeilGlassBorder)
                  )
                }
              }
            }
          }
        }

        // Vault items
        item {
          Text(
            text = "HARDWARE CERTIFICATES & ASSETS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.2.sp,
              fontSize = 11.sp
            ),
            color = VeilSecondary.copy(alpha = 0.8f),
            modifier = Modifier.padding(start = 4.dp, top = 6.dp)
          )
        }

        item {
          CrystallineGlassBox(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
              VaultAssetItem(
                icon = Icons.Default.Key,
                title = "Root Quantum Keyring",
                subtitle = "Fingerprint: 0x9F4E...C21B",
                badge = "ECC-256"
              )
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(0.5.dp)
                  .background(VeilGlassBorder)
              )
              VaultAssetItem(
                icon = Icons.Default.FolderSpecial,
                title = "Veil Lens Aperture RAW File",
                subtitle = "4.8 MB • Encrypted Blob",
                badge = "VIEW ONCE"
              )
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(0.5.dp)
                  .background(VeilGlassBorder)
              )
              VaultAssetItem(
                icon = Icons.Default.Description,
                title = "Hardware FIDO2 Enclave Spec",
                subtitle = "Zero-Knowledge Proof Attestation",
                badge = "VERIFIED"
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun VaultAssetItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  badge: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = VeilPrimary,
      modifier = Modifier.size(20.dp)
    )

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.sp
        ),
        color = VeilOnSurface
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
        color = VeilSecondary
      )
    }

    Surface(
      shape = RoundedCornerShape(10.dp),
      color = VeilPrimary.copy(alpha = 0.08f)
    ) {
      Text(
        text = badge,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 9.sp,
          letterSpacing = 0.5.sp
        ),
        color = VeilPrimary,
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
      )
    }
  }
}
