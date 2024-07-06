/*
 * Copyright 2024 Martin Atanasov.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.martinatanasov.computerstore.service;

import com.martinatanasov.computerstore.dao.RoleDao;
import com.martinatanasov.computerstore.dao.UserDao;
import com.martinatanasov.computerstore.entity.Role;
import com.martinatanasov.computerstore.entity.User;
import com.martinatanasov.computerstore.model.WebUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    private final RoleDao roleDao;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleDao roleDao, BCryptPasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByUserName(String email) {
        // check the database if the user already exists
        return userDao.findByUserName(email);
    }

    @Override
    public void save(WebUser webUser) {

        User user = new User();

        // assign user details to the user object
        user.setEmail(webUser.getEmail());
        user.setPassword(passwordEncoder.encode(webUser.getPassword()));
        user.setFirstName(webUser.getFirstName());
        user.setLastName(webUser.getLastName());
        user.setEnabled(true);
        //The default new profile is set to verified by email
        user.setVerifiedProfile(true);

        //Set creation date
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        user.setCreationDate(timestamp);
        user.setModifyDate(timestamp);

        // give user default role of "employee"
        user.setRoles(Arrays.asList(roleDao.findRoleByName("ROLE_CUSTOMER")));

        // save user in the database
        userDao.save(user);
    }

    @Override
    public boolean changePassword(String userName, String oldPassword, String newPassword) {
        //Get the current user
        User user = userDao.findByUserName(userName);
        if(user != null){
            if(passwordEncoder.matches(oldPassword, user.getPassword())){
                //Set the new password with bcrypt to the object
                user.setPassword(passwordEncoder.encode(newPassword));
                //Change the modification date
                user.setModifyDate(new Timestamp(System.currentTimeMillis()));
                //Save the object to the DB
                userDao.save(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userDao.findByUserName(email);

        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        Collection<SimpleGrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    private Collection<SimpleGrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role index : roles) {
            SimpleGrantedAuthority tempAuthority = new SimpleGrantedAuthority(index.getAuthority());
            authorities.add(tempAuthority);
        }
        return authorities;
    }
}
