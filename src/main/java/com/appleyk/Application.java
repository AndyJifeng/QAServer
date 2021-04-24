package com.appleyk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;

@SpringBootApplication // same as @Configuration @EnableAutoConfiguration// @ComponentScan
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) throws IOException {
		makeFile();
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void makeFile() throws IOException {
		File file = new File("./data/entityAndRelationData/");
		if(!file.exists())
			file.mkdirs();
		file = new File("./data/loadTempData/");
		if(!file.exists())
			file.mkdirs();
		file = new File("./data/template/");
		if(!file.exists())
			file.mkdirs();
		file = new File("./data/dictionary/");

		if(!file.exists())
			file.mkdirs();
		file = new File("./data/entityAndRelationData/property.txt");
		if(!file.exists())
			file.createNewFile();
		file = new File("./data/entityAndRelationData/entity.txt");
		if(!file.exists())
			file.createNewFile();
		file = new File("./data/entityAndRelationData/relation.txt");
		if(!file.exists())
			file.createNewFile();
		file = new File("./data/entityAndRelationData/word.txt");

		if(!file.exists())
			file.createNewFile();
		file = new File("./data/template/ThreeEntity.txt");
		if(!file.exists())
			file.createNewFile();
		file = new File("./data/template/TwoEntity.txt");
		if(!file.exists())
			file.createNewFile();
		file = new File("./data/template/OneEntity.txt");
		if(!file.exists())
			file.createNewFile();
	}
}

@Configuration
class MyMvcConfig {

	@Bean
	public WebMvcConfigurer webMvcConfigurer1() {
		return new WebMvcConfigurer() {

			//配置跨域
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/*/*")     //允许的路径
						.allowedMethods("*")     //允许的方法
						.allowedOrigins("*")       //允许的网站
						.allowedHeaders("*")     //允许的请求头
						.allowCredentials(true)
						.maxAge(3600);
			}
		};

	}

	@Bean
	public WebMvcConfigurer webMvcConfigurer2() {
		return new WebMvcConfigurer() {

			//配置跨域
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/*")     //允许的路径
						.allowedMethods("*")     //允许的方法
						.allowedOrigins("*")       //允许的网站
						.allowedHeaders("*")     //允许的请求头
						.allowCredentials(true)
						.maxAge(3600);
			}
		};

	}
}
