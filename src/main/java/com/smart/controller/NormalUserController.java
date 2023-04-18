package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.requestdto.UserRequest;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class NormalUserController {
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	  BCryptPasswordEncoder passwordEncoder;
	
	@ModelAttribute
	public void addCommon(Model model,Principal principal) {
		
         System.out.println(principal.getName());
		
		User user=userRepository.getUserByUserName(principal.getName());
		//System.out.println(" principal USER"+user);
		
		model.addAttribute("user", user);
		
	}
	
	
	

	@RequestMapping("/index")
	public String index(Model model,Principal principal) {
		
		model.addAttribute("title","Dashboard");
		return "normal/user_dashboard";	
		}
	
	@GetMapping("/addcontact")
	public String addContact(Model model,Principal principal,HttpSession session) {
		
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		session.removeAttribute("message");
		
		return "normal/add_contact_form";
	}
	
	@PostMapping("/registercontact")
	public String registerContact(@ModelAttribute Contact contact,@RequestParam("profileimage")MultipartFile file ,Principal principal,Model model,HttpSession session){
		
		session.removeAttribute("message");
		try {
			User user = userRepository.getUserByUserName(principal.getName());
			String mobilegot=contact.getPhone();
		
			List<Contact> optionalcontact= contactRepository.findByPhoneAndUser(mobilegot, user);
		
			
			if(optionalcontact.size()!=0) {
				throw new Exception("phone number already associated with other contact");
			}
			
			
//			processing image
             if(file.isEmpty()) {
            	 contact.setImage("defaultprofile.png");
            	 
             }
             else {
            	 Integer integer=user.getId();
            	 String id=integer.toString();
            	 String filename= id.concat(file.getOriginalFilename());
            	 contact.setImage(filename);
            	 
            	 File saveFile=new ClassPathResource("static/images").getFile() ;   
            	 
            	 Path path= Paths.get(saveFile.getAbsolutePath()+File.separator+filename);
            	 Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
             
             }
			
			
			
			contact.setUser(user);
			user.getContactList().add(contact);	
			this.userRepository.save(user);
             model.addAttribute("userRequest", new UserRequest() );
			 session.setAttribute("message",  new Message("Saved Successfully ","alert-success"));
			
			System.out.println("contact from register contact");
			
		} catch (Exception e) {
			System.out.println("ERROR"+e.getMessage());
			model.addAttribute("contact", contact);
			session.setAttribute("message",  new Message("Something Went Wrong !! "+e.getMessage(),"alert-danger"));
			
			
			e.printStackTrace();
		}
		
		
		
		
		return "normal/add_contact_form";
	}
	
	
	
	
	// show contacts
   @GetMapping("/showcontacts")	
	public String showContacts( Model model,Principal principal,HttpSession session) {
	   
	   session.removeAttribute("message");
	   model.addAttribute("title","View Contacts");
	   
	   User user=userRepository.getUserByUserName(principal.getName());
	   
	   List<Contact> contacts = this.contactRepository.findContactByUser(user.getId());
	   model.addAttribute("contacts", contacts);
	   
	   return"normal/showcontacts";
   }
   
   @GetMapping("/{cId}/contact")
   public String ContactDetails(@PathVariable("cId") Integer cId,Model model,Principal principal) {
	   model.addAttribute("title","Contact Details");
	   
	   Optional<Contact> optional=contactRepository.findById(cId);
	   Contact contact=optional.get();
	   
	   User user=userRepository.getUserByUserName(principal.getName());
	  
	   
	   if(user.getId()==(contact.getUser()).getId())
	   model.addAttribute("contact", contact);
	   
	   System.out.println("contact details printing..............."+contact.getName()+contact.getEmail());
	   
	   return "normal/contactdetails";
   }
	
	
   @GetMapping("/delete/{cId}")
   public String DeleteContact(@PathVariable("cId") Integer cId, Model model,Principal principal) {
	   
	  
	   Optional<Contact> optionalContact=contactRepository.findById(cId);
	   Contact contact=optionalContact.get();
	   
	   System.out.println("contact details printing..............."+contact.getName()+contact.getEmail());
	   
	   User user=userRepository.getUserByUserName(principal.getName());
	   
	   if(user.getId()==(contact.getUser()).getId())
		  {
		   
		   
		   if(!(contact.getImage().equals("defaultprofile.png"))) {
		   
		      try {
			         File deleteFile=new ClassPathResource("static/images").getFile() ;
			         File file1= new File(deleteFile,contact.getImage());
			         file1.delete();
			
		          } catch (Exception e) {
		 	
		                                  }
		   
		   }
		     user.getContactList().remove(contact);
		     this.userRepository.save(user);
	    }
	   	   return "redirect:/user/showcontacts";
   }

	
	
     //updatecontactform
	

   @GetMapping("/update/{cId}")
   public String UpdateContactForm(@PathVariable("cId") Integer cId, Model model,Principal principal) {
	   
	   Optional<Contact> optionalContact=contactRepository.findById(cId);
	   Contact contact=optionalContact.get();
	   model.addAttribute("contact", contact);
	   
	   return "normal/updatecontactform";
	
   }
   
   @PostMapping("/updatecontactprocess")
   public String UpdateContactProcess(@ModelAttribute Contact contact,@RequestParam("profileimage")MultipartFile file,Principal principal,Model model,HttpSession session) {
	     try {
	    	 
	    	 
	    	 System.out.println("Updating contact-------------------****************************************************************");
	  	   System.out.println("name-------"+contact.getName());
	  	   
	  	   User user=userRepository.getUserByUserName(principal.getName());
	  	 String mobilegot=contact.getPhone();
			
			List<Contact> optionalcontact= contactRepository.findByPhoneAndUser(mobilegot, user);
		
			if(optionalcontact.size()>0) {
			int contactid=optionalcontact.get(0).getCId();
			
			if(contact.getCId()!=contactid)
			{
				
				throw new Exception("phone number already associated with other contact");
			}
			}
			
			
	  	   
//	  		processing image
	         if(file.isEmpty()) {
	        	// contact.setImage("defaultprofile.png");
	        	 
	         }
	         else {
	        	 
	        	 if(!(contact.getImage().equals("defaultprofile.png"))) {
	        	 File deleteFile=new ClassPathResource("static/images").getFile() ;
		         File file1= new File(deleteFile,contact.getImage());
		         file1.delete();
	        	 }
	        	 
	        	 
	        	 Integer integer=user.getId();
	        	 String id=integer.toString();
	        	 String filename= id.concat(file.getOriginalFilename());
	        	 contact.setImage(filename);
	        	 
	        	 File saveFile=new ClassPathResource("static/images").getFile() ;   
	        	 
	        	 Path path= Paths.get(saveFile.getAbsolutePath()+File.separator+filename);
	        	 Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
	         
	         }
	  		
	  	   
	  	   
	  	   
	  	   
	  	   contact.setUser(user);
	  	   contactRepository.save(contact);
	    	 
			
		} catch (Exception e) {
			System.out.println("ERROR"+e.getMessage());
			model.addAttribute("contact", contact);
			session.setAttribute("message",  new Message("Something Went Wrong !! "+e.getMessage(),"alert-danger"));
			
			
			e.printStackTrace();
			return "normal/updatecontactform";
			
		}
	  
	   
	   return "redirect:/user/"+contact.getCId()+"/contact";
   }
	
   
//   search controller
   
   @ResponseBody
   @GetMapping("/search/{query}")
   public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal){
	
	   System.out.println(query);
	   User user = userRepository.getUserByUserName(principal.getName());
	   List<Contact> contacts=contactRepository.findByNameContainingAndUser(query, user);
	   return ResponseEntity.ok(contacts);
   }
   
   
   //Settings
   @GetMapping("/settings")
   public String settings(Model model,HttpSession session) {
	   
	   session.removeAttribute("message");
	   model.addAttribute("oldpassword", new String());
	   model.addAttribute("newpassword", new String());
	   
	   return "normal/settings";
   }
   
   @PostMapping("/changepass")
   public String changePass(@RequestParam("oldpassword") String oldpassword,@RequestParam("newpassword") String newpassword,Principal principal,HttpSession session) {
	   
	   try {
		   System.out.println("new password===----------------------------------"+newpassword);
		   System.out.println("old password===----------------------------------"+oldpassword);
		   if(newpassword.length()<5)
			   throw new Exception("enter a valid password");
		   
		   User user= userRepository.getUserByUserName(principal.getName());
		   String original=user.getPassword();
		   System.out.println("original=============================================================="+original);
		   
		
		  
		   if(this.passwordEncoder.matches(oldpassword, original)) {
			   user.setPassword(passwordEncoder.encode(newpassword));
			   userRepository.save(user);
			   session.setAttribute("message", new Message("Password changed Successfully", "alert-success"));
		   }
		   else {
			   throw new Exception("password do not match");
		   }
		
	} catch (Exception e) {
		System.out.println("Exception -------------------------------------"+e.getMessage());
		session.setAttribute("message", new Message("Password Not changed"+e.getMessage(), "alert-danger"));
	}
	
	   
	   return "normal/settings";
   }
   
}
