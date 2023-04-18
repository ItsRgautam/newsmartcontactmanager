
package com.smart.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.requestdto.UserRequest;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;




@Controller
@SessionAttributes
public class UserController {
	@Autowired
	  BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	UserRepository userRepository;
	
	@ResponseBody
	@PostMapping("/item")
	public User insertUser( @Valid@RequestBody UserRequest userRequest,BindingResult result){
		if(result.hasErrors()) {
			System.out.println(result);
			return null;
		}
		User user2= userRequest.toUser();
		 System.out.println(user2);
		return user2;
	}
  

	
	@GetMapping("/")
	public String home(Model M) {
		M.addAttribute("title", "Home-Smart Contact Manager");
		
		return "home" ;
	}
	
	@GetMapping("/about")
	public String about(Model M) {
		M.addAttribute("title", "About-Smart Contact Manager");
		
		return "about" ;
	}
	
	@GetMapping("/signup")
	public String signup(Model M,HttpSession session) {
		M.addAttribute("title", "Signup-Smart Contact Manager");
		M.addAttribute("userRequest", new UserRequest());
		session.removeAttribute("message");
		
		return "signup" ;
	}
	
	
	@RequestMapping("/signin")
	public String index(HttpSession session) {
		
		session.removeAttribute("email");
		return "login";	
		}
	
	

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("userRequest") UserRequest userRequest,BindingResult result,@RequestParam(value="agreement",defaultValue = "false") boolean agreement,Model model,HttpSession session) {
	    
		try {
			
			if(!agreement) {
				System.out.println("please mark checkbox");
				throw new Exception("Agree to terms and conditions");				
			}
			
			if(result.hasErrors()) {
				 System.out.println("validation error ***************************************************************************");
					System.out.println(result);
					model.addAttribute("userRequest", userRequest);
					session.setAttribute("message",  new Message("Invalid input ","alert-danger"));
					return "signup";
				}
			
			User user2= userRequest.toUser();
		    User users= userRepository.save(user2);
		    System.out.println(users);
		    
			 model.addAttribute("userRequest", new UserRequest() );
			 
			 session.setAttribute("message",  new Message("Successfully Register","alert-success"));
			 
				return "signup";

			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("userRequest", userRequest);
			session.setAttribute("message",  new Message("Something Went Wrong !! "+e.getMessage(),"alert-danger"));
			
			return "signup";
			
		}
		
		
	}
	
	
		
	
}
