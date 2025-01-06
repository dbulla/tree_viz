package com.nurflugel.dependencyvisualizer.enums;

import com.nurflugel.dependencyvisualizer.io.readers.DataFileReader;
import com.nurflugel.dependencyvisualizer.io.readers.JsonFileReader;
import com.nurflugel.dependencyvisualizer.io.readers.TextFileReader;
import com.nurflugel.dependencyvisualizer.io.writers.DataFileWriter;
import com.nurflugel.dependencyvisualizer.io.writers.JsonFileWriter;
import com.nurflugel.dependencyvisualizer.io.writers.TextFileWriter;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.stream.Stream;

/** What type of text file is this? */
public enum FileType {
  JSON(".json", JsonFileReader.class, JsonFileWriter.class),
  TXT (".txt", TextFileReader.class, TextFileWriter.class);

  @Getter
  private final String                          extension;
  private final Class<? extends DataFileReader> fileReaderClass;
  private final Class<? extends DataFileWriter> fileWriterClass;

  FileType(String extension, Class<? extends DataFileReader> fileReaderClass, Class<? extends DataFileWriter> fileWriterClass) {
    this.extension       = extension;
    this.fileReaderClass = fileReaderClass;
    this.fileWriterClass = fileWriterClass;
  }

    public static FileType findByExtension(String text) {
    String fullText = '.' + text;

    return Stream.of(values())
                 .filter(v -> v.getExtension().equals(fullText))
                 .findAny().orElseThrow(RuntimeException::new);
  }

  public String getDotPath(String absolutePath) { return StringUtils.replace(absolutePath, extension, ".dot"); }

  public DataFileReader getDataFileReader(File sourceDataFile) throws IllegalAccessException, InstantiationException {
    DataFileReader reader = fileReaderClass.newInstance();

    reader.setSourceDataFile(sourceDataFile);

    return reader;
  }

  public DataFileWriter getDataFileWriter(File sourceDataFile) throws IllegalAccessException, InstantiationException {
    DataFileWriter reader = fileWriterClass.newInstance();

    return reader;
  }
}
