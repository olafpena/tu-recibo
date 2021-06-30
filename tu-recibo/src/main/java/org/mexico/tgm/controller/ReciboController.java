package org.mexico.tgm.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.UserAuthenticator;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;

**/
import org.mexico.tgm.model.Ruta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jcifs.smb1.smb1.NtlmPasswordAuthentication;
import jcifs.smb1.smb1.SmbFile;

@Controller
public class ReciboController {
	
	private static Logger logger = LoggerFactory.getLogger(ReciboController.class);

	@GetMapping("/recibo")
	public String recibo(Model model) {
	    UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.addAttribute("name", ud.getUsername());
	    model.addAttribute("ruta", new Ruta());

		logger.info("Inicio de sesion usuario:" + ud.getUsername());

		return "recibo";
	}
	
	  @PostMapping("/recibo")
	  public String postRecibo(@ModelAttribute Ruta ruta, Model model) {
	    model.addAttribute("ruta", ruta);
	    
		
	    return "recibo";
	  }

	
	 private static void listFilesWindows(String shareDirectory) {
	        long startTime = System.currentTimeMillis();

	        try {
             // Domain server verification
    NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("tgm.com.mx", "garias", "buendia34");
    SmbFile remoteFile = new SmbFile(shareDirectory,"smb://192.168.0.29/Usuarios/STI/Comprobantes Nomina/2020/", auth);
             System.out.print("Remote shared directory access time-consuming: [{}]" + (System.currentTimeMillis()-startTime));
    if (remoteFile.exists()) {
      SmbFile[] files =remoteFile.listFiles();
                        SmbFile f = files[1];
                        f.exists();                    	 


    } else {
                     System.out.print("File does not exist");
    }
	        }catch(Exception e) {
	        	System.out.println(e);
	        	e.printStackTrace();
	        }

	    }
	
	 
	
	public static void main(String[] args) {
		ReciboController r = new ReciboController();
		ReciboController.listFilesWindows("smb:\\\\192.168.0.29\\Usuarios\\STI\\Comprobantes Nomina\\2020\\");

	}
}
