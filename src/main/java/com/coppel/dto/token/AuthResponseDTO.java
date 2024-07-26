package com.coppel.dto.token;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResponseDTO {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private int expiresIn;

    private String scope;

    @JsonProperty("userOrgs")
    private List<String> userOrgs;

    private int edge;

    private String organization;

    @JsonProperty("userLocationDTOS")
    private List<UserLocationDTO> userLocationDTOS;

    @JsonProperty("accesstoAllBUs")
    private boolean accessToAllBUs;

    @JsonProperty("tenantId")
    private String tenantId;

    private String locale;

    @JsonProperty("excludedUserBusinessUnits")
    private List<String> excludedUserBusinessUnits;

    @JsonProperty("userDefaultDTOS")
    private List<UserDefaultDTO> userDefaultDTOS;

    @JsonProperty("userBusinessUnits")
    private List<String> userBusinessUnits;

    @JsonProperty("userTimeZone")
    private String userTimeZone;

    private String jti;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static class UserLocationDTO {

        @JsonProperty("locationId")
        private String locationId;

        @JsonProperty("locationType")
        private String locationType;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static class UserDefaultDTO {

        @JsonProperty("defaultLocation")
        private String defaultLocation;

        @JsonProperty("defaultOrganization")
        private String defaultOrganization;

        @JsonProperty("defaultBusinessUnit")
        private String defaultBusinessUnit;

    }
}