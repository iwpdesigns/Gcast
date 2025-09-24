package com.gcast.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gcast.R
import com.gcast.ui.components.CastButton
import com.gcast.data.MediaItem
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.media.MediaLoadRequestData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Videos", "Photos", "Music")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App Bar with Cast Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "GCast",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            CastButton()
        }
        
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Content based on selected tab
        when (selectedTab) {
            0 -> VideoContent()
            1 -> PhotoContent()
            2 -> MusicContent()
        }
    }
}

@Composable
fun VideoContent() {
    val sampleVideos = remember {
        listOf(
            MediaItem("Big Buck Bunny", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", MediaItem.Type.VIDEO),
            MediaItem("Elephant Dream", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4", MediaItem.Type.VIDEO),
            MediaItem("Sintel", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4", MediaItem.Type.VIDEO)
        )
    }
    
    MediaGrid(items = sampleVideos, itemType = "Videos")
}

@Composable
fun PhotoContent() {
    val samplePhotos = remember {
        listOf(
            MediaItem("Sample Photo 1", "https://picsum.photos/800/600?random=1", MediaItem.Type.IMAGE),
            MediaItem("Sample Photo 2", "https://picsum.photos/800/600?random=2", MediaItem.Type.IMAGE),
            MediaItem("Sample Photo 3", "https://picsum.photos/800/600?random=3", MediaItem.Type.IMAGE)
        )
    }
    
    MediaGrid(items = samplePhotos, itemType = "Photos")
}

@Composable
fun MusicContent() {
    val sampleMusic = remember {
        listOf(
            MediaItem("Sample Song 1", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", MediaItem.Type.AUDIO),
            MediaItem("Sample Song 2", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", MediaItem.Type.AUDIO),
            MediaItem("Sample Song 3", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3", MediaItem.Type.AUDIO)
        )
    }
    
    MediaGrid(items = sampleMusic, itemType = "Music")
}

@Composable
fun MediaGrid(
    items: List<MediaItem>,
    itemType: String
) {
    if (items.isEmpty()) {
        EmptyState(itemType = itemType)
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                MediaItemCard(
                    mediaItem = item,
                    onClick = { 
                        // TODO: Implement cast functionality
                    }
                )
            }
        }
    }
}

@Composable
fun MediaItemCard(
    mediaItem: MediaItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Media type icon
            Icon(
                imageVector = when (mediaItem.type) {
                    MediaItem.Type.VIDEO -> Icons.Default.PlayArrow
                    MediaItem.Type.IMAGE -> Icons.Default.Image
                    MediaItem.Type.AUDIO -> Icons.Default.MusicNote
                },
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            // Media info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = mediaItem.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = when (mediaItem.type) {
                        MediaItem.Type.VIDEO -> "Video file"
                        MediaItem.Type.IMAGE -> "Image file" 
                        MediaItem.Type.AUDIO -> "Audio file"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Cast icon (loads if session active, otherwise shows chooser)
            val _localContext = LocalContext.current
            IconButton(onClick = {
                val ctx = _localContext
                try {
                    val result = com.google.android.gms.cast.framework.CastContext.getSharedInstance(ctx)
                    val castCtx = if (result is com.google.android.gms.cast.framework.CastContext) result else null
                    val session = castCtx?.sessionManager?.currentCastSession

                    if (session != null && session.isConnected) {
                        try {
                            val remote = session.remoteMediaClient
                            val metadata = com.google.android.gms.cast.MediaMetadata(
                                when (mediaItem.type) {
                                    MediaItem.Type.VIDEO -> com.google.android.gms.cast.MediaMetadata.MEDIA_TYPE_MOVIE
                                    MediaItem.Type.IMAGE -> com.google.android.gms.cast.MediaMetadata.MEDIA_TYPE_PHOTO
                                    MediaItem.Type.AUDIO -> com.google.android.gms.cast.MediaMetadata.MEDIA_TYPE_MUSIC_TRACK
                                }
                            )
                            metadata.putString(com.google.android.gms.cast.MediaMetadata.KEY_TITLE, mediaItem.title)
                            val mediaInfo = com.google.android.gms.cast.MediaInfo.Builder(mediaItem.url)
                                .setStreamType(com.google.android.gms.cast.MediaInfo.STREAM_TYPE_BUFFERED)
                                .setContentType(
                                    when (mediaItem.type) {
                                        MediaItem.Type.VIDEO -> "video/mp4"
                                        MediaItem.Type.AUDIO -> "audio/mpeg"
                                        MediaItem.Type.IMAGE -> "image/jpeg"
                                    }
                                )
                                .setMetadata(metadata)
                                .build()

                            // Prefer new MediaLoadRequestData-based API; fallback to deprecated load(mediaInfo, autoplay)
                            try {
                                val loadRequest = MediaLoadRequestData.Builder()
                                    .setMediaInfo(mediaInfo)
                                    .setAutoplay(true)
                                    .build()
                                remote?.load(loadRequest)
                            } catch (e: Exception) {
                                try {
                                    remote?.load(mediaInfo, true)
                                } catch (inner: Exception) {
                                    inner.printStackTrace()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        // show chooser dialog
                        try {
                            val fragment = androidx.mediarouter.app.MediaRouteChooserDialogFragment()
                            val selector = androidx.mediarouter.media.MediaRouteSelector.Builder()
                                .addControlCategory(com.google.android.gms.cast.CastMediaControlIntent
                                    .categoryForCast(com.google.android.gms.cast.CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID))
                                .build()
                            fragment.routeSelector = selector
                            val activity = ctx as? androidx.fragment.app.FragmentActivity
                            activity?.let {
                                fragment.show(it.supportFragmentManager, "media_route_chooser")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Cast,
                    contentDescription = "Cast this media",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun EmptyState(itemType: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = when (itemType) {
                "Videos" -> Icons.Default.VideoLibrary
                "Photos" -> Icons.Default.PhotoLibrary
                "Music" -> Icons.Default.LibraryMusic
                else -> Icons.Default.Folder
            },
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No $itemType Found",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Add some $itemType to get started with casting",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}