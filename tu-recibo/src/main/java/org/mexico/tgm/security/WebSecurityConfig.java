package org.mexico.tgm.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

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
	  auth.authenticationProvider(activeDirectoryLdapAuthenticationProvider());
  }
  

  @Bean
  public AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
	    ActiveDirectoryLdapAuthenticationProvider adProvider =
	            new ActiveDirectoryLdapAuthenticationProvider("tgm.com.mx", "ldap://192.168.0.1:389","dc=tgm, dc=com, dc=mx");
	    adProvider.setConvertSubErrorCodesToExceptions(true);
	    adProvider.setUseAuthenticationRequestCredentials(true);
	    adProvider.setSearchFilter("(&(objectClass=user)(sAMAccountName={1}))");

      return adProvider;
  }
  
}
