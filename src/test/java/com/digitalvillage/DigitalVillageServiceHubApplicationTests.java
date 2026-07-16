package com.digitalvillage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.digitalvillage.mapper.UserMapper;
import com.digitalvillage.repository.UserRepository;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration")
@ActiveProfiles("test")
class DigitalVillageServiceHubApplicationTests {

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private UserMapper userMapper;

	@Test
	void contextLoads() {
	}

}
