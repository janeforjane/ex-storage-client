package org.example.repo;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.IOUtils;
import org.example.entities.ExtensionType;
import org.example.exceptions.NotFoundException;
import org.example.exceptions.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@Slf4j
@Repository
public class ContractsRepository implements FilesRepository{

    @Autowired
    Sardine sardine;

    @Value("${application.cloudstorage.address}")
    private String generalCloudStorageAddress;

//    private static final String EXTENSION = ".zip";
    private static final String SERVER_LOCATION_CONTRACTS_ACTIVE = "contracts/active";
    private static final String SERVER_LOCATION_CONTRACTS_CANCELLED = "contracts/cancelled";

    public boolean isFileExistAndActive(String fileId) throws ServiceUnavailableException {

        String zipFileName = fileId + ExtensionType.ZIP;
        log.info("Try to find in cloud storage file: {}", zipFileName);

        try {
            return sardine.exists(generalCloudStorageAddress + SERVER_LOCATION_CONTRACTS_ACTIVE + File.separator + zipFileName);
        } catch (IOException e) {
            log.error("Error while trying to define is file with id: {} exists in cloud storage. Error message: {}", fileId, e.getMessage());
            throw new ServiceUnavailableException("Error while trying to define is file with id: " + fileId + " exists in cloud storage. Error message: " + e.getMessage());
        }
    }

    @Override
    public boolean isFileExistAndCancelled(String fileId) throws ServiceUnavailableException {

        String zipFileName = fileId + ExtensionType.ZIP;
        log.info("Try to find in cloud storage file: {}", zipFileName);

        try {
            return sardine.exists(generalCloudStorageAddress + SERVER_LOCATION_CONTRACTS_CANCELLED + File.separator + zipFileName);
        } catch (IOException e) {
            log.error("Error while trying to define is file with id: {} exists in cloud storage. Error message: {}", fileId, e.getMessage());
            throw new ServiceUnavailableException("Error while trying to define is file with id: " + fileId + " exists in cloud storage. Error message: " + e.getMessage());
        }
    }

    @Override
    public Set<String> getListOfFiles() throws ServiceUnavailableException {

        log.info("Start to collect list of files from point: {}", (generalCloudStorageAddress + SERVER_LOCATION_CONTRACTS_ACTIVE + File.separator));

        Set<String> listOfFiles = new HashSet<>();

        try {
            List<DavResource> resources = sardine.list(generalCloudStorageAddress + SERVER_LOCATION_CONTRACTS_ACTIVE + File.separator, 1, true);

            resources.forEach(davResource -> {
                listOfFiles.add(davResource.getName());
            });

        } catch (IOException e) {
            log.error("Error while trying to get list of contracts files from cloud storage. Error message: {}", e.getMessage());
            throw new ServiceUnavailableException("Error while trying to get list of contracts files from cloud storage. Error message: " + e.getMessage());
        }

        return listOfFiles;

    }

    public byte[]  getFile(String fileName, String extension) throws ServiceUnavailableException, NotFoundException {

        if(ExtensionType.ZIP.equals(extension)){
            return getZipFile(fileName);
        }

        if(ExtensionType.PKS.equals(extension)){
            return getPksFiles(fileName);
        }

        return getOtherExtensionFile(fileName, extension);
    }

    private byte[]  getZipFile(String fileName) throws ServiceUnavailableException {

        byte[] bytes;
        String completeFileName = fileName + ExtensionType.ZIP;

        InputStream inputStream = null;
        try {

            inputStream = sardine.get(generalCloudStorageAddress + SERVER_LOCATION_CONTRACTS_ACTIVE + File.separator + completeFileName);
            bytes = IOUtils.toByteArray(inputStream);

        } catch (IOException e) {
            log.error("Error while trying to get file: {} from cloud storage. Error message: {}", fileName, e.getMessage());
            throw new ServiceUnavailableException("Error while trying to get file: " + fileName + " from cloud storage. Error message: " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return bytes;
    }

    public byte[]  getOtherExtensionFile(String fileName, String extension) throws ServiceUnavailableException, NotFoundException {

        //get zip, find specific file in zip, read bytes of this file

        InputStream inputStream = null;
        String zipFileName = fileName + ExtensionType.ZIP;

        try {

            inputStream = sardine.get(generalCloudStorageAddress + SERVER_LOCATION_CONTRACTS_ACTIVE + File.separator + zipFileName);

            ArchiveStreamFactory archiveStreamFactory = new ArchiveStreamFactory();
            ArchiveInputStream archiveInputStream = archiveStreamFactory.createArchiveInputStream(ArchiveStreamFactory.ZIP, inputStream);

            ArchiveEntry archiveEntry;

            while ((archiveEntry = archiveInputStream.getNextEntry()) != null) {
                if (archiveEntry.getName().contains(extension)) {
                    log.info("File {} was found and will be obtained by client", archiveEntry.getName());
                    return IOUtils.toByteArray(archiveInputStream);
                }
            }
        } catch (ArchiveException | IOException e) {
            log.error("Error while trying to get file: {} from cloud storage. Error message: {}", fileName, e.getMessage());
            throw new ServiceUnavailableException("Error while trying to get file: " + fileName + " from cloud storage. Error message: " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        log.error("Error while trying to find file {} with extension: {} from cloud storage.",fileName, extension);
        throw new NotFoundException("Файл с именем: " + fileName + " и расширением: " + extension + " не найден.");
    }

    public byte[]  getPksFiles(String fileName) throws ServiceUnavailableException, NotFoundException {

        //get archive, find specific files in 1st archive, read bytes of files and pack in 2nd archive

        byte[] inputbytes;
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            inputStream = sardine.get(generalCloudStorageAddress + SERVER_LOCATION_CONTRACTS_ACTIVE + File.separator + fileName + ExtensionType.ZIP);

            log.info("Getting PKS files. Firstly try to get archive for id: {}, on point: {}", fileName, (generalCloudStorageAddress + SERVER_LOCATION_CONTRACTS_ACTIVE + File.separator + fileName + ExtensionType.ZIP));

            ArchiveStreamFactory archiveStreamFactory = new ArchiveStreamFactory();
            ArchiveInputStream archiveInputStream = archiveStreamFactory.createArchiveInputStream(ArchiveStreamFactory.ZIP, inputStream);

            //outputstream (destination for data)
            byteArrayOutputStream = new ByteArrayOutputStream();
            ArchiveOutputStream archiveOutputStream = archiveStreamFactory.createArchiveOutputStream("zip", byteArrayOutputStream);

            ArchiveEntry archiveEntry = null;
            while ((archiveEntry = archiveInputStream.getNextEntry()) != null) {

                if (archiveEntry.getName().contains(ExtensionType.PKS7) || archiveEntry.getName().contains(ExtensionType.PKS10)) {

                    log.info("In archive {} was found file: {}", (fileName + ExtensionType.ZIP), archiveEntry.getName());

                    inputbytes = IOUtils.toByteArray(archiveInputStream);

                    ArchiveEntry zipAE = new ZipArchiveEntry(archiveEntry.getName());
                    archiveOutputStream.putArchiveEntry(zipAE);
                    archiveOutputStream.write(inputbytes);
                    archiveOutputStream.closeArchiveEntry();
                }
            }
            archiveOutputStream.finish();
            archiveOutputStream.flush();

            return byteArrayOutputStream.toByteArray();

        } catch (ArchiveException e) {
            log.error("ArchiveException error while trying to get file: {} from cloud storage. Error message: {}", fileName, e.getMessage());
            throw new ServiceUnavailableException("Error while trying to get file: " + fileName + " from cloud storage. Error message: " + e.getMessage());
        } catch (IOException e) {
            log.error("IOException error while trying to get file: {} from cloud storage. Error message: {}", fileName, e.getMessage());
            throw new ServiceUnavailableException("Error while trying to get file: " + fileName + " from cloud storage. Error message: " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(byteArrayOutputStream);
        }
    }



    public Map<String, String> getAllContracts() throws ServiceUnavailableException {

        Map <String, String> passports = new HashMap<>();

        try {
            List<DavResource> resources = sardine.list(generalCloudStorageAddress + SERVER_LOCATION_CONTRACTS_ACTIVE + File.separator, 1, true);
            resources.forEach(davResource -> {
                if("application/zip".equals(davResource.getContentType())){
                    passports.put(davResource.getName(), davResource.getPath());
                }
            });
        } catch (IOException e) {
            log.error("Error while trying to get all contracts files from cloud storage. Error message: {}", e.getMessage());
            throw new ServiceUnavailableException("Error while trying to get all contracts files from cloud storage. Error message: " + e.getMessage());
        }

        return passports;
    }
}
