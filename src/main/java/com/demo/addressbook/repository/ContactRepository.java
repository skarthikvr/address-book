package com.demo.addressbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.addressbook.entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long>  {
}