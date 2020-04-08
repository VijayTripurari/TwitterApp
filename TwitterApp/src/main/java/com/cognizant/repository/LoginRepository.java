package com.cognizant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.congnizant.model.LoginDetail;

@Repository
public interface LoginRepository extends JpaRepository<LoginDetail, Integer> {
	
	public List<LoginDetail> findByLoginId(Integer loginId);
	public List<LoginDetail> findByToken(String token);
 
}
