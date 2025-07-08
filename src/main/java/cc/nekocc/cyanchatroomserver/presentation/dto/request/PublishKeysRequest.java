package cc.nekocc.cyanchatroomserver.presentation.dto.request;

import com.google.gson.JsonObject;

public record PublishKeysRequest(JsonObject key_bundle)
{  }
