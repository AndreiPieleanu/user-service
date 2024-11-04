package s6.userservice.servicelayer.token;

import s6.userservice.configuration.AccessToken;

public interface IAccessTokenEncoder {
    String encode(AccessToken accessToken);
}
