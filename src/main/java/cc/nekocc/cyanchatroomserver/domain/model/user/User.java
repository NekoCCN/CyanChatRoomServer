package cc.nekocc.cyanchatroomserver.domain.model.user;

import com.github.f4b6a3.uuid.UuidCreator;
import org.mindrot.jbcrypt.BCrypt;

import java.time.OffsetDateTime;
import java.util.UUID;

public class User
{
    private UUID id_;
    private String username_;
    private String password_hash_;
    private UserStatus status_ = UserStatus.ONLINE;
    private String nickname_;
    private String avatar_url_;
    private String signature_;
    private String public_key_bundle_;
    private OffsetDateTime created_at_;
    
    public User(String username, String password_hash, String nickname)
    {
        id_ = UuidCreator.getTimeOrderedEpoch();
        username_ = username;
        password_hash_ = password_hash;
        nickname_ = nickname;
        status_ = UserStatus.ONLINE;
        created_at_ = OffsetDateTime.now();
    }
    
    public User()
    {  }

    /**
     * 核心业务逻辑: 验证密码
     *
     * @param plain_password 明文密码
     * @return 是否匹配
     */
    public boolean checkPassword(String plain_password)
    {
        return BCrypt.checkpw(plain_password, password_hash_);
    }

    /**
     * 核心业务逻辑: 更新个人资料
     */
    public void updateProfile(String new_nickname, String new_signature, String new_avatar_url)
    {
        if (new_nickname != null && !new_nickname.isBlank())
        {
            nickname_ = new_nickname;
        }
        if (new_signature != null)
        {
            signature_ = new_signature;
        }
        if (new_avatar_url != null && !new_avatar_url.isBlank())
        {
            avatar_url_ = new_avatar_url;
        }
    }

    public void changeUsername(String new_username)
    {
        if (new_username == null || new_username.isBlank())
        {
            throw new IllegalArgumentException("用户名不能为空。");
        }
        username_ = new_username;
    }

    public UUID getId()
    {
        return id_;
    }

    public void setId(UUID id)
    {
        id_ = id;
    }

    public String getUsername()
    {
        return username_;
    }

    public void setUsername(String username)
    {
        username_ = username;
    }

    public String getPasswordHash()
    {
        return password_hash_;
    }

    public void setPasswordHash(String password_hash)
    {
        password_hash_ = password_hash;
    }

    public UserStatus getStatus()
    {
        return status_;
    }

    public void setStatus(UserStatus status)
    {
        status_ = status;
    }

    public String getNickname()
    {
        return nickname_;
    }

    public void setNickname(String nickname)
    {
        nickname_ = nickname;
    }

    public String getAvatarUrl()
    {
        return avatar_url_;
    }

    public void setAvatarUrl(String avatar_url)
    {
        avatar_url_ = avatar_url;
    }

    public String getSignature()
    {
        return signature_;
    }

    public void setSignature(String signature)
    {
        signature_ = signature;
    }


    public String getPublicKeyBundle()
    {
        return public_key_bundle_;
    }

    public void setPublicKeyBundle(String public_key_bundle)
    {
        public_key_bundle_ = public_key_bundle;
    }

    public OffsetDateTime getCreatedAt()
    {
        return created_at_;
    }

    public void setCreatedAt(OffsetDateTime created_at)
    {
        created_at_ = created_at;
    }
}