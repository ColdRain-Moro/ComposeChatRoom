package kim.bifrost.coldrain.composechatroom

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kim.bifrost.coldrain.composechatroom.model.bean.ChatMessageBean
import kim.bifrost.coldrain.composechatroom.model.db.AppDatabase
import kim.bifrost.coldrain.composechatroom.utils.gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URI
import java.net.URLEncoder

/**
 * kim.bifrost.coldrain.composechatroom.ChatViewModel
 * ComposeChatRoom
 *
 * @author 寒雨
 * @since 2021/12/27 23:56
 **/
class ChatViewModel(context: Context) : ViewModel() {

    private val database = AppDatabase.create(context)

    val tempMessageFlow by lazy { database.getSolarTermDao().queryAll() }

    private lateinit var webSocketClient: WebSocketClient

    fun cleanCache() {
        database.getSolarTermDao().delete()
    }

    fun connect(name: String?, avatar: String?) {
        val client = object : WebSocketClient(URI.create(url + buildQueryString(name, avatar))) {

            override fun onOpen(handshakedata: ServerHandshake?) {

            }

            override fun onMessage(text: String?) {
                viewModelScope.launch(Dispatchers.IO) {
                    database.getSolarTermDao().insert(gson.fromJson(text, ChatMessageBean::class.java))
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {

            }

            override fun onError(ex: Exception?) {

            }
        }
        if (!::webSocketClient.isInitialized) {
            webSocketClient = client
        }
        if (!webSocketClient.isOpen) webSocketClient.connect()
    }

    fun send(text: String) {
        webSocketClient.send(text)
    }

    private fun buildQueryString(name: String?, avatar: String?): String {
        if (name == null && avatar == null) return ""
        val builder = StringBuilder("?")
        name?.let {
            builder.append("username=${URLEncoder.encode(it, "utf-8")}")
            avatar?.let {
                builder.append("&")
            }
        }
        avatar?.let {
            builder.append("avatar=$it")
        }
        return builder.toString()
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory  {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChatViewModel(context) as T
        }
    }

    companion object {
        const val url = "ws://42.192.196.215:8080/chatroom"
    }
}