package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class VerificationController {
	
	 @Autowired
	   private JavaMailSender mailSender;
	 
	 @Autowired
	 BCryptPasswordEncoder passwordEncoder;
	 
	 @Autowired
	 UserRepository userRepository;
	 
	 Random random = new Random(1000);
	  private int otporiginal;
	 
	 @GetMapping("/forgetpass")
	 public String forgetPassword(){
		 otporiginal = random.nextInt(999999);
		// System.out.println(otporiginal+"-*-*--*-*-*--*-*-***-*-*-*-**-*-**-*-*-*-*-*-*-*-*-*-*************");
		 return "forgetpass";
	 }
	 
	 
	     
	  @GetMapping("/email")
	    public String sendingEmail( @RequestParam(name="mailid") String mailid,Model model,HttpSession session) {
		  try {
			  session.setAttribute("email",mailid);
			  
			  User user=userRepository.getUserByUserName(mailid);
			  if(user==null) {
				  throw new Exception("No user registered with this Email");
			  }
			  
			  model.addAttribute("user",user);
			  model.addAttribute("title","verification");
			  
	         String from = "smartcontactmanagingwebsite@gmail.com";
		         String to =mailid;
		          
		         SimpleMailMessage message = new SimpleMailMessage(); 
	         
		         message.setFrom(from);
		         message.setTo(to);
		         message.setSubject("OTP");
		         otporiginal = random.nextInt(999999);
		         System.out.println(otporiginal+"-*-*--*-*-*--*-*-***-*-*-*-**-*-**-*-*-*-*-*-*-*-*-*-*************");
		        
		         message.setText("Your otp is "+otporiginal);
		          
		         mailSender.send(message);
		        	
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			
			return "/forgetpass";
		}
		  
	    	return "otpverify";
	    }

	  @GetMapping("/verifyotppass")
	    public String verifyOtpPass(@RequestParam("otp") Integer otp ,Model model) {
		  
		  try {
			  int otpgot=otp;
			 
			  System.out.println("OOOOOOOOOTTTTTTTTTTTTTPPPPPPPPPPP"+otp);
			  System.out.println("original otp :"+otporiginal);
			  System.out.println(otporiginal==otpgot);
			  if(otporiginal!=otpgot) 
				  throw new Exception("Incorrect Otp");
			
			
		} catch (Exception e) {
			System.out.println("EXCEPTION************************************"+e.getMessage());
			 return "otpverify";
		}
		  
		 return "changepassword";
	  }
	  
	 
	  @PostMapping("/changepassword")
	  public String changePassword(@RequestParam("email") String email,@RequestParam("newpassword") String newpassword,HttpSession session) {
		
		  System.out.println(email);
		  System.out.println(newpassword);
		  User user=userRepository.getUserByUserName(email);
		  user.setPassword(passwordEncoder.encode(newpassword));
		  userRepository.save(user);
		  
		  return "redirect:/signin?change=password changerd successfully";
	  }
	  
	  
	  
}
