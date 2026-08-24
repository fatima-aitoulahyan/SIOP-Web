package com.example.backend_siop.common.util;

import com.example.backend_siop.common.exception.BusinessRuleException;
import io.minio.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class FileStorageUtil {

  private final MinioClient minioClient;
  private final MinioClient minioPresignClient;

  @Value("${minio.bucket-name}")
  private String bucketName;

  public FileStorageUtil(
    @Qualifier("minioClient") MinioClient minioClient,
    @Qualifier("minioPresignClient") MinioClient minioPresignClient
  ) {
    this.minioClient = minioClient;
    this.minioPresignClient = minioPresignClient;
  }

  public String store(MultipartFile fichier, String dossier) {
    assurerBucketExiste();

    String extension = extraireExtension(fichier.getOriginalFilename());
    String nomUnique = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
    String cheminObjet = dossier + "/" + nomUnique;

    try (InputStream is = fichier.getInputStream()) {
      minioClient.putObject(
        PutObjectArgs.builder()
          .bucket(bucketName)
          .object(cheminObjet)
          .stream(is, fichier.getSize(), -1)
          .contentType(fichier.getContentType())
          .build()
      );
    } catch (Exception e) {
      throw new BusinessRuleException("Erreur lors de l'upload du fichier : " + e.getMessage());
    }

    return cheminObjet;
  }

  public String storeFromClasspath(String cheminClasspath, String dossier) {
    assurerBucketExiste();

    try (InputStream is = getClass().getClassLoader().getResourceAsStream(cheminClasspath)) {
      if (is == null) {
        throw new BusinessRuleException("Ressource introuvable dans le classpath : " + cheminClasspath);
      }

      byte[] contenu = is.readAllBytes();
      String extension = extraireExtension(cheminClasspath);
      String nomUnique = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
      String cheminObjet = dossier + "/" + nomUnique;
      String contentType = URLConnection.guessContentTypeFromName(cheminClasspath);

      try (InputStream copie = new ByteArrayInputStream(contenu)) {
        minioClient.putObject(
          PutObjectArgs.builder()
            .bucket(bucketName)
            .object(cheminObjet)
            .stream(copie, contenu.length, -1)
            .contentType(contentType != null ? contentType : "application/octet-stream")
            .build()
        );
      }

      return cheminObjet;
    } catch (Exception e) {
      throw new BusinessRuleException("Erreur lors de l'upload depuis le classpath : " + e.getMessage());
    }
  }

  public String copier(String cheminSource, String dossierDestination) {
    try {
      String extension = extraireExtension(cheminSource);
      String nomUnique = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
      String cheminDestination = dossierDestination + "/" + nomUnique;

      minioClient.copyObject(
        CopyObjectArgs.builder()
          .bucket(bucketName)
          .object(cheminDestination)
          .source(CopySource.builder()
            .bucket(bucketName)
            .object(cheminSource)
            .build())
          .build()
      );

      return cheminDestination;
    } catch (Exception e) {
      throw new BusinessRuleException("Erreur lors de la copie du fichier : " + e.getMessage());
    }
  }

  /**
   * Génère une URL présignée destinée au NAVIGATEUR de l'utilisateur.
   * Utilise le client public (endpoint résolvable depuis internet),
   * contrairement aux autres méthodes qui utilisent le client interne Docker.
   */
  public String getUrlTemporaire(String cheminObjet) {
    try {
      return minioPresignClient.getPresignedObjectUrl(
        GetPresignedObjectUrlArgs.builder()
          .method(io.minio.http.Method.GET)
          .bucket(bucketName)
          .object(cheminObjet)
          .expiry(1, TimeUnit.HOURS)
          .build()
      );
    } catch (Exception e) {
      throw new BusinessRuleException("Erreur lors de la génération de l'URL : " + e.getMessage());
    }
  }

  public void delete(String cheminObjet) {
    try {
      minioClient.removeObject(
        RemoveObjectArgs.builder()
          .bucket(bucketName)
          .object(cheminObjet)
          .build()
      );
    } catch (Exception e) {
      throw new BusinessRuleException("Erreur lors de la suppression du fichier : " + e.getMessage());
    }
  }

  public InputStream download(String cheminObjet) {
    try {
      return minioClient.getObject(
        GetObjectArgs.builder()
          .bucket(bucketName)
          .object(cheminObjet)
          .build()
      );
    } catch (Exception e) {
      throw new BusinessRuleException("Erreur lors du téléchargement du fichier : " + e.getMessage());
    }
  }

  private void assurerBucketExiste() {
    try {
      boolean exists = minioClient.bucketExists(
        BucketExistsArgs.builder().bucket(bucketName).build());
      if (!exists) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
      }
    } catch (Exception e) {
      throw new BusinessRuleException("Erreur lors de la vérification/création du bucket : " + e.getMessage());
    }
  }

  private String extraireExtension(String nomFichier) {
    if (nomFichier == null || !nomFichier.contains(".")) return "";
    return nomFichier.substring(nomFichier.lastIndexOf('.') + 1);
  }
}
