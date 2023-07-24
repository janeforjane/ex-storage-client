package org.example.entities;

import lombok.Data;

@Data
public class StorageFile {

    String name;
    String path;
    byte[] bytesOfFile;
    String fileExtension;


}
