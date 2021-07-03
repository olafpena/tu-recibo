package org.mexico.tgm.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.mexico.tgm.model.CustomLdapUserDetails;
import org.mexico.tgm.model.Ruta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jcifs.smb1.smb1.NtlmPasswordAuthentication;
import jcifs.smb1.smb1.SmbFile;

@Controller
public class ReciboController {

	private static Logger logger = LoggerFactory.getLogger(ReciboController.class);
	
	private String annio;

	@GetMapping("/recibo")
	public String recibo(Model model, @RequestParam(name = "annio",required = false) String annio) {
		if(annio==null) {
			Calendar calendar = Calendar.getInstance();
			this.annio = String.valueOf(calendar.get(Calendar.YEAR));			
		}else {
			this.annio = annio;
		}
		
		CustomLdapUserDetails userDetails = (CustomLdapUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.addAttribute("userName", userDetails.getUsername().toUpperCase());
		model.addAttribute("year",this.annio);

		model.addAttribute("recibos", listFilesWindows("smb:\\\\192.168.0.29\\Usuarios\\STI\\Comprobantes Nomina\\"+this.annio+"\\", userDetails.getNumeroEmpleado()));
		logger.info("Inicio de sesion usuario:" + userDetails.getUsername());

		return "recibo";
	}

	@PostMapping("/recibo")
	public String postRecibo(@ModelAttribute Ruta ruta, Model model) {
		model.addAttribute("ruta", ruta);

		return "recibo";
	}


	private List<Ruta> listFilesWindows(String shareDirectory,String idEmpleado) {
		long startTime = System.currentTimeMillis();
		List<Ruta> nameFileList = new ArrayList<Ruta>();
		try {
			// Domain server verification
			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("tgm.com.mx", "garias", "buendia34");
			SmbFile remoteFile = new SmbFile(shareDirectory,
					"smb://192.168.0.29/Usuarios/STI/Comprobantes Nomina/"+this.annio+ "/", auth);
			System.out.print(
					"Remote shared directory access time-consuming: [{}]" + (System.currentTimeMillis() - startTime));
			if (remoteFile.exists() && 	remoteFile.isDirectory()) {
				SmbFile[] quincenaFolders = remoteFile.listFiles();
				for (int i = 0; i<quincenaFolders.length; i++ ) {
					SmbFile quincena = quincenaFolders[i];
					if ( quincena!=null && quincena.exists() && quincena.isDirectory() ) {
						SmbFile[] quincenaFiles = quincena.listFiles();
						for (int j=0; j<quincenaFiles.length; j++) {
							if (quincenaFiles!= null && quincenaFiles[j].isFile()) {
								SmbFile recibo = quincenaFiles[j];
								int initIndex = recibo.getName().lastIndexOf("_");
								if (initIndex > 1) {
									String lastFileName = recibo.getName().substring(initIndex);
									//System.out.println( "FileName= "+ recibo.getCanonicalPath());
									if(lastFileName.contains("_"+idEmpleado+".")) {
										Ruta ruta = new Ruta();
										ruta.setNombreRecibo(recibo.getName());
										ruta.setRutaRecibo(recibo.getCanonicalPath());
										nameFileList.add(ruta);
										System.out.println( "FileName= "+ recibo.getCanonicalPath());
										
									}									
								}
							}
						}
					}
				}

			} else {
				System.out.print("File does not exist");
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return nameFileList;
	}	

	public static void main(String[] args) {
		ReciboController r = new ReciboController();
		r.listFilesWindows("smb:\\\\192.168.0.29\\Usuarios\\STI\\Comprobantes Nomina\\2020\\", "1179");

	}
}
