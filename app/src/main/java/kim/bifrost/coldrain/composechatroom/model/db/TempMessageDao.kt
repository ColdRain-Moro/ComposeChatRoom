package kim.bifrost.coldrain.composechatroom.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kim.bifrost.coldrain.composechatroom.model.bean.ChatMessageBean
import kotlinx.coroutines.flow.Flow

/**
 * kim.bifrost.coldrain.composechatroom.model.db.TempMessageDao
 * ComposeChatRoom
 *
 * @author 寒雨
 * @since 2021/12/27 0:56
 **/
@Dao
interface TempMessageDao {

    /**
     * 查询所有的节气数据
     */
    @Query("SELECT * FROM message")
    fun queryAll(): Flow<List<ChatMessageBean>>

    /**
     * 插入一条数据
     */
    @Insert()
    fun insert(entity: ChatMessageBean)

    /**
     * 清空term表中所有内容
     */
    @Query("DELETE FROM message")
    fun delete()
}