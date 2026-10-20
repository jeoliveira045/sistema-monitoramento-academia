package monokai.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import tools.jackson.databind.ObjectMapper;

@Configuration
public class HttpClientConfig {

	@Value("${api.baseurl}")
	private String baseurl;

	ObjectMapper om = new ObjectMapper();

	@Bean
	@Order(0)
	public HttpClient httpClient() {

		return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).version(HttpClient.Version.HTTP_2)
				.build();

	}

}
