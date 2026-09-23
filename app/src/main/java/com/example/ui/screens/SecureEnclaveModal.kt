package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.example.ui.components.CrystallineGlassBox
import com.example.ui.components.CrystallineKeypadButton
import com.example.ui.theme.VeilEmeraldShield
import com.example.ui.theme.VeilError
import com.example.ui.theme.VeilGlassBorder
import com.example.ui.theme.VeilOnPrimary
import com.example.ui.theme.VeilPrimary
import com.example.ui.theme.VeilPrimaryContainer
import com.example.ui.theme.VeilSecondary
import com.example.ui.viewmodel.VeilViewModel

@Composable
fun SecureEnclaveModal(
  viewModel: VeilViewModel,
  modifier: Modifier = Modifier
) {
  val showModal by viewModel.showEnclaveModal.collectAsState()
  val pinInput by viewModel.pinInput.collectAsState()
  val pinError by viewModel.pinError.collectAsState()
  val activity = LocalContext.current as? FragmentActivity

  LaunchedEffect(showModal) {
    if (showModal && activity != null) {
      viewModel.triggerBiometricUnlock(activity)
    }
  }

  AnimatedVisibility(
    visible = showModal,
    enter = fadeIn(),
    exit = fadeOut()
  ) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.55f))
        .clickable(onClick = { viewModel.closeEnclaveModal() }),
      contentAlignment = Alignment.Center
    ) {
      // Enclave modal dialog
      CrystallineGlassBox(
        shape = RoundedCornerShape(28.dp),
        backgroundColor = Color(0xFFFBFBFC).copy(alpha = 0.94f),
        borderColor = VeilGlassBorder,
        elevation = 8.dp,
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .padding(vertical = 24.dp)
          .clickable(enabled = false) {}
          .testTag("secure_enclave_modal")
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Close button on top right
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            IconButton(
              onClick = { viewModel.closeEnclaveModal() },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = VeilSecondary
              )
            }
          }

          // Shield Emblem
          Box(
            modifier = Modifier
              .size(64.dp)
              .shadow(2.dp, CircleShape)
              .clip(CircleShape)
              .background(VeilPrimary)
              .border(2.dp, VeilGlassBorder, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = "Enclave Shield",
              tint = Color.White,
              modifier = Modifier.size(30.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "Secure Enclave",
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 22.sp
            ),
            color = VeilPrimary
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "This conversation is sealed with zero-knowledge, hardware-isolated encryption.",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontSize = 13.sp,
              lineHeight = 18.sp
            ),
            color = VeilSecondary,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Tech Specs Badge
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = VeilPrimary.copy(alpha = 0.06f),
              modifier = Modifier.border(0.5.dp, VeilPrimary.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            ) {
              Text(
                text = "Protocol Curve25519",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 10.sp
                ),
                color = VeilPrimary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = VeilEmeraldShield.copy(alpha = 0.12f),
              modifier = Modifier.border(0.5.dp, VeilEmeraldShield.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            ) {
              Text(
                text = "Status: Enclave Armored",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 10.sp
                ),
                color = VeilEmeraldShield,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // 4 PIN Dots
          Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            (0..3).forEach { index ->
              val isFilled = index < pinInput.length
              Box(
                modifier = Modifier
                  .size(16.dp)
                  .clip(CircleShape)
                  .background(
                    if (isFilled) VeilPrimary else Color.Transparent
                  )
                  .border(
                    width = 1.5.dp,
                    color = if (isFilled) VeilPrimary else VeilSecondary.copy(alpha = 0.4f),
                    shape = CircleShape
                  )
              )
            }
          }

          if (pinError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = pinError ?: "",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
              ),
              color = VeilError
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Keypad Grid
          Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Row 1
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
              CrystallineKeypadButton(digit = "1", letters = "", onClick = { viewModel.enterPinDigit("1") })
              CrystallineKeypadButton(digit = "2", letters = "ABC", onClick = { viewModel.enterPinDigit("2") })
              CrystallineKeypadButton(digit = "3", letters = "DEF", onClick = { viewModel.enterPinDigit("3") })
            }
            // Row 2
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
              CrystallineKeypadButton(digit = "4", letters = "GHI", onClick = { viewModel.enterPinDigit("4") })
              CrystallineKeypadButton(digit = "5", letters = "JKL", onClick = { viewModel.enterPinDigit("5") })
              CrystallineKeypadButton(digit = "6", letters = "MNO", onClick = { viewModel.enterPinDigit("6") })
            }
            // Row 3
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
              CrystallineKeypadButton(digit = "7", letters = "PQRS", onClick = { viewModel.enterPinDigit("7") })
              CrystallineKeypadButton(digit = "8", letters = "TUV", onClick = { viewModel.enterPinDigit("8") })
              CrystallineKeypadButton(digit = "9", letters = "WXYZ", onClick = { viewModel.enterPinDigit("9") })
            }
            // Row 4
            Row(
              horizontalArrangement = Arrangement.spacedBy(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Biometric quick unlock
              Box(
                modifier = Modifier
                  .size(72.dp)
                  .clip(CircleShape)
                  .clickable(onClick = {
                    if (activity != null) {
                      viewModel.triggerBiometricUnlock(activity)
                    } else {
                      viewModel.authenticateWithBiometrics()
                    }
                  })
                  .testTag("btn_biometric_unlock"),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Fingerprint,
                  contentDescription = "Biometric Unlock",
                  tint = VeilPrimary,
                  modifier = Modifier.size(30.dp)
                )
              }

              CrystallineKeypadButton(digit = "0", letters = "+", onClick = { viewModel.enterPinDigit("0") })

              // Backspace
              Box(
                modifier = Modifier
                  .size(72.dp)
                  .clip(CircleShape)
                  .clickable(onClick = { viewModel.deletePinDigit() })
                  .testTag("btn_pin_backspace"),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Backspace,
                  contentDescription = "Backspace",
                  tint = VeilSecondary,
                  modifier = Modifier.size(24.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Biometrics CTA
          Button(
            onClick = {
              if (activity != null) {
                viewModel.triggerBiometricUnlock(activity)
              } else {
                viewModel.authenticateWithBiometrics()
              }
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = VeilPrimaryContainer,
              contentColor = VeilOnPrimary
            ),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(46.dp)
              .testTag("btn_authenticate_biometrics")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Authenticate with Biometrics",
                style = MaterialTheme.typography.labelLarge.copy(
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 13.sp
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Session auto-clears after 15 minutes of inactivity.",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 11.sp,
              color = VeilSecondary.copy(alpha = 0.7f)
            ),
            textAlign = TextAlign.Center
          )
        }
      }
    }
  }
}
