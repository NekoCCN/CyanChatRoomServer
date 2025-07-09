package cc.nekocc.cyanchatroomserver.application.impl;

import cc.nekocc.cyanchatroomserver.application.service.ChatApplicationService;
import cc.nekocc.cyanchatroomserver.domain.repository.GroupMemberRepository;
import cc.nekocc.cyanchatroomserver.domain.repository.OfflineMessageRepository;
import cc.nekocc.cyanchatroomserver.domain.service.ChatDomainService;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository.GroupMemberRepositoryImpl;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository.OfflineMessageRepositoryImpl;
import cc.nekocc.cyanchatroomserver.infrastructure.session.SessionManager;

import java.util.UUID;

public class ChatApplicationServiceImpl implements ChatApplicationService
{
    private final ChatDomainService chat_domain_service_;

    public ChatApplicationServiceImpl()
    {
        // 在构造函数中注入所有需要的依赖
        this.chat_domain_service_ = new ChatDomainService(
                new OfflineMessageRepositoryImpl(),
                new GroupMemberRepositoryImpl(),
                SessionManager.getInstance()
        );
    }

    @Override
    public void sendMessage(UUID sender_id, String recipient_type, UUID recipient_id, String content_type, boolean is_encrypted, String content)
    {
        if ("USER".equals(recipient_type))
        {
            chat_domain_service_.forwardPrivateMessage(sender_id, recipient_id, content_type, is_encrypted, content);
        } else if ("GROUP".equals(recipient_type))
        {
            chat_domain_service_.forwardGroupMessage(sender_id, recipient_id, content_type, is_encrypted, content);
        } else
        {
            System.err.println("未知的接收方类型: " + recipient_type);
        }
    }
}