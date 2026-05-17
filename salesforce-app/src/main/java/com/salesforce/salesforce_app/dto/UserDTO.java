package com.salesforce.salesforce_app.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String instanceUrl;
    private String sfUserId;
}
