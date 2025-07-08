package in.niet.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.niet.stockmanagement"})
public class StockManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockManagementSystemApplication.class, args);
    }
}
