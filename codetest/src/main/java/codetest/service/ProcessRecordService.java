package codetest.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import codetest.domain.DomainObject;

/** An interface to read all the records from certain CSV file. */
public interface ProcessRecordService<T extends DomainObject> {

	List<T> processFile(File file) throws IOException;
}
