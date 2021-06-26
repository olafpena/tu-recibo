package org.mexico.tgm.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
   
    http
    .authorizeRequests()
    	.antMatchers("/recibo").authenticated()
    	.antMatchers("/static/**").permitAll()
    	.anyRequest().fullyAuthenticated()
    	.and()
    .formLogin()
        .loginPage("/login")
        .defaultSuccessUrl("/recibo", true)
        .permitAll()
        .and()
    .logout()                                    
        .permitAll();
    
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
	  /**
    auth
      .ldapAuthentication()
        .userDnPatterns("uid={0},ou=people")
        .groupSearchBase("ou=groups")
        .contextSource()
          .url("ldap://localhost:8389/dc=springframework,dc=org")
          .and()
        .passwordCompare()
          .passwordEncoder(new BCryptPasswordEncoder())
          .passwordAttribute("userPassword");
**/
	  
	    auth
	      .ldapAuthentication()
	        .userDnPatterns("uid={0},ou=people")
	        .groupSearchBase("ou=groups")
	        .contextSource()
	          .url("ldap://192.168.0.1:8389/dc=tgm,dc=com,dc=mx")
	          .managerDn("administrador")
	          .managerPassword("STIYCP@sw0rd19")
	          .and()
	        .passwordCompare()
	          .passwordEncoder(new BCryptPasswordEncoder())
	          .passwordAttribute("userPassword");	  
  
  }
  
}
