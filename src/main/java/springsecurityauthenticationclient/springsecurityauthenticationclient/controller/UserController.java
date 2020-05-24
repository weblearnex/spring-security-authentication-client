package springsecurityauthenticationclient.springsecurityauthenticationclient.controller;

import java.io.IOException;
import java.util.Arrays;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import springsecurityauthenticationclient.springsecurityauthenticationclient.model.User;

@RestController
public class UserController {
	
	 @GetMapping("/users")
	    public ModelAndView getUser() {
	        return new ModelAndView("user");
	    }
		
		 @GetMapping("/showUsers")
		 public ModelAndView showUsers(@RequestParam("code") String code) throws JsonProcessingException, IOException {
				ResponseEntity<String> response = null;
				System.out.println("Authorization Ccode------" + code);
				RestTemplate restTemplate = new RestTemplate();
				String credentials = "webleanex:webleanex";
				String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				headers.add("Authorization", "Basic " + encodedCredentials);
				HttpEntity<String> request = new HttpEntity<String>(headers);

				String access_token_url = "http://localhost:8080/oauth/token";
				access_token_url += "?code=" + code;
				access_token_url += "&grant_type=authorization_code";
				access_token_url += "&redirect_uri=http://localhost:8090/showUsers";

				response = restTemplate.exchange(access_token_url, HttpMethod.POST, request, String.class);

				System.out.println("Access Token Response ---------" + response.getBody());
				// getting  Access Token From the recieved JSON response
				ObjectMapper mapper = new ObjectMapper();
				JsonNode node = mapper.readTree(response.getBody());
				String token = node.path("access_token").asText();
				String url = "http://localhost:8080/users";
				HttpHeaders headers1 = new HttpHeaders();
				// binding  Access Token with bearer for any other API call
				headers1.add("Authorization", "Bearer " + token);
				
				HttpEntity<String> entity = new HttpEntity<>(headers1);
				ResponseEntity<User[]> users = restTemplate.exchange(url, HttpMethod.GET, entity, User[].class);
				System.out.println(users);
				User[] userArray = users.getBody();
				System.out.println("Totals Register Users are=="+userArray.length);
				ModelAndView model = new ModelAndView("showUser");
				model.addObject("users", Arrays.asList(userArray));
				return model;
			}
}
