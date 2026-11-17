package br.unit.aula11Upload;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/download")
public class DownloadController {
  @GetMapping("/{id}")
  public ResponseEntity<List<String>> listarArquivos(@PathVariable String id)
          throws IOException {

    Path pasta = Paths.get("uploads").resolve(id);

    if (!Files.exists(pasta)) {
      return ResponseEntity.ok(Collections.emptyList());
    }

    List<String> urls = Files.list(pasta)
            .filter(Files::isRegularFile)
            .map(path -> "http://localhost:8080/download/"
                    + id + "/"
                    + path.getFileName())
            .toList();

    return ResponseEntity.ok(urls);
  }

  @GetMapping("/{id}/{arquivo}")
  public ResponseEntity<Resource> baixar(
          @PathVariable String id,
          @PathVariable String arquivo) throws IOException {

    Path caminho = Paths.get("uploads")
            .resolve(id)
            .resolve(arquivo);

    Resource resource = new UrlResource(caminho.toUri());

    if (!resource.exists()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
  }

}
