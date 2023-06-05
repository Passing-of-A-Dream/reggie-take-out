package ink.ybl.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import static ink.ybl.reggie.PrintNoBug.printNoBug;

@Slf4j
@SpringBootApplication
@ServletComponentScan // 扫描注解 启动拦截
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动成功");
        printNoBug();
    }
}
