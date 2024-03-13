package com.nguyenphuong.PackageTransfer;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class PackageTransferApplicationTests {

  @MockBean
  private RedissonClient redissonClient;


  @Test
  void contextLoads() {
  }

}
