package com.smart.entities;



import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Contact {
 
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cId;
	
	private String name;
	
	private String secondName;
	
	private String work;
	
	private String email;
	
	private String phone;
	
	private String image;
	
	@Column(length = 500)
	private String description;
	
	@ManyToOne
	@JsonIgnore
	private User user;
	
	@Override
	public boolean equals(Object object) {
		
		return (this.cId==((Contact)object).getCId());
		
	}
}
