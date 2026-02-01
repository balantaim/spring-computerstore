/*
 * Copyright 2024-2026 Martin Atanasov.
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
import com.martinatanasov.computerstore.repositories.RoleDao;
import com.martinatanasov.computerstore.repositories.UserDao;
import com.martinatanasov.computerstore.services.payments.PaymentCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PaymentCustomerService paymentCustomerService;
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

    @Transactional
    @Override
    public void save(AppUserDTO appUserDTO) {
        User user = new User();
        //Assign user details to the user object
        user.setEmail(appUserDTO.getEmail());
        user.setPassword(passwordEncoder.encode(appUserDTO.getPassword()));
        user.setFirstName(appUserDTO.getFirstName());
        user.setLastName(appUserDTO.getLastName());
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setAttempts((byte) 0);
        //The default new profile is set to verified by email
        user.setVerifiedProfile(true);
        //Set lock date
        user.setLockDate(new Timestamp(System.currentTimeMillis()));
        //Give user default role of "employee"
        user.setRoles(Arrays.asList(roleDao.findRoleByName("ROLE_CUSTOMER")));
        //Save user in the database
        userDao.save(user);
    }

    @Transactional
    @Override
    public void save(User user) {
        //Save user in the database
        userDao.save(user);
    }

    @Transactional
    @Override
    public void delete(final String email) {
        userDao.deleteByUserEmail(email);
    }

    @Transactional
    @Override
    public void setAccountStatus(final String email, final Boolean enabled) {
        User user = userDao.findByUserName(email);
        if (user != null) {
            user.setEnabled(enabled);
            userDao.save(user);
        }
    }

    @Override
    public List<User> getUsersDetailsInfo() {
        return userDao.getAllUsers();
    }

    @Transactional
    @Override
    public void disableOrEnableUser(final Long userId, final boolean enabled, boolean accountNonLocked, final boolean verified) {
        User user = findByUserId(userId);
        if (user != null) {
            if (user.getEnabled() != enabled) {
                user.setEnabled(enabled);
                if (enabled) {
                    user.setAttempts((byte) 0);
                }
            }
            if (user.getAccountNonLocked() != accountNonLocked) {
                user.setAccountNonLocked(accountNonLocked);
            }
            if (user.getVerifiedProfile() != verified) {
                user.setVerifiedProfile(verified);
            }
            userDao.save(user);
        }
    }

    @Transactional
    @Override
    public boolean changePassword(final String userName, final String oldPassword, final String newPassword) {
        //Get the current user
        User user = userDao.findByUserName(userName);
        if (user != null) {
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                //Set the new password with bcrypt to the object
                user.setPassword(passwordEncoder.encode(newPassword));
                //Save the object to the DB
                userDao.save(user);
                return true;
            }
        }
        return false;
    }

    @Transactional
    @Override
    public boolean setNewPassword(final String userName, final String newPassword) {
        User user = userDao.findByUserName(userName);
        if (user != null) {
            //Set the new password with bcrypt to the object
            user.setPassword(passwordEncoder.encode(newPassword));
            //Save the object to the DB
            userDao.save(user);
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public void updateAddressInformation(final String username,
            final String firstName,
            final String lastName,
            final String phone,
            final String country,
            final String address) {
        User user = userDao.findByUserName(username);
        if (user != null) {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(phone);
            user.setCountry(country);
            user.setAddress(address);
            //Save the new data
            userDao.save(user);
            //Update customer's info if customerId exist
            if (user.getCustomerId() != null) {
                paymentCustomerService.updateCustomerById(user.getCustomerId(), user);
            }
        }
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
