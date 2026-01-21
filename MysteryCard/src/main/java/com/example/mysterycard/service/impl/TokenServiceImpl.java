package com.example.mysterycard.service.impl;

import com.example.mysterycard.entity.Users;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.repository.UsersRepo;
import com.example.mysterycard.service.TokenService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    @Value("${jwt.secret}")
    private  String SECRET_KEY;
    @Value("${jwt.expiration}")
    private Long EXPIRATION_TIME;
    private final UsersRepo usersRepo;
    @Override
    public String generateToken(Users user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("email", user.getEmail())
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("tokenVersion", user.getTokenVersion())
                .claim("scope", getScope(user))
                .subject(user.getEmail())
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        jwsObject.sign(new MACSigner(SECRET_KEY.getBytes(StandardCharsets.UTF_8)));
        return jwsObject.serialize();
    }

    @Override
    public boolean validateToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        SignedJWT signedJWT = SignedJWT.parse(token);
        String email = signedJWT.getJWTClaimsSet().getSubject();
        int tokenVersion = signedJWT.getJWTClaimsSet().getIntegerClaim("tokenVersion");
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        Users users = usersRepo.findByEmail(email);
        if(users == null)
        {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
       boolean verified = signedJWT.verify(verifier);
        return verified && users.getTokenVersion() == tokenVersion && expirationTime.after(new Date());
    }

    public String getScope(Users user) {
        StringJoiner scope = new StringJoiner(" ");
        if(user.getRolelist() != null)
        {
            user.getRolelist().forEach(role -> {

                scope.add("ROLE_"+role.getRoleCode());
                if(role.getPermisions() != null && role.getPermisions().size() > 0)
                {
                    role.getPermisions().forEach(privilege -> scope.add(privilege.getPermisionCode()));
                }
            });

        }
        return scope.toString().trim();
    }
}
