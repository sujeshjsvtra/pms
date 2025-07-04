package com.sparksupport.pms;

import com.sparksupport.pms.model.Product;
import com.sparksupport.pms.model.Sale;
import com.sparksupport.pms.service.ProductService;
import com.sparksupport.pms.service.SaleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class PmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PmsApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(ProductService productService, SaleService saleService) {
		return args -> {
			// Create test products
			Product p1 = new Product();
			p1.setName("Laptop");
			p1.setDescription("Gaming Laptop");
			p1.setPrice(1200.0);
			p1.setQuantity(10);
			productService.addProduct(p1);

			Product p2 = new Product();
			p2.setName("Phone");
			p2.setDescription("Smartphone");
			p2.setPrice(800.0);
			p2.setQuantity(20);
			productService.addProduct(p2);

			 
			Sale s1 = new Sale();
			s1.setProduct(p1);
			s1.setQuantity(2);
			s1.setSaleDate(LocalDateTime.now());
			saleService.addSale(s1);

			Sale s2 = new Sale();
			s2.setProduct(p2);
			s2.setQuantity(3);
			s2.setSaleDate(LocalDateTime.now());
			saleService.addSale(s2);

			Sale s3 = new Sale();
			s3.setProduct(p1);
			s3.setQuantity(1);
			s3.setSaleDate(LocalDateTime.now());
			saleService.addSale(s3);

			 
			System.out.println("Total Revenue: " + productService.getTotalRevenue());
			System.out.println("Revenue for Laptop: " + productService.getRevenueByProduct(p1.getId()));
			System.out.println("Revenue for Phone: " + productService.getRevenueByProduct(p2.getId()));
		};
	}

}
