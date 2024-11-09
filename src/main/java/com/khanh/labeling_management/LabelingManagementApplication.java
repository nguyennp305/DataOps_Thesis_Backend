package com.khanh.labeling_management;

import com.khanh.labeling_management.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

//@SpringBootApplication(exclude = {
//		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class}
//)
@SpringBootApplication
@EnableConfigurationProperties(value = ApplicationProperties.class)
public class LabelingManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(LabelingManagementApplication.class, args);
	}

}
