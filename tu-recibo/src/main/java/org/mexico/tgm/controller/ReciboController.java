package org.mexico.tgm.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.UserAuthenticator;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
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
	  
	
	private String getShareFiles(Ruta ruta) {
		List<String> filesList = new ArrayList<String>();
		

		try {
			File f=new File("abc.txt"); //Takes the default path, else, you can specify the required path
			if(f.exists())
			{
			    f.delete();
			}
			f.createNewFile(); 
			FileObject destination = VFS.getManager().resolveFile(f.getAbsolutePath());
			UserAuthenticator auth = new StaticUserAuthenticator("", "GARIAS", "buendia34");
			FileSystemOptions opts = new FileSystemOptions();

			DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
			//FileObject homeDirectory = VFS.getManager().resolveFile("\\\\192.168.0.1\\direcory\\to\\GetData\\sourceFile.txt",opts);
			FileObject homeDirectory = VFS.getManager().resolveFile("\\\\192.168.0.1\\nomina\\annio\\quincena\\",opts);
			FileObject[] recibosList = homeDirectory.getChildren();
			for (int i=0; i< recibosList.length; i++) {
				//filesList.add( recibosList[i].getName().getBaseName() );
				filesList.add( recibosList[i].getName().getBaseName()+recibosList[i].getName().getExtension());
			}
			
			//destination.copyFrom(fo,Selectors.SELECT_SELF);
			destination.close();
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
