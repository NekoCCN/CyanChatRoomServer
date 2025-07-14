package cc.nekocc.cyanchatroomserver.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;

/**
 * 使用 Gson 实现的JSON序列化/反序列化工具类
 */
public final class JsonUtil
{
    final static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .create();

    private JsonUtil()
    {  }

    /**
     * 将任何Java对象序列化为JSON字符串。
     */
    public static String serialize(Object obj)
    {
        return GSON.toJson(obj);
    }

    /**
     * 将JSON字符串反序列化为简单的、非泛型的Java对象。
     */
    public static <T> T deserialize(String json, Class<T> cl) throws JsonSyntaxException
    {
        return GSON.fromJson(json, cl);
    }

    /**
     * 反序列化我们自定义的泛型 ProtocolMessage<T>。
     */
    public static <T> ProtocolMessage<T> deserializeProtocolMessage(String json, Class<T> payload_type)
    {
        // 我们都知道 Type 相比 Class 代表一个完整的类型 包括泛型
        Type protocol_type = TypeToken.getParameterized(ProtocolMessage.class, payload_type).getType();
        return GSON.fromJson(json, protocol_type);
    }
}
