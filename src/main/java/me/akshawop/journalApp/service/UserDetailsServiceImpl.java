package me.akshawop.journalApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import me.akshawop.journalApp.entity.User;
import me.akshawop.journalApp.repository.UserRepo;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // get the user from db
        User user = repo.findByUsername(username);
        if (user != null) {
            return org.springframework.security.core.userdetails.User.builder()
                    // give spring security the references to where each required data points are
                    // present
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRoles().toArray(new String[0]))
                    .build();
        }
        throw new UsernameNotFoundException("No user found with the username: " + username);
    }

}
