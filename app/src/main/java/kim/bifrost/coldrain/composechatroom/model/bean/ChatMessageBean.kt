package kim.bifrost.coldrain.composechatroom.model.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import java.util.*

/**
 * kim.bifrost.coldrain.composechatroom.model.bean.ChatMessageBean
 * ComposeChatRoom
 *
 * @author 寒雨
 * @since 2021/12/27 0:09
 **/
@Entity(tableName = "message")
data class ChatMessageBean(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int? = null,
    @Expose
    @ColumnInfo(name = "type", typeAffinity = ColumnInfo.TEXT)
    val type: String,
    @Expose
    @ColumnInfo(name = "username", typeAffinity = ColumnInfo.TEXT)
    val username: String,
    @Expose
    @ColumnInfo(name = "data", typeAffinity = ColumnInfo.TEXT, defaultValue = "null")
    val data: String?,
    @Expose
    @ColumnInfo(name = "avatar", typeAffinity = ColumnInfo.TEXT)
    val avatar: String,
    @Expose
    @ColumnInfo(name = "date", typeAffinity = ColumnInfo.INTEGER)
    val date: Long
) {
    fun time(): String {
        return Date(date).toLocaleString()
    }
}