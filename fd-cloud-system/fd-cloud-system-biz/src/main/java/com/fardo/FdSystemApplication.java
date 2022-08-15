package com.fardo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@EnableSwagger2
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableAsync
public class FdSystemApplication {

  public static void main(String[] args) throws UnknownHostException {

    ConfigurableApplicationContext application = SpringApplication.run(FdSystemApplication.class, args);
    Environment env = application.getEnvironment();
    String ip = InetAddress.getLocalHost().getHostAddress();
    String port = env.getProperty("server.port");
    log.info("\n----------------------------------------------------------\n\t" +
        "Application FD-SYSTEM-BIZ is running! Access URLs:\n\t" +
        "Local: \t\thttp://localhost:" + port  + "/\n\t" +
        "External: \thttp://" + ip + ":" + port  + "/\n\t" +
        "Swagger-UI: \t\thttp://" + ip + ":" + port  + "/doc.html\n" +
        "----------------------------------------------------------");

    log.info(env.getProperty("project.version"));

  }
}