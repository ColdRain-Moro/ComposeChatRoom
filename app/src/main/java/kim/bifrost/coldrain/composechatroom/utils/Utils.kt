package kim.bifrost.coldrain.composechatroom.utils

import com.google.gson.GsonBuilder

/**
 * kim.bifrost.coldrain.composechatroom.utils.Utils
 * ComposeChatRoom
 *
 * @author 寒雨
 * @since 2021/12/27 0:53
 **/
val gson = GsonBuilder()
    .excludeFieldsWithoutExposeAnnotation()
    .create()