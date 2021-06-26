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
	  
	/**
	private String getShareFiles(Ruta ruta) {
		List<String> filesList = new ArrayList<String>();
		

		try {
			File f=new File("abc.txt"); //Takes the default path, else, you can specify the required path
			if(f.exists())
			{
			    f.delete();
			}
			f.createNewFile(); 
			//FileObject destination = VFS.getManager().resolveFile(f.getAbsolutePath());
			//UserAuthenticator auth = new StaticUserAuthenticator("", "GARIAS", "buendia34");
			UserAuthenticator auth = new StaticUserAuthenticator(null, "apena", "*Paperboy1");

			FileSystemOptions opts = new FileSystemOptions();

			DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
			//FileObject homeDirectory = VFS.getManager().resolveFile("\\\\192.168.0.1\\direcory\\to\\GetData\\sourceFile.txt",opts);
			//FileObject homeDirectory = VFS.getManager().resolveFile("\\\\192.168.0.1\\Comprobantes de nomina\\annio\\quincena\\",opts);
		    FileSystemManager fs = VFS.getManager();
			FileObject homeDirectory = fs.resolveFile("192.168.1.108\\public\\mac",opts);
			FileObject[] recibosList = homeDirectory.getChildren();
			for (int i=0; i< recibosList.length; i++) {
				//filesList.add( recibosList[i].getName().getBaseName() );
				filesList.add( recibosList[i].getName().getBaseName()+recibosList[i].getName().getExtension());
				System.out.println("nombre archivo:"+filesList.get(i));
			}
			
			//destination.copyFrom(fo,Selectors.SELECT_SELF);
			//destination.close();
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	***/

	 
	 private static void listFiles(String shareDirectory) {
	        long startTime = System.currentTimeMillis();

	        try {
                // Domain server verification
       NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("WORKGROUP", "apena", "Paperboy1");
       SmbFile remoteFile = new SmbFile(shareDirectory, auth);
                System.out.print("Remote shared directory access time-consuming: [{}]" + (System.currentTimeMillis()-startTime));
       if (remoteFile.exists()) {
           SmbFile[] files = remoteFile.listFiles();
           //remoteFile.listFiles(shareDirectory);
                        System.out.println("File exists");
           for (SmbFile f : files) {
               System.out.println(f.getName());
           }
       } else {
                        System.out.print("File does not exist");
       }
	        }catch(Exception e) {
	        	System.out.println(e);
	        	e.printStackTrace();
	        }

	    }
	
	 private static void listFilesWindows(String shareDirectory) {
	        long startTime = System.currentTimeMillis();

	        try {
             // Domain server verification
    NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("WORKGROUP", "GARIAS", "buendia34");
    SmbFile remoteFile = new SmbFile(shareDirectory, auth);
             System.out.print("Remote shared directory access time-consuming: [{}]" + (System.currentTimeMillis()-startTime));
    if (remoteFile.exists()) {
        SmbFile[] files = remoteFile.listFiles();
        //remoteFile.listFiles(shareDirectory);
                     System.out.println("File exists");
        for (SmbFile f : files) {
            System.out.println(f.getName());
        }
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
	//	r.getShareFiles(new Ruta());
		//ReciboController.listFiles("smb://192.168.1.1/datos/memory1/");
		ReciboController.listFilesWindows("\\192.168.0.29\\STI\\Comprobantes Nomina\\2020");

	}
}
