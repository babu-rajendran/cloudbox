package com.babu.cloudbox.service;

import com.babu.cloudbox.model.Metadata;
import com.babu.cloudbox.model.Role;
import com.babu.cloudbox.model.User;
import com.babu.cloudbox.repository.MetadataRepository;
import com.babu.cloudbox.repository.RoleRepository;
import com.babu.cloudbox.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service("metadataService")
public class MetadataServiceImpl implements MetadataService {

    @Autowired
    private MetadataRepository metadataRepository;

    @Override
    public Metadata findMetadataByFilename(String folder, String filename) {
        return metadataRepository.findByFolderAndFilename(folder, filename);
    }

    @Override
    public Metadata findMetadataById(int id) {
        Metadata metadata = null;
        try {
            metadata = metadataRepository.findById(id);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return metadata;
    }

    @Override
    public List<Metadata> findAllMetadataByUser(String folder) {
        return metadataRepository.findByFolder(folder);
    }

    @Override
    public List<Metadata> findAll() {
        return metadataRepository.findAll();
    }

    @Override
    public void saveMetadata(Metadata metadata) {
        metadataRepository.save(metadata);
    }

    @Override
    public void deleteMetadata(int id) {
        try {
            metadataRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
