
package br.unit.aula11Upload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/")
public class ArquivoController {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void recebeArquivo(
            @RequestPart("nome") String nome,
            @RequestPart("email") String email,
            @RequestPart("telefone") String telefone,
            @RequestPart("document") MultipartFile arquivoRecebido) {

        PessoaDTO pessoa = new PessoaDTO(nome, email, telefone);

        System.out.println("MIME: " + arquivoRecebido.getContentType());
        System.out.println("Tamanho: " + arquivoRecebido.getSize());

        // Verificar se o arquivo está vazio
        if (arquivoRecebido.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O arquivo não pode estar vazio."
            );
        }

        // Verificar o tipo do arquivo
        if (!"image/jpeg".equalsIgnoreCase(
                arquivoRecebido.getContentType())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Apenas arquivos JPEG ou JPG são permitidos."
            );
        }

        String nomeArquivo = arquivoRecebido.getOriginalFilename();

        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nome do arquivo inválido."
            );
        }

        nomeArquivo = nomeArquivo.replace("\\", "/");
        nomeArquivo = nomeArquivo.substring(
                nomeArquivo.lastIndexOf("/") + 1
        );

        String extensao = nomeArquivo.toLowerCase();

        if (!extensao.endsWith(".jpg")
                && !extensao.endsWith(".jpeg")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Apenas arquivos JPG ou JPEG são permitidos."
            );
        }

        if (arquivoRecebido.getSize() > 500 * 1024) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O arquivo não pode ultrapassar 500 KB."
            );
        }

        Path pastaDocumentos = Paths.get(
                System.getProperty("user.home"),
                "Documents"
        );

        try {
            Files.createDirectories(pastaDocumentos);

            Path arquivoDisco = pastaDocumentos.resolve(nomeArquivo);

            Files.copy(
                    arquivoRecebido.getInputStream(),
                    arquivoDisco,
                    StandardCopyOption.REPLACE_EXISTING
            );

            System.out.println(
                    "Arquivo salvo em: " + arquivoDisco.toAbsolutePath()
            );

        } catch (IOException e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao salvar o arquivo.",
                    e
            );
        }
    }
}
