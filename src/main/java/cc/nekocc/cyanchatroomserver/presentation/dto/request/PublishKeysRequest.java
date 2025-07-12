package cc.nekocc.cyanchatroomserver.presentation.dto.request;

import com.google.gson.JsonObject;
import java.util.UUID;

public record PublishKeysRequest(UUID client_request_id, JsonObject key_bundle)
{  }
