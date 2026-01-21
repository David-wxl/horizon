package com.horizon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * HORIZON 地平线 - 主启动类
 * 
 * @author Horizon Team
 * @since 2024-01-21
 */
@SpringBootApplication
@MapperScan("com.horizon.mapper")
public class HorizonApplication {

    public static void main(String[] args) {
        SpringApplication.run(HorizonApplication.class, args);
        System.out.println("""
            
            ╔═══════════════════════════════════════╗
            ║   HORIZON Backend Started Successfully   ║
            ║   Port: 8080                          ║
            ║   Context: /api                       ║
            ╚═══════════════════════════════════════╝
            """);
    }
}
