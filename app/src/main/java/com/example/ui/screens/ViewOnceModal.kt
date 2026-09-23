package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CrystallineGlassBox
import com.example.ui.theme.VeilEmeraldShield
import com.example.ui.theme.VeilError
import com.example.ui.theme.VeilGlassBorder
import com.example.ui.theme.VeilPrimary
import com.example.ui.theme.VeilPrimaryContainer
import com.example.ui.theme.VeilQuantumCyan
import com.example.ui.theme.VeilSecondary
import com.example.ui.viewmodel.VeilViewModel
import kotlinx.coroutines.delay

@Composable
fun ViewOnceModal(
  viewModel: VeilViewModel,
  modifier: Modifier = Modifier
) {
  val viewOnceMessage by viewModel.viewOnceMessage.collectAsState()

  AnimatedVisibility(
    visible = viewOnceMessage != null,
    enter = fadeIn(),
    exit = fadeOut()
  ) {
    viewOnceMessage?.let { msg ->
      var countdown by remember(msg.id) { mutableIntStateOf(15) }

      LaunchedEffect(msg.id) {
        while (countdown > 0) {
          delay(1000)
          countdown--
        }
        viewModel.closeAndBurnViewOnce(msg.id)
      }

      Box(
        modifier = modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.88f))
          .padding(16.dp),
        contentAlignment = Alignment.Center
      ) {
        CrystallineGlassBox(
          shape = RoundedCornerShape(24.dp),
          backgroundColor = Color(0xFF14171E).copy(alpha = 0.95f),
          borderColor = Color.White.copy(alpha = 0.3f),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("view_once_modal")
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Header
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = VeilError.copy(alpha = 0.2f),
                modifier = Modifier.border(0.5.dp, VeilError.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = VeilError,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "BURNING IN ${countdown}s",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    ),
                    color = Color.White
                  )
                }
              }

              IconButton(
                onClick = { viewModel.closeAndBurnViewOnce(msg.id) },
                modifier = Modifier.size(36.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Burn and Exit",
                  tint = Color.White
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Decrypted image container
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
              contentAlignment = Alignment.Center
            ) {
              Image(
                painter = painterResource(id = R.drawable.img_crystalline_preview),
                contentDescription = "Decrypted Lens Aperture RAW Asset",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )

              // Security Watermark overlay
              Text(
                text = "ZERO-KNOWLEDGE ENCLAVE • SESSION #4092\nCRYPTO: ECDH-25519-AES256",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                ),
                color = Color.White.copy(alpha = 0.45f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                  .align(Alignment.BottomCenter)
                  .padding(bottom = 12.dp)
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = msg.attachmentName ?: "Veil_Lens_Aperture.raw",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              ),
              color = Color.White
            )

            Text(
              text = "Decrypted in ephemeral RAM cache. Zero traces written to disk.",
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.7f)
              ),
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = { viewModel.closeAndBurnViewOnce(msg.id) },
              colors = ButtonDefaults.buttonColors(
                containerColor = VeilError,
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(18.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("btn_burn_media_now")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.LocalFireDepartment,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Burn & Expunge Now",
                  style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
              }
            }
          }
        }
      }
    }
  }
}
