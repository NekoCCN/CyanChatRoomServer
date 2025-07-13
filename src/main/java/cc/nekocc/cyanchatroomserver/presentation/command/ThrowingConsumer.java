package cc.nekocc.cyanchatroomserver.presentation.command;

@FunctionalInterface
public interface ThrowingConsumer<T>
{
    void accept(T t) throws Exception;
}