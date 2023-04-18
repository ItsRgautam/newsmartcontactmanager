package com.smart.requestdto;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.smart.entities.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Component
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
	
	
	  BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();

	@NotBlank(message = " name cannot be empty")
	@Size(min=3,max=12,message = "name should be between 3 to 12")
     private String name;
	
	@NotBlank(message = "email cannot be blank")
	@Email(message="enter a valid email")
	private String  email;
	
	@NotBlank(message = "password cannot be blank")
	@Size(min=2,max=15,message="enter a valid pasword")
	private String password;
	
	private String about;

	
	public User toUser(){
	
		User user=new User();
		user.setName(this.getName());
		user.setEmail(this.getEmail());
		user.setPassword(passwordEncoder.encode(this.getPassword()));
		user.setAbout(this.getAbout());
		user.setEnabled(true);
		user.setRole("ROLE_USER");
		
		return user;
	}
}
