package com.example.demo.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Data
public class MyPicConfig implements WebMvcConfigurer {

	@Value("${file.filePath}")
	private String filePath;

	@Value("${file.domain}")
	String domain;

	@Value("${file.imagePath}")
	String imagePath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		/*System.out.println("配置文件已经生效");*/
		registry.addResourceHandler("/static/images/**").addResourceLocations("file:" + filePath);
	}
}
