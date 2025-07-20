package com.career.portal.dto;

import lombok.Data;

@Data
public class PasswordReset {

    private String token;
    private String password;

}
