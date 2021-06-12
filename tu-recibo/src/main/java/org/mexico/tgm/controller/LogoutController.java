package org.mexico.tgm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller		
public class LogoutController {
	
		  @GetMapping("/logout")
		  public String index( Model model) {
			  return "logout";
		  }
	}
