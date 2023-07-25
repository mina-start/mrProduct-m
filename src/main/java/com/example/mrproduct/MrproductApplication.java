package com.example.mrproduct;

import com.example.mrproduct.netty.NettyServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.Async;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.example.mrproduct.mapper")
public class MrproductApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MrproductApplication.class, args);
    }

    @Autowired
    private NettyServer nettyServer;

    /**
     * run方法必须异步，不异步的话，打包的时候在test环节，主线程会当作一直没有执行完毕的状态，所以必须异步
     */
    @Async
    @Override
    public void run(String... args) throws Exception {
        nettyServer.start();
    }
}
