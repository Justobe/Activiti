package com.blog.yanming.repository;

import com.blog.yanming.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by YanMing on 2017/7/13.
 */
@Repository
@Transactional
public interface UserRepository extends JpaRepository<User,Long> {

    User findByUsername(String username);
    User findById(Long id);
}
