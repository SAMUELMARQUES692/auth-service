package dev.samuel.auth_service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class RsaKeyConfig {

    @Value("${security.jwt.private-key}")
    private Resource privateKeyResource;

    @Value("${security.jwt.public-key}")
    private Resource publicKeyResource;

    @Bean
    public RSAPrivateKey rsaPrivateKey() throws Exception {
        String conteudo = lerChave(privateKeyResource, "PRIVATE KEY");
        byte[] bytes = Base64.getDecoder().decode(conteudo);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        String conteudo = lerChave(publicKeyResource, "PUBLIC KEY");
        byte[] bytes = Base64.getDecoder().decode(conteudo);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    private String lerChave(Resource resource, String tipo) throws Exception {
        try (InputStream is = resource.getInputStream()) {
            String conteudo = new String(is.readAllBytes());
            return conteudo
                    .replace("-----BEGIN " + tipo + "-----", "")
                    .replace("-----END " + tipo + "-----", "")
                    .replaceAll("\\s", "");
        }
    }
}