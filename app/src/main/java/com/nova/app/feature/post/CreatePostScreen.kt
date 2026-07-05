package com.nova.app.feature.post

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.nova.app.ui.theme.*

enum class InputMode {
    None, Keyboard, Gallery, Emoji, Hashtag, Mention
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(onBack: () -> Unit) {
    var postContent by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var activeMode by remember { mutableStateOf(InputMode.Gallery) }
    var recentPhotos by remember { mutableStateOf<List<Uri>>(emptyList()) }
    
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            recentPhotos = getRecentPhotos(context)
        }
    }

    // Load photos logic
    LaunchedEffect(Unit) {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            recentPhotos = getRecentPhotos(context)
        } else {
            permissionLauncher.launch(permission)
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                selectedImages = (selectedImages + uris).distinct().take(10)
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Post", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = { /* Post action */ },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleMain),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Post", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Main scrollable content (shrinks when keyboard/panels are up)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // User Header
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.Gray))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Alex Johnson", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                }

                // Input Area
                TextField(
                    value = postContent,
                    onValueChange = { postContent = it },
                    placeholder = { Text("What's on your mind?", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .focusRequester(focusRequester)
                        .onFocusChanged { 
                            if (it.isFocused) {
                                if (activeMode != InputMode.Hashtag && activeMode != InputMode.Mention) {
                                    activeMode = InputMode.Keyboard
                                }
                            }
                        },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = PurpleMain,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )

                // Image Grid
                if (selectedImages.isNotEmpty()) {
                    ImageGrid(
                        images = selectedImages,
                        onRemove = { index ->
                            selectedImages = selectedImages.filterIndexed { i, _ -> i != index }
                        }
                    )
                }
            }

            // --- REFINED BOTTOM INTERACTION SECTION ---
            Column(modifier = Modifier.imePadding()) {
                // Suggestion Overlay (Compact height, directly above toolbar)
                if (activeMode == InputMode.Hashtag || activeMode == InputMode.Mention) {
                    SuggestionPanel(
                        height = 240.dp, // Fixed compact height
                        isHashtag = activeMode == InputMode.Hashtag,
                        onSelect = { 
                            postContent += it 
                            activeMode = InputMode.Keyboard
                            focusRequester.requestFocus()
                        }
                    )
                }

                // Toolbar
                PostToolbar(
                    activeMode = activeMode,
                    onModeChange = { mode ->
                        when (mode) {
                            InputMode.Keyboard -> {
                                focusRequester.requestFocus()
                                keyboardController?.show()
                                activeMode = InputMode.Keyboard
                            }
                            InputMode.Hashtag, InputMode.Mention -> {
                                activeMode = mode
                                focusRequester.requestFocus()
                                keyboardController?.show()
                            }
                            else -> {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                activeMode = if (activeMode == mode) InputMode.None else mode
                            }
                        }
                    },
                    onFullGalleryClick = {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                )

                // Bottom Panels
                if (activeMode == InputMode.Gallery) {
                    GalleryPanel(
                        height = screenHeight * 0.4f,
                        photos = recentPhotos,
                        onImageSelected = { uri ->
                            if (selectedImages.size < 10) {
                                selectedImages = (selectedImages + uri).distinct()
                            }
                        }
                    )
                }

                if (activeMode == InputMode.Emoji) {
                    EmojiPanel(
                        height = screenHeight * 0.4f,
                        onEmojiSelect = { postContent += it }
                    )
                }
            }
        }
    }
}

fun getRecentPhotos(context: Context): List<Uri> {
    val uris = mutableListOf<Uri>()
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
    
    context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrder
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext() && uris.size < 24) {
            val id = cursor.getLong(idColumn)
            val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            uris.add(contentUri)
        }
    }
    return uris
}

@Composable
fun GalleryPanel(
    height: androidx.compose.ui.unit.Dp, 
    photos: List<Uri>, 
    onImageSelected: (Uri) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(BgCardDark)
    ) {
        if (photos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No photos found or permission denied", color = Color.Gray, fontSize = 12.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(photos) { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray)
                            .clickable { onImageSelected(uri) },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
fun EmojiPanel(height: androidx.compose.ui.unit.Dp, onEmojiSelect: (String) -> Unit) {
    val emojis = listOf("😊", "😂", "🥰", "😍", "✨", "🙌", "🔥", "📸", "🌈", "☕", "🍕", "🎉")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(BgCardDark)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            items(emojis) { emoji ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onEmojiSelect(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 24.sp)
                }
            }
        }
    }
}

@Composable
fun SuggestionPanel(height: androidx.compose.ui.unit.Dp, isHashtag: Boolean, onSelect: (String) -> Unit) {
    val items = if (isHashtag) {
        listOf(
            Triple("#travel", "1.2M posts", "4.5M views"),
            Triple("#photography", "850K posts", "2.1M views"),
            Triple("#weekend", "500K posts", "1.8M views"),
            Triple("#vibes", "2.4M posts", "10.2M views"),
            Triple("#nature", "3.1M posts", "15.4M views")
        )
    } else {
        listOf(
            Triple("@alex_rivera", "Active now", ""),
            Triple("@seraphina", "Mutual friend", ""),
            Triple("@ken_dev", "Following", ""),
            Triple("@chloe_m", "Mutual friend", ""),
            Triple("@marcus_j", "Active 2h ago", "")
        )
    }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        color = BgCardDark,
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                if (isHashtag) "Trending Hashtags" else "Friends", 
                fontWeight = FontWeight.Bold, 
                color = PurplePink,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items) { (name, info, views) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(name) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(if (isHashtag) RoundedCornerShape(8.dp) else CircleShape)
                                .background(Color.DarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isHashtag) Text("#", color = Color.Gray)
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(info, color = Color.Gray, fontSize = 11.sp)
                                if (views.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(modifier = Modifier.size(2.dp).clip(CircleShape).background(Color.Gray))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(views, color = Color.Gray, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                }
            }
        }
    }
}

@Composable
fun PostToolbar(
    activeMode: InputMode,
    onModeChange: (InputMode) -> Unit,
    onFullGalleryClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                ToolbarIcon(Icons.Default.EmojiEmotions, activeMode == InputMode.Emoji) { onModeChange(InputMode.Emoji) }
                ToolbarIcon(Icons.Default.Image, activeMode == InputMode.Gallery) { onModeChange(InputMode.Gallery) }
                ToolbarIcon(Icons.Default.AlternateEmail, activeMode == InputMode.Mention) { onModeChange(InputMode.Mention) }
                Text(
                    "#",
                    modifier = Modifier.clickable { onModeChange(InputMode.Hashtag) }.padding(8.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (activeMode == InputMode.Hashtag) PurpleMain else Color.Gray
                )
            }
            
            if (activeMode == InputMode.Gallery) {
                TextButton(onClick = onFullGalleryClick) {
                    Text("All Photos", color = PurpleMain)
                }
            } else {
                IconButton(onClick = { onModeChange(InputMode.Keyboard) }) {
                    Icon(Icons.Default.Keyboard, contentDescription = "Keyboard", tint = if (activeMode == InputMode.Keyboard) PurpleMain else Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ToolbarIcon(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(icon, contentDescription = null, tint = if (isSelected) PurpleMain else Color.Gray)
    }
}

@Composable
fun ImageGrid(images: List<Uri>, onRemove: (Int) -> Unit) {
    val count = images.size
    Box(modifier = Modifier.padding(16.dp)) {
        when (count) {
            1 -> ImageItem(images[0], modifier = Modifier.fillMaxWidth().aspectRatio(1.5f), onRemove = { onRemove(0) })
            2 -> Row(Modifier.fillMaxWidth().height(200.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ImageItem(images[0], modifier = Modifier.weight(1f).fillMaxHeight(), onRemove = { onRemove(0) })
                ImageItem(images[1], modifier = Modifier.weight(1f).fillMaxHeight(), onRemove = { onRemove(1) })
            }
            3 -> Column(Modifier.fillMaxWidth().height(300.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ImageItem(images[0], modifier = Modifier.fillMaxWidth().weight(1.5f), onRemove = { onRemove(0) })
                Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ImageItem(images[1], modifier = Modifier.weight(1f).fillMaxHeight(), onRemove = { onRemove(1) })
                    ImageItem(images[2], modifier = Modifier.weight(1f).fillMaxHeight(), onRemove = { onRemove(2) })
                }
            }
            else -> {
                Column(Modifier.fillMaxWidth().height(300.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ImageItem(images[0], modifier = Modifier.weight(1f).fillMaxHeight(), onRemove = { onRemove(0) })
                        ImageItem(images[1], modifier = Modifier.weight(1f).fillMaxHeight(), onRemove = { onRemove(1) })
                    }
                    Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ImageItem(images[2], modifier = Modifier.weight(1f).fillMaxHeight(), onRemove = { onRemove(2) })
                        Box(Modifier.weight(1f).fillMaxHeight()) {
                            ImageItem(images[3], modifier = Modifier.fillMaxSize(), onRemove = { onRemove(3) })
                            if (count > 4) {
                                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)), contentAlignment = Alignment.Center) {
                                    Text("+${count - 3}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageItem(uri: Uri, modifier: Modifier, onRemove: () -> Unit) {
    Box(modifier = modifier.clip(RoundedCornerShape(8.dp))) {
        AsyncImage(model = uri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        IconButton(
            onClick = onRemove,
            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(24.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

@Preview
@Composable
fun SuggestionPanelPreview() {
    NOVATheme(darkTheme = true) {
        SuggestionPanel(height = 400.dp, isHashtag = true, onSelect = {})
    }
}

@Preview
@Composable
fun CreatePostScreenPreview() {
    NOVATheme(darkTheme = true) {
        CreatePostScreen(onBack = {})
    }
}
