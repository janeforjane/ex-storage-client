package org.example.logic;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.ExtensionType;
import org.example.exceptions.ContractCancelledException;
import org.example.exceptions.NotFoundException;
import org.example.exceptions.ServiceUnavailableException;
import org.example.repo.FilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CloudStorageProcessor {

    @Autowired
    FilesRepository filesRepository;

    public byte[] getFile(String fileName, String extension) throws ServiceUnavailableException, NotFoundException, ContractCancelledException {

        String archiveFileName = fileName + ExtensionType.ZIP;
        boolean isFileExistAndActive = filesRepository.isFileExistAndActive(fileName);
        boolean isFileExistAndCancelled = filesRepository.isFileExistAndCancelled(fileName);

        if(!isFileExistAndActive && !isFileExistAndCancelled){
            log.info("File with name {} doesn't exist", fileName);
            throw new NotFoundException("File with name " + fileName + " doesn't exist");
        }

        if(isFileExistAndCancelled){
            log.info("File with name {} doesn't exist", fileName);
            throw new ContractCancelledException("File with name " + fileName + " was cancelled");
        }

        log.info("Zip file: {} is detected in cloud storage. Start attempts for getting certain files with id: {} and pks extensions", archiveFileName, fileName);

        return filesRepository.getFile(fileName, extension);
    }
}
