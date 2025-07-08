package cc.nekocc.cyanchatroomserver.application.service;

import java.util.Optional;

public interface KeyManagementService
{
    void publishKeys(Integer user_id, String public_key_bundle_json);
    Optional<String> fetchKeys(Integer user_id);
}