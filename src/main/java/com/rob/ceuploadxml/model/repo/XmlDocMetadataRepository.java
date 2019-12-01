package com.rob.ceuploadxml.model.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rob.ceuploadxml.model.XmlDocMetadata;

@Repository
public interface XmlDocMetadataRepository extends CrudRepository<XmlDocMetadata, Integer> {

}
