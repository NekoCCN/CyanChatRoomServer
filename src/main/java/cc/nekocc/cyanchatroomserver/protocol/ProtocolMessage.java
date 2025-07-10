package cc.nekocc.cyanchatroomserver.protocol;

/**
 * 统一的协议消息体 (信封)
 * @param <T> 泛型载荷, 携带具体业务数据
 */
public class ProtocolMessage<T>
{
    private String type;
    private T payload;

    public ProtocolMessage()
    {  }

    public ProtocolMessage(String type, T payload)
    {
        this.type = type;
        this.payload = payload;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public T getPayload()
    {
        return payload;
    }

    public void setPayload(T payload)
    {
        this.payload = payload;
    }
}
