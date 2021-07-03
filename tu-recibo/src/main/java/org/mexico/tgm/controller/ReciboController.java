package org.mexico.tgm.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.mexico.tgm.model.CustomLdapUserDetails;
import org.mexico.tgm.model.Ruta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jcifs.smb1.smb1.NtlmPasswordAuthentication;
import jcifs.smb1.smb1.SmbFile;

@Controller
public class ReciboController {

	@Autowired
	private Environment env;
	
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

		// 
		String ruta = env.getProperty("mx.com.tgm.ruta").replace("/", "\\");
		logger.info("ruta escapada={}",ruta);
		model.addAttribute("recibos", listFilesWindows(ruta+this.annio+"\\", userDetails.getNumeroEmpleado()));
		logger.info("Inicio de sesion usuario={}",userDetails.getUsername());

		return "recibo";
	}


	private List<Ruta> listFilesWindows(String shareDirectory,String idEmpleado) {
		List<Ruta> nameFileList = new ArrayList<Ruta>();
		try {
			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("tgm.com.mx", "garias", "buendia34");
			SmbFile remoteFile = new SmbFile(shareDirectory,
					env.getProperty("mx.com.tgm.ruta")+this.annio+ "/", auth);
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
									if(lastFileName.contains("_"+idEmpleado+".")) {
										Ruta ruta = new Ruta();
										ruta.setNombreRecibo(recibo.getName());
										ruta.setRutaRecibo(recibo.getCanonicalPath());
										nameFileList.add(ruta);
										logger.info("Found FileName={}", recibo.getCanonicalPath());										
									}									
								}
							}
						}
					}
				}

			} else {
				logger.info("No se encontraro archivos para el empleado={}", idEmpleado);
			}
		} catch (Exception e) {
			logger.info("Error al buscare archivos para el empleado={}, error={}", idEmpleado,e);
			e.printStackTrace();
		}
		logger.info("Archivos encontrados={} para el empleado={}", nameFileList.size(), idEmpleado);
		return nameFileList;
	}	

	public static void main(String[] args) {
		ReciboController r = new ReciboController();
		r.listFilesWindows("smb:\\\\192.168.0.29\\Usuarios\\STI\\Comprobantes Nomina\\2020\\", "1179");

	}
}
