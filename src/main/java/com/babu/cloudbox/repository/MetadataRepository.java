package com.babu.cloudbox.repository;

import com.babu.cloudbox.model.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("metadataRepository")
public interface MetadataRepository extends JpaRepository<Metadata, Long> {

    List<Metadata> findAll();

    Metadata findById(int id);

    Metadata findByFilename(String filename);

    Metadata findByFolderAndFilename(String folder, String filename);

    List<Metadata> findByFolder(String folder);

    @Transactional
    long deleteById(int id);
}
