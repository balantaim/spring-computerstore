/*
 * Copyright 2024-2025 Martin Atanasov.
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

package com.martinatanasov.computerstore.services;

import com.martinatanasov.computerstore.entities.Role;
import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.model.AppUserDTO;
import com.martinatanasov.computerstore.model.UserInfoDTO;
import com.martinatanasov.computerstore.repositories.RoleDao;
import com.martinatanasov.computerstore.repositories.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserDao userDao;

    @Autowired
    private final RoleDao roleDao;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public User findByUserName(final String email) {
        // Check the database if the user already exists
        return userDao.findByUserName(email);
    }

    @Override
    public User findByUserId(final Long id) {
        return userDao.findByUserId(id);
    }

    @Override
    public User findByCustomerId(final String customerId) {
        return userDao.findByCustomerId(customerId);
    }

    @Override
    public void save(AppUserDTO appUserDTO) {
        User user = new User();
        //Assign user details to the user object
        user.setEmail(appUserDTO.getEmail());
        user.setPassword(passwordEncoder.encode(appUserDTO.getPassword()));
        user.setFirstName(appUserDTO.getFirstName());
        user.setLastName(appUserDTO.getLastName());
        user.setEnabled(true);
        user.setAttempts((byte) 0);
        //The default new profile is set to verified by email
        user.setVerifiedProfile(true);

        //Set creation date
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        user.setCreationDate(timestamp);
        user.setModifyDate(timestamp);
        user.setLockDate(timestamp);

        //Give user default role of "employee"
        user.setRoles(Arrays.asList(roleDao.findRoleByName("ROLE_CUSTOMER")));

        //Save user in the database
        userDao.save(user);
    }

    @Override
    public void delete(final String email) {
        userDao.deleteByUserEmail(email);
    }

    @Override
    public void setAccountStatus(final String email, final Boolean enabled) {
        User user = userDao.findByUserName(email);
        if(user != null){
            user.setEnabled(enabled);
            userDao.save(user);
        }
    }

    @Override
    public Set<UserInfoDTO> getUsersInfo() {
        Iterable<User> data = userDao.getAllUsers();
        Set<UserInfoDTO> users = new HashSet<>();
        if(data != null){
            data.forEach(index -> {
                users.add(new UserInfoDTO(
                        index.getId(),
                        index.getEmail(),
                        index.getFirstName(),
                        index.getLastName(),
                        index.getEnabled(),
                        index.getVerifiedProfile(),
                        index.getCreationDate(),
                        index.getModifyDate()
                ));
            });
        } else {
            return null;
        }
        //Return users sorted by Email asc
        return users.stream()
                .sorted(Comparator.comparing(UserInfoDTO::getEmail))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public boolean changePassword(final String userName, final String oldPassword, final String newPassword) {
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
    public boolean setNewPassword(final String userName, final String newPassword) {
        User user = userDao.findByUserName(userName);
        if(user != null){
            //Set the new password with bcrypt to the object
            user.setPassword(passwordEncoder.encode(newPassword));
            //Change the modification date
            user.setModifyDate(new Timestamp(System.currentTimeMillis()));
            //Save the object to the DB
            userDao.save(user);
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
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
