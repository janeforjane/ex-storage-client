package org.example.logic;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.ExtensionType;
import org.example.entities.StorageFile;
import org.example.exceptions.ContractCancelledException;
import org.example.exceptions.NotFoundException;
import org.example.exceptions.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ContractFilesLogic {

    @Autowired
    CloudStorageProcessor processor;



    public StorageFile getZip(String id_qp) throws ServiceUnavailableException, NotFoundException, ContractCancelledException {

        StorageFile file = new StorageFile();

        file.setName(id_qp + ExtensionType.ZIP);
        file.setFileExtension(ExtensionType.ZIP);
        file.setBytesOfFile(processor.getFile(id_qp, ExtensionType.ZIP));

        return file;
    }

    public StorageFile getPdf(String id_qp) throws ServiceUnavailableException, NotFoundException, ContractCancelledException {

        StorageFile file = new StorageFile();

        file.setName(id_qp + ExtensionType.PDF);
        file.setFileExtension(ExtensionType.PDF);
        file.setBytesOfFile(processor.getFile(id_qp, ExtensionType.PDF));

        return file;

    }

    public StorageFile getXml(String id_qp) throws ServiceUnavailableException, NotFoundException, ContractCancelledException {

        StorageFile file = new StorageFile();

        file.setName(id_qp + ExtensionType.XML);
        file.setFileExtension(ExtensionType.XML);
        file.setBytesOfFile(processor.getFile(id_qp, ExtensionType.XML));

        return file;
    }

    public StorageFile getPks(String id_qp) throws NotFoundException, ServiceUnavailableException, ContractCancelledException {

        StorageFile file = new StorageFile();

        file.setName(id_qp + ExtensionType.PKS);
        file.setFileExtension(ExtensionType.PKS);
        file.setBytesOfFile(processor.getFile(id_qp, ExtensionType.PKS));

        return file;

    }
}
