package com.cognizant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.congnizant.model.User;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	
	public List<User> findByUserId(int followerId);
	
	public List<User> findByEmail(String email);

}
