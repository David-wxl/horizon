package com.horizon.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

/**
 * 注册请求DTO
 */
@Data
public class RegisterDTO {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度为3-20个字符")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20个字符")
    private String password;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    private String nickname;
}
