package net.raumzeitfalle.docdrop.storage;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.resteasy.reactive.multipart.FileUpload;

import net.raumzeitfalle.docdrop.Configuration;

public record Artifact(String groupId,
                       String artifactName,
                       String version,
                       LocalDateTime dateTime,
                       String sourceFileName,
                       Path file,
                       Path artifactsDirectory) {
    
    private static final Logger LOG = Logger.getLogger(Artifact.class.getName());
    
    private static final DateTimeFormatter SNAPSHOT_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public String snapshot() {
        return SNAPSHOT_FORMAT.format(dateTime);
    }
    
    public static Artifact prepare(Configuration config, 
                                   ArtifactUploadInput input, 
                                   LocalDateTime timestamp,
                                   FileUpload upload,
                                   Path file) {
        LOG.log(Level.INFO, "Generating Artifact from upload: {0}", upload.fileName());
        return new Artifact(sanitize(input.group), 
                            sanitize(input.artifact), 
                            sanitize(input.version),
                            timestamp,
                            upload.fileName(),
                            file, 
                            config.getArtifactsDirectory());
    }
    
    public static Path prepareSnapshot(Configuration config, Path source, LocalDateTime dateTime) {
        String fileName = SNAPSHOT_FORMAT.format(dateTime)+"_"+source.getFileName();
        return config.getIngestDirectory().resolve(fileName);
    }
    
    public static Path prepareSnapshot(Configuration config, FileUpload input) {
        return config.getIngestDirectory().resolve(input.filePath().getFileName());
    }
    
    public static String sanitize(String source) {
        
        char[] goodChars = new char[source.length()];
        int good = 0;
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (Character.isAlphabetic(c) 
                || Character.isDigit(c)
                || '.' == c
                || '-' == c
                || '_' == c
                || '+' == c) {
                goodChars[good] = c;
                good++;
            }
        }
        
        char[] newString = new char[good];
        System.arraycopy(goodChars, 0, newString, 0, good);
        String noSpecials = String.valueOf(newString);

        while (noSpecials.contains("__") 
                || noSpecials.contains("..") 
                || noSpecials.contains("--")
                || noSpecials.contains("++")) {
            noSpecials = noSpecials.replace("__", "_")
                                   .replace("--", "-")
                                   .replace("++", "+")
                                   .replace("..", ".");
        }
        return noSpecials;
    }

}
