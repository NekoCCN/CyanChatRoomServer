package cc.nekocc.cyanchatroomserver.presentation.dto.request;

/*
Example JSON for REGISTER_REQUEST:
{
    "type": "LOGIN_REQUEST",
    "payload":
    {
        "username": "Neko",
        "password": "143150151"
    }
}
 */
public record LoginRequest(String username, String password)
{  }
