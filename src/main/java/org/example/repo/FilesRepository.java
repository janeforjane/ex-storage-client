package org.example.repo;


import org.example.exceptions.NotFoundException;
import org.example.exceptions.ServiceUnavailableException;

import java.util.Set;

public interface FilesRepository {

    Set<String> getListOfFiles() throws ServiceUnavailableException;
    byte[] getFile(String fileName, String extension) throws ServiceUnavailableException, NotFoundException;
    boolean isFileExistAndActive(String fileName) throws ServiceUnavailableException, NotFoundException;
    boolean isFileExistAndCancelled(String fileName) throws ServiceUnavailableException, NotFoundException;
}
