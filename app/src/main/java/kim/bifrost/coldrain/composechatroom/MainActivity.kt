package kim.bifrost.coldrain.composechatroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberImagePainter
import kim.bifrost.coldrain.composechatroom.model.bean.ChatMessageBean
import kim.bifrost.coldrain.composechatroom.ui.theme.ComposeChatRoomTheme
import kim.bifrost.coldrain.composechatroom.widgets.CommonOutlinedTextField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeChatRoomTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Main()
                }
            }
        }
    }
}


@Composable
fun Main() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "start") {
        composable("start") { Start(navController) }
        composable(
            "chat/{userId}?avatar={avatar}",
            arguments = listOf(navArgument("avatar") { defaultValue = "null" })
        ) { Chat(navController, it.arguments!!.getString("userId")!!.run { if (this == "null") null else this }, it.arguments!!.getString("avatar")!!.run { if (this == "null") null else this }) }
    }
}

@Composable
fun Start(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var userId by remember { mutableStateOf("") }
        var avatar by remember { mutableStateOf("") }
        Text(
            text = "ComposeChatRoom",
            modifier = Modifier.padding(top = 150.dp),
            style = MaterialTheme.typography.h4
        )
        CommonOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 150.dp, start = 40.dp, end = 40.dp),
            value = userId,
            onValueChange = { value ->
                val realValue = value.trimStart().trimEnd()
                if (realValue.length <= 12) {
                    userId = realValue
                }
            },
            label = "UserId",
            maxLines = 1,
            singleLine = true
        )
        CommonOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 40.dp, end = 40.dp),
            value = avatar,
            onValueChange = { value ->
                avatar = value
            },
            label = "Avatar (optional)",
            maxLines = 1,
            singleLine = true
        )
        Button(
            onClick = {
                navController.navigate("chat/${if(userId.isEmpty()) "null" else userId}${if(avatar.isEmpty()) "" else "?avatar=$avatar"}")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, end = 40.dp, top = 30.dp)
        ) {
            Text(text = "??????")
        }
    }
}

@Composable
fun Chat(navController: NavController, userId: String?, avatar: String?) {
    val viewModel = viewModel<ChatViewModel>(
        factory = ChatViewModel.Factory(LocalContext.current)
    )
    var dialogState by remember { mutableStateOf(false) }
    var inputMsg by remember { mutableStateOf("") }
    // ????????????webSocket????????????
    viewModel.connect(userId, avatar)
    // ???????????????
    val chatMessages by viewModel.tempMessageFlow.collectAsState(listOf())
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text("ChatRoom") },
            navigationIcon = {
                IconButton(onClick = {
                    dialogState = true
                }) {
                    Icon(Icons.Filled.Info, null)
                }
            }
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.83f),
            state = scrollState,
            reverseLayout = true
        ) {
            var scroll = false
            itemsIndexed(chatMessages.asReversed()) { index, item ->
                ChatItem(bean = item)
                if (!scroll) {
                    scroll = true
                    LaunchedEffect(scrollState) {
                        scrollState.animateScrollToItem(0)
                    }
                }
            }
        }
        // ?????????
        Column(
            modifier = Modifier
                .background(Color(0xB2E0E0E0))
                .wrapContentHeight()
        ) {
            CommonOutlinedTextField(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .background(Color.White),
                value = inputMsg,
                label = "",
                onValueChange = { value ->
                    inputMsg = value
                }
            )
            Button(
                onClick = {
                    if (inputMsg.isNotEmpty()) {
                        viewModel.send(inputMsg)
                        inputMsg = ""
                    }
                },
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(60.dp)
                    .align(Alignment.CenterHorizontally),
            ) {
                Text(
                    text = "Send",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
    if (dialogState) {
        AlertDialog(
            onDismissRequest = { dialogState = false },
            title = {
                Text(text = "?????????????????????")
            },
            text = {
                Text(text = "??????????????????????????????????????????")
            },
            confirmButton = {
                Button(
                    onClick = {
                        dialogState = false
                        scope.launch(Dispatchers.IO) {
                            viewModel.cleanCache()
                            withContext(Dispatchers.Main) {
                                scaffoldState.snackbarHostState.showSnackbar("????????????!")
                            }
                        }
                    }
                ) {
                    Text(text = "??????!")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        dialogState = false
                    }
                ) {
                    Text(text = "??????")
                }
            }
        )
    }
}

@Preview
@Composable
fun ChatItem(
    bean: ChatMessageBean = ChatMessageBean(
        type = "OPEN",
        username = "Rain",
        avatar = "https://gitee.com/coldrain-moro/images_bed/raw/master/images/chino.jpg",
        data = "??????????????????????????????????????????????????????????????????????????????????????????",
        date = System.currentTimeMillis()
    ),
) {
    when (bean.type) {
        "MESSAGE" -> {
            MessageChatItem(bean = bean)
        }
        "OPEN" -> {
            MessageOpenItem(bean = bean)
        }
        "CLOSE" -> {
            MessageCloseItem(bean = bean)
        }
    }
}

@Composable
private fun MessageChatItem(
    bean: ChatMessageBean,
) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { },
        elevation = 10.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 10.dp),
            ) {
                Surface(
                    shape = CircleShape,
                ) {
                    Image(
                        painter = rememberImagePainter(data = bean.avatar),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    Modifier
                        .fillMaxHeight()
                        .wrapContentWidth()
                        .padding(start = 10.dp)
                ) {
                    Text(
                        text = bean.username,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500
                        )
                    )
                    Text(
                        text = bean.time(),
                        style = TextStyle(
                            fontSize = 11.sp,
                        ),
                        modifier = Modifier.padding(top = 1.dp),
                        color = Color.Gray
                    )
                }
            }
            Divider(
                color = Color.Gray,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .defaultMinSize(minHeight = 50.dp)
                    .padding(10.dp)
            ) {
                Text(bean.data!!)
            }
        }
    }
}

@Composable
private fun MessageOpenItem(
    bean: ChatMessageBean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .defaultMinSize(minHeight = 20.dp),
        ) {
            Text(
                text = "${bean.username} ??????????????????",
                style = TextStyle(
                    color = Color.Gray
                ),
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

@Composable
private fun MessageCloseItem(
    bean: ChatMessageBean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .defaultMinSize(minHeight = 20.dp),
        ) {
            Text(
                text = "${bean.username} ??????????????????",
                style = TextStyle(
                    color = Color.Gray
                ),
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}
