package com.babu.cloudbox.service;

import com.babu.cloudbox.model.Metadata;
import com.babu.cloudbox.model.User;

import java.util.List;

public interface MetadataService {

    public Metadata findMetadataByFilename(String folder, String filename);

    public List<Metadata> findAllMetadataByUser(String folder);

    public void saveMetadata(Metadata metadata);

    void deleteMetadata(int id);

    List<Metadata> findAll();
    Metadata findMetadataById(int id);
}
