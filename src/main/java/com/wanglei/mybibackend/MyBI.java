package com.wanglei.mybibackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.wanglei.mybibackend.mapper")//扫描mapper
@EnableScheduling
public class MyBI {

	public static void main(String[] args) {

		SpringApplication.run(MyBI.class, args);
	}

}
