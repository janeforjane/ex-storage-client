package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.ExtensionType;
import org.example.exceptions.ContractCancelledException;
import org.example.exceptions.NotFoundException;
import org.example.exceptions.ServiceUnavailableException;
import org.example.logic.ContractFilesLogic;
import org.example.utils.RelevantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@Slf4j
public class InputService {

    @Autowired
    ContractFilesLogic contractFilesLogic;

    @Autowired
    RelevantType relevantType;

    @GetMapping("/contracts")
    public ResponseEntity<Resource> getFile(@RequestParam(name = "idQp") String idQp,
                                            @RequestParam(name = "fileType") String fileType){

        if (!isTypeRelevant(fileType)){
            StringBuilder errormessage = new StringBuilder();
            errormessage.append("В запросе указан некорректный тип файла! Возможные типы:");
            errormessage.append(relevantType.getRelevanttypes());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ByteArrayResource(errormessage.toString().getBytes(StandardCharsets.UTF_16)));
        }

        ResponseEntity<Resource> response = null;
        try {
            if("zip".equals(fileType)){
                response = getZip(idQp);
            }
            if("xml".equals(fileType)){
                response = getXml(idQp);
            }
            if("pdf".equals(fileType)){
                response = getPdf(idQp);
            }
            if("pks".equals(fileType)){
                response = getPks(idQp);
            }

        } catch (ServiceUnavailableException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ByteArrayResource(e.getMessage().getBytes(StandardCharsets.UTF_16)));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ByteArrayResource(e.getMessage().getBytes(StandardCharsets.UTF_16)));
        } catch (ContractCancelledException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ByteArrayResource(e.getMessage().getBytes(StandardCharsets.UTF_16)));
        }
        return response;
    }

    private boolean isTypeRelevant(String requestedContentType){
        return relevantType.getRelevanttypes().contains(requestedContentType);
    }

    private ResponseEntity<Resource> getZip(String idQp) throws NotFoundException, ServiceUnavailableException, ContractCancelledException {

        ByteArrayResource resource = new ByteArrayResource(contractFilesLogic.getZip(idQp).getBytesOfFile());

        String headerAttachmentFileName = "attachment; filename=" + idQp + ExtensionType.ZIP;

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, headerAttachmentFileName);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(header)
//                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(resource);
    }

    private ResponseEntity<Resource> getPdf(String idQp) throws NotFoundException, ServiceUnavailableException, ContractCancelledException {

        ByteArrayResource resource = new ByteArrayResource(contractFilesLogic.getPdf(idQp).getBytesOfFile());

        String headerAttachmentFileName = "attachment; filename=" + idQp + ExtensionType.PDF;

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, headerAttachmentFileName);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
//        header.add("Content-Type", "application/zip");

        return ResponseEntity.ok()
                .headers(header)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    private ResponseEntity<Resource> getXml(String idQp) throws NotFoundException, ServiceUnavailableException, ContractCancelledException {

        ByteArrayResource resource = new ByteArrayResource(contractFilesLogic.getXml(idQp).getBytesOfFile());

        String headerAttachmentFileName = "attachment; filename=" + idQp + ExtensionType.XML;

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, headerAttachmentFileName);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");


        return ResponseEntity.ok()
                .headers(header)
                .contentType(MediaType.APPLICATION_XML)
                .body(resource);
    }

    private ResponseEntity<Resource> getPks(String idQp) throws NotFoundException, ServiceUnavailableException, ContractCancelledException {

        ByteArrayResource resource = new ByteArrayResource(contractFilesLogic.getPks(idQp).getBytesOfFile());

        String headerAttachmentFileName = "attachment; filename=" + idQp + ExtensionType.ZIP;

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, headerAttachmentFileName);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(header)
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(resource);
    }

}
