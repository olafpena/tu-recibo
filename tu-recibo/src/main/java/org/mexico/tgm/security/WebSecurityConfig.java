package org.mexico.tgm.security;

import java.util.Collection;

import org.mexico.tgm.model.CustomLdapUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

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
	    adProvider.setUserDetailsContextMapper(userDetailsContextMapper());
	    adProvider.setSearchFilter("(&(objectClass=user)(sAMAccountName={1}))");

      return adProvider;
  }
  
  
  @Bean
  public UserDetailsContextMapper userDetailsContextMapper() {
      return new LdapUserDetailsMapper() {
          @Override
          public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
              UserDetails details = super.mapUserFromContext(ctx, username, authorities);     
              CustomLdapUserDetails customLdapUserDetails = new CustomLdapUserDetails((LdapUserDetails) details);
              customLdapUserDetails.setNumeroEmpleado( ctx.getStringAttribute("extensionAttribute11"));
              return customLdapUserDetails;
          }
      };
  }
  
  
}
