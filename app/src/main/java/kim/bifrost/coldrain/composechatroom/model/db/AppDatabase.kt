package kim.bifrost.coldrain.composechatroom.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kim.bifrost.coldrain.composechatroom.model.bean.ChatMessageBean

/**
 * kim.bifrost.coldrain.composechatroom.model.db.AppDatabase
 * ComposeChatRoom
 *
 * @author 寒雨
 * @since 2021/12/27 0:58
 **/
@Database(entities = [ChatMessageBean::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getSolarTermDao(): TempMessageDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "db_temp_message")
                .build()
    }
}