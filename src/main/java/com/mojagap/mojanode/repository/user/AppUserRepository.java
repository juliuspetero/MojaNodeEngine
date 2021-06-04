package com.mojagap.mojanode.repository.user;

import com.mojagap.mojanode.model.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
}
