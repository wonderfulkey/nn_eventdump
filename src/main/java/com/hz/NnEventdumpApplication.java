package com.hz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hz.mapper")
public class NnEventdumpApplication {

	public static void main(String[] args) {
		SpringApplication.run(NnEventdumpApplication.class, args);
	}

}
