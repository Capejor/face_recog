package com.faceRecog.manage;

import java.lang.management.ManagementFactory;


import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;



@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = "com.faceRecog.manage.mapper")
public class SpringbootShiroApplication {

	/*public static void main(String[] args) {
		SpringApplication.run(SpringbootShiroApplication.class, args);
	}*/
	
	private static final Logger logger = LoggerFactory.getLogger(SpringbootShiroApplication.class);
	 
	private static ApplicationContext applicationContext = null;
	public static void main(String[] args) {
		String mode = args != null && args.length > 0 ? args[0] : null;
 
		if (logger.isDebugEnabled()) {
			logger.debug("PID:" + ManagementFactory.getRuntimeMXBean().getName() + " Application mode:" + mode + " context:" + applicationContext);
		}
		if (applicationContext != null && mode != null && "stop".equals(mode)) {
			System.exit(SpringApplication.exit(applicationContext, new ExitCodeGenerator() {
				@Override
				public int getExitCode() {
					return 0;
				}
			}));
		}
		else {
			SpringApplication app = new SpringApplication(SpringbootShiroApplication.class);
			applicationContext = app.run(args);
			if (logger.isDebugEnabled()) {
				logger.debug("PID:" + ManagementFactory.getRuntimeMXBean().getName() + " Application started context:" + applicationContext);
			}
		}
	}
}
