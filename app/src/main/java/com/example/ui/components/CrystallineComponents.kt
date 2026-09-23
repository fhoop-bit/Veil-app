package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Contact
import com.example.ui.theme.VeilEmeraldShield
import com.example.ui.theme.VeilGlassBorder
import com.example.ui.theme.VeilGlassBorderSubtle
import com.example.ui.theme.VeilGlassWhite
import com.example.ui.theme.VeilOnPrimary
import com.example.ui.theme.VeilOnSurface
import com.example.ui.theme.VeilPrimary
import com.example.ui.theme.VeilPrimaryContainer
import com.example.ui.theme.VeilQuantumCyan
import com.example.ui.theme.VeilSecondary

@Composable
fun CrystallineGlassBox(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(22.dp),
  backgroundColor: Color = Color.White.copy(alpha = 0.52f),
  borderColor: Color = VeilGlassBorder,
  elevation: Dp = 1.dp,
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .shadow(elevation, shape, clip = false)
      .clip(shape)
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            backgroundColor,
            backgroundColor.copy(alpha = backgroundColor.alpha * 0.75f)
          )
        )
      )
      .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
          colors = listOf(
            borderColor,
            borderColor.copy(alpha = 0.2f)
          )
        ),
        shape = shape
      )
  ) {
    content()
  }
}

@Composable
fun CrystallineIconButton(
  icon: ImageVector,
  contentDescription: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  tint: Color = VeilPrimary,
  size: Dp = 44.dp,
  testTag: String = ""
) {
  Box(
    modifier = modifier
      .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
      .testTag(testTag.ifBlank { "icon_btn_${contentDescription.lowercase().replace(" ", "_")}" })
      .clip(CircleShape)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(bounded = true, radius = 24.dp),
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .size(size)
        .shadow(0.5.dp, CircleShape)
        .clip(CircleShape)
        .background(Color.White.copy(alpha = 0.55f))
        .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = tint,
        modifier = Modifier.size((size.value * 0.48f).dp)
      )
    }
  }
}

@Composable
fun CrystallineFilterPill(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  testTag: String = ""
) {
  val animElevation by animateFloatAsState(
    targetValue = if (isSelected) 3f else 0.5f,
    animationSpec = tween(150),
    label = "pill_elevation"
  )

  Box(
    modifier = modifier
      .defaultMinSize(minHeight = 48.dp)
      .testTag(testTag.ifBlank { "filter_pill_${label.lowercase()}" })
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(bounded = true, radius = 20.dp),
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = if (isSelected) VeilPrimaryContainer else Color.White.copy(alpha = 0.5f),
      shadowElevation = animElevation.dp,
      modifier = Modifier
        .border(
          width = 1.dp,
          color = if (isSelected) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.65f),
          shape = RoundedCornerShape(20.dp)
        )
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        if (icon != null) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) VeilOnPrimary else VeilSecondary,
            modifier = Modifier
              .size(14.dp)
              .padding(end = 4.dp)
          )
        }
        Text(
          text = label,
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = 13.sp,
            letterSpacing = 0.2.sp
          ),
          color = if (isSelected) VeilOnPrimary else VeilOnSurface
        )
      }
    }
  }
}

@Composable
fun PriorityContactItem(
  contact: Contact,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .defaultMinSize(minWidth = 64.dp, minHeight = 64.dp)
      .testTag("priority_contact_${contact.id}")
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(bounded = true),
        onClick = onClick
      )
      .padding(horizontal = 4.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier.size(56.dp),
      contentAlignment = Alignment.Center
    ) {
      // Crystalline prismatic glowing halo
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(CircleShape)
          .background(
            Brush.sweepGradient(
              listOf(
                Color.White.copy(alpha = 0.9f),
                VeilQuantumCyan.copy(alpha = 0.6f),
                Color.White.copy(alpha = 0.8f),
                Color.White.copy(alpha = 0.3f),
                Color.White.copy(alpha = 0.9f)
              )
            )
          )
          .padding(2.dp)
      ) {
        // Inner avatar
        Box(
          modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(
              try {
                Color(android.graphics.Color.parseColor(contact.avatarColorHex))
              } catch (e: Exception) {
                VeilPrimaryContainer
              }
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = contact.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
          )
        }
      }

      // Vaulted lock badge or online status
      if (contact.isVaulted) {
        Box(
          modifier = Modifier
            .size(18.dp)
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
            modifier = Modifier.size(10.dp)
          )
        }
      } else if (contact.isOnline) {
        Box(
          modifier = Modifier
            .size(14.dp)
            .align(Alignment.BottomEnd)
            .clip(CircleShape)
            .background(VeilEmeraldShield)
            .border(2.dp, Color.White, CircleShape)
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = contact.name.split(" ").firstOrNull() ?: contact.name,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
      ),
      color = VeilOnSurface,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
fun CrystallineKeypadButton(
  digit: String,
  letters: String = "",
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .size(72.dp)
      .testTag("pin_key_$digit")
      .clip(CircleShape)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(bounded = true, radius = 36.dp),
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .size(68.dp)
        .shadow(1.dp, CircleShape)
        .clip(CircleShape)
        .background(Color.White.copy(alpha = 0.55f))
        .border(1.dp, Color.White.copy(alpha = 0.85f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = digit,
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp
          ),
          color = VeilPrimary,
          textAlign = TextAlign.Center
        )
        if (letters.isNotEmpty()) {
          Text(
            text = letters,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Normal,
              fontSize = 9.sp,
              letterSpacing = 1.sp
            ),
            color = VeilSecondary,
            textAlign = TextAlign.Center
          )
        }
      }
    }
  }
}
