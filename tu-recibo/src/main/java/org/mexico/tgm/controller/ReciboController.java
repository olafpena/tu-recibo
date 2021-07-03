package org.mexico.tgm.controller;

import java.util.ArrayList;
import java.util.List;

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
		model.addAttribute("userName", ud.getUsername().toUpperCase());
		model.addAttribute("recibos", listFilesWindows("smb:\\\\192.168.0.29\\Usuarios\\STI\\Comprobantes Nomina\\2020\\", "1179"));
		logger.info("Inicio de sesion usuario:" + ud.getUsername());

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
					"smb://192.168.0.29/Usuarios/STI/Comprobantes Nomina/2020/", auth);
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
