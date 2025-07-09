package cc.nekocc.cyanchatroomserver.application.service;

import java.util.Optional;
import java.util.UUID;

public interface KeyManagementService
{
    /**
     * 发布用户的公钥包
     */
    void publishKeys(UUID user_id, String public_key_bundle_json);

    /**
     * 获取另一个用户的公钥包
     */
    Optional<String> fetchKeys(UUID user_id);
}
