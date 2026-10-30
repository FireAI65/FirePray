package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlaybackState
import com.example.data.model.DoaItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DoaCard(
    doa: DoaItem,
    playbackState: AudioPlaybackState,
    onPlayAudio: (DoaItem) -> Unit,
    onStopAudio: () -> Unit,
    onToggleBookmark: (DoaItem) -> Unit,
    onOpenTasbih: (DoaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isCurrentAudioPlaying = playbackState.isPlaying && playbackState.currentDoaId == doa.id

    val cardBorderColor by animateColorAsState(
        targetValue = if (isCurrentAudioPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        label = "card_border"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("doa_card_${doa.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentAudioPlaying) 6.dp else 2.dp
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isCurrentAudioPlaying) 2.dp else 1.dp,
            color = cardBorderColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Category, Source & Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = doa.category,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = doa.source,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { onToggleBookmark(doa) },
                    modifier = Modifier.testTag("bookmark_btn_${doa.id}")
                ) {
                    Icon(
                        imageVector = if (doa.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (doa.isBookmarked) "Hapus Bookmark" else "Simpan Bookmark",
                        tint = if (doa.isBookmarked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Prayer Title
            Text(
                text = doa.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Arabic Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = doa.arabicText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 44.sp,
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            textDirection = TextDirection.Rtl
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Latin Transliteration
            Text(
                text = doa.latinText,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.tertiary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Translation (Arti)
            Text(
                text = doa.translation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 21.sp
            )

            // Benefit / Keutamaan (Callout)
            if (doa.benefit.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡 ${doa.benefit}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Audio Active Status Banner
            AnimatedVisibility(visible = isCurrentAudioPlaying) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = playbackState.currentPartText.ifEmpty { "Sedang Memutar Audio..." },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Text(
                                text = playbackState.playMode.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Toolbar (Audio, Tasbih, Copy, Share)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Audio Voice Button
                FilledTonalButton(
                    onClick = {
                        if (isCurrentAudioPlaying) {
                            onStopAudio()
                        } else {
                            onPlayAudio(doa)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("audio_play_btn_${doa.id}")
                ) {
                    Icon(
                        imageVector = if (isCurrentAudioPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isCurrentAudioPlaying) "Hentikan Suara" else "Putar Suara Doa",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCurrentAudioPlaying) "Stop Suara" else "Dengarkan",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                // Digital Tasbih Count Button
                OutlinedButton(
                    onClick = { onOpenTasbih(doa) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("tasbih_btn_${doa.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = "Hitung Dzikir",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Tasbih",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                // Copy Button
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(
                            "Doa",
                            "${doa.title}\n\n${doa.arabicText}\n\n${doa.latinText}\n\nArtinya:\n${doa.translation}\n(${doa.source})"
                        )
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Doa berhasil disalin ke clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("copy_btn_${doa.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Salin Teks Doa",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Share Button
                IconButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "${doa.title}\n\n${doa.arabicText}\n\n${doa.latinText}\n\nArtinya:\n\"${doa.translation}\"\n\nSumber: ${doa.source}\nKeutamaan: ${doa.benefit}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Bagikan Doa"))
                    },
                    modifier = Modifier.testTag("share_btn_${doa.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Bagikan Doa",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
