package s6.userservice.servicelayer;


import s6.userservice.configuration.AccessToken;

public interface IAccessTokenDecoder {
    AccessToken decode(String accessTokenEncoded);
}
